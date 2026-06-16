package com.artistlink.negotiation;

import com.artistlink.application.Application;
import com.artistlink.application.ApplicationRepository;
import com.artistlink.application.ApplicationStatus;
import com.artistlink.artist.Artist;
import com.artistlink.artist.ArtistRepository;
import com.artistlink.auth.AuthPrincipal;
import com.artistlink.booking.Booking;
import com.artistlink.booking.BookingRepository;
import com.artistlink.common.AuthorType;
import com.artistlink.common.exception.BadRequestException;
import com.artistlink.common.exception.ForbiddenException;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.messaging.Conversation;
import com.artistlink.messaging.ConversationRepository;
import com.artistlink.negotiation.dto.NegotiationResponse;
import com.artistlink.negotiation.dto.OfferResponse;
import com.artistlink.negotiation.dto.SendOfferRequest;
import com.artistlink.notification.NotificationService;
import com.artistlink.notification.NotificationType;
import com.artistlink.notification.RelatedEntityType;
import com.artistlink.opportunity.Opportunity;
import com.artistlink.opportunity.OpportunityRepository;
import com.artistlink.user.UserRole;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final NegotiationOfferRepository offerRepository;
    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final ConversationRepository conversationRepository;
    private final NotificationService notifications;

    public NegotiationService(NegotiationRepository negotiationRepository,
                              NegotiationOfferRepository offerRepository,
                              ApplicationRepository applicationRepository,
                              OpportunityRepository opportunityRepository,
                              ArtistRepository artistRepository,
                              VenueRepository venueRepository,
                              BookingRepository bookingRepository,
                              ConversationRepository conversationRepository,
                              NotificationService notifications) {
        this.negotiationRepository = negotiationRepository;
        this.offerRepository = offerRepository;
        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.bookingRepository = bookingRepository;
        this.conversationRepository = conversationRepository;
        this.notifications = notifications;
    }

    /** Internal holder resolving both parties of a negotiation. */
    private record Parties(Negotiation negotiation, Application application, Opportunity opportunity,
                           UUID artistId, UUID venueId) {}

    private Parties resolve(UUID negotiationId) {
        Negotiation n = negotiationRepository.findById(negotiationId)
                .orElseThrow(() -> new NotFoundException("Negotiation not found"));
        Application a = applicationRepository.findById(n.getApplicationId())
                .orElseThrow(() -> new NotFoundException("Application not found"));
        Opportunity o = opportunityRepository.findById(a.getOpportunityId())
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));
        return new Parties(n, a, o, a.getArtistId(), o.getVenueId());
    }

    private OfferParty requireParticipant(AuthPrincipal actor, Parties p) {
        if (actor.authorType() == AuthorType.ARTIST && p.artistId().equals(actor.authorId())) {
            return OfferParty.ARTIST;
        }
        if (actor.authorType() == AuthorType.VENUE && p.venueId().equals(actor.authorId())) {
            return OfferParty.VENUE;
        }
        throw new ForbiddenException("You are not a participant in this negotiation");
    }

    @Transactional(readOnly = true)
    public NegotiationResponse get(AuthPrincipal actor, UUID negotiationId) {
        Parties p = resolve(negotiationId);
        requireParticipant(actor, p);
        return toResponse(p);
    }

    /** Look up the negotiation tied to an application (for artist/venue deep links). */
    @Transactional(readOnly = true)
    public NegotiationResponse getByApplication(AuthPrincipal actor, UUID applicationId) {
        Negotiation n = negotiationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new NotFoundException("No negotiation for this application"));
        return get(actor, n.getId());
    }

    /** Either party sends a counter-offer. */
    @Transactional
    public NegotiationResponse sendOffer(AuthPrincipal actor, UUID negotiationId, SendOfferRequest req) {
        Parties p = resolve(negotiationId);
        OfferParty party = requireParticipant(actor, p);
        if (p.negotiation().getStatus() != NegotiationStatus.OPEN) {
            throw new BadRequestException("This negotiation is no longer open");
        }
        NegotiationOffer offer = new NegotiationOffer();
        offer.setNegotiationId(negotiationId);
        offer.setOfferedBy(party);
        offer.setAmount(req.amount());
        offer.setTerms(req.terms() == null ? "" : req.terms());
        offerRepository.save(offer);

        // Notify the counterparty
        if (party == OfferParty.VENUE) {
            notifications.emit(UserRole.ARTIST, p.artistId(),
                    NotificationType.NEGOTIATION_OFFER_RECEIVED, RelatedEntityType.NEGOTIATION, negotiationId,
                    "New offer received", venueName(p.venueId()) + " sent an offer of " + req.amount());
        } else {
            notifications.emit(UserRole.VENUE, p.venueId(),
                    NotificationType.NEGOTIATION_OFFER_RECEIVED, RelatedEntityType.NEGOTIATION, negotiationId,
                    "New offer received", artistName(p.artistId()) + " sent an offer of " + req.amount());
        }
        return toResponse(p);
    }

    /**
     * Accept the latest offer → negotiation AGREED, application ACCEPTED, Booking created,
     * Conversation unlocked (Artist ↔ Venue). The party accepting must not be the one who
     * made the latest offer.
     */
    @Transactional
    public NegotiationResponse accept(AuthPrincipal actor, UUID negotiationId) {
        Parties p = resolve(negotiationId);
        OfferParty party = requireParticipant(actor, p);
        Negotiation n = p.negotiation();
        if (n.getStatus() != NegotiationStatus.OPEN) {
            throw new BadRequestException("This negotiation is no longer open");
        }
        List<NegotiationOffer> offers = offerRepository.findByNegotiationIdOrderByCreatedAtAsc(negotiationId);
        if (offers.isEmpty()) {
            throw new BadRequestException("There is no offer to accept yet");
        }
        NegotiationOffer latest = offers.get(offers.size() - 1);
        if (latest.getOfferedBy() == party) {
            throw new BadRequestException("You cannot accept your own offer; wait for the other party");
        }

        n.setStatus(NegotiationStatus.AGREED);
        negotiationRepository.save(n);

        Application application = p.application();
        application.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(application);

        // Create booking (idempotent on negotiation)
        Booking booking = bookingRepository.findByNegotiationId(negotiationId).orElseGet(() -> {
            Booking b = new Booking();
            b.setNegotiationId(negotiationId);
            b.setArtistId(p.artistId());
            b.setVenueId(p.venueId());
            b.setAgreedAmount(latest.getAmount());
            b.setEventDate(p.opportunity().getEventDate());
            return bookingRepository.save(b);
        });

        // Unlock conversation (Artist <-> Venue), idempotent on booking
        conversationRepository.findByBookingId(booking.getId()).orElseGet(() -> {
            Conversation c = new Conversation();
            c.setBookingId(booking.getId());
            c.setArtistId(p.artistId());
            c.setVenueId(p.venueId());
            return conversationRepository.save(c);
        });

        // Notify both parties
        notifications.emit(UserRole.ARTIST, p.artistId(),
                NotificationType.NEGOTIATION_AGREED, RelatedEntityType.BOOKING, booking.getId(),
                "Booking confirmed", "Your booking for \"" + p.opportunity().getTitle() + "\" is confirmed");
        notifications.emit(UserRole.VENUE, p.venueId(),
                NotificationType.BOOKING_CONFIRMED, RelatedEntityType.BOOKING, booking.getId(),
                "Booking confirmed", "Booking confirmed with " + artistName(p.artistId()));

        return toResponse(resolve(negotiationId));
    }

    /** Reject/decline the negotiation. */
    @Transactional
    public NegotiationResponse reject(AuthPrincipal actor, UUID negotiationId) {
        Parties p = resolve(negotiationId);
        requireParticipant(actor, p);
        Negotiation n = p.negotiation();
        if (n.getStatus() == NegotiationStatus.AGREED) {
            throw new BadRequestException("An agreed negotiation cannot be declined");
        }
        n.setStatus(NegotiationStatus.DECLINED);
        negotiationRepository.save(n);

        Application application = p.application();
        application.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(application);

        // Notify the other party
        if (actor.authorType() == AuthorType.VENUE) {
            notifications.emit(UserRole.ARTIST, p.artistId(),
                    NotificationType.APPLICATION_STATUS_CHANGED, RelatedEntityType.NEGOTIATION, negotiationId,
                    "Negotiation declined", "The negotiation for \"" + p.opportunity().getTitle() + "\" was declined");
        } else {
            notifications.emit(UserRole.VENUE, p.venueId(),
                    NotificationType.APPLICATION_STATUS_CHANGED, RelatedEntityType.NEGOTIATION, negotiationId,
                    "Negotiation declined", artistName(p.artistId()) + " declined the negotiation");
        }
        return toResponse(resolve(negotiationId));
    }

    private NegotiationResponse toResponse(Parties p) {
        List<OfferResponse> offers = offerRepository
                .findByNegotiationIdOrderByCreatedAtAsc(p.negotiation().getId())
                .stream().map(OfferResponse::from).toList();
        OfferResponse latest = offers.isEmpty() ? null : offers.get(offers.size() - 1);
        UUID bookingId = bookingRepository.findByNegotiationId(p.negotiation().getId())
                .map(Booking::getId).orElse(null);
        Artist artist = artistRepository.findById(p.artistId()).orElse(null);
        return new NegotiationResponse(
                p.negotiation().getId(), p.application().getId(),
                p.opportunity().getId(), p.opportunity().getTitle(),
                p.artistId(), artist == null ? "Unknown Artist" : artist.getDisplayName(),
                artist == null ? null : artist.getAvatarUrl(),
                p.venueId(), venueName(p.venueId()),
                p.negotiation().getStatus().name(),
                offers, latest, bookingId, p.negotiation().getCreatedAt());
    }

    private String artistName(UUID id) {
        return artistRepository.findById(id).map(Artist::getDisplayName).orElse("An artist");
    }

    private String venueName(UUID id) {
        return venueRepository.findById(id).map(Venue::getDisplayName).orElse("A venue");
    }
}
