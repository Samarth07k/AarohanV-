package com.artistlink.application;

import com.artistlink.application.dto.ApplicationResponse;
import com.artistlink.application.dto.CreateApplicationRequest;
import com.artistlink.artist.Artist;
import com.artistlink.artist.ArtistRepository;
import com.artistlink.auth.AuthPrincipal;
import com.artistlink.common.AuthorType;
import com.artistlink.common.exception.BadRequestException;
import com.artistlink.common.exception.ConflictException;
import com.artistlink.common.exception.ForbiddenException;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.negotiation.Negotiation;
import com.artistlink.negotiation.NegotiationRepository;
import com.artistlink.notification.NotificationService;
import com.artistlink.notification.NotificationType;
import com.artistlink.notification.RelatedEntityType;
import com.artistlink.opportunity.Opportunity;
import com.artistlink.opportunity.OpportunityRepository;
import com.artistlink.opportunity.OpportunityStatus;
import com.artistlink.user.UserRole;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final NegotiationRepository negotiationRepository;
    private final NotificationService notifications;

    public ApplicationService(ApplicationRepository applicationRepository,
                              OpportunityRepository opportunityRepository,
                              ArtistRepository artistRepository,
                              VenueRepository venueRepository,
                              NegotiationRepository negotiationRepository,
                              NotificationService notifications) {
        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.negotiationRepository = negotiationRepository;
        this.notifications = notifications;
    }

    /* ---- Artist submits an application ---- */
    @Transactional
    public ApplicationResponse submit(AuthPrincipal actor, CreateApplicationRequest req) {
        if (actor.authorType() != AuthorType.ARTIST) {
            throw new ForbiddenException("Only artists can apply");
        }
        Opportunity opp = opportunityRepository.findById(req.opportunityId())
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));
        if (opp.getStatus() != OpportunityStatus.OPEN) {
            throw new BadRequestException("This opportunity is not open for applications");
        }
        if (applicationRepository.existsByOpportunityIdAndArtistId(opp.getId(), actor.authorId())) {
            throw new ConflictException("You have already applied to this opportunity");
        }

        Application a = new Application();
        a.setOpportunityId(opp.getId());
        a.setArtistId(actor.authorId());
        a.setCoverMessage(req.coverMessage() == null ? "" : req.coverMessage());
        a = applicationRepository.save(a);

        String artistName = artistName(actor.authorId());
        notifications.emit(UserRole.VENUE, opp.getVenueId(),
                NotificationType.NEW_APPLICATION, RelatedEntityType.APPLICATION, a.getId(),
                "New application", artistName + " applied to \"" + opp.getTitle() + "\"");

        return toResponse(a);
    }

    /* ---- Artist withdraws ---- */
    @Transactional
    public ApplicationResponse withdraw(AuthPrincipal actor, UUID id) {
        Application a = requireApplication(id);
        if (actor.authorType() != AuthorType.ARTIST || !a.getArtistId().equals(actor.authorId())) {
            throw new ForbiddenException("Only the applying artist can withdraw");
        }
        a.setStatus(ApplicationStatus.WITHDRAWN);
        a = applicationRepository.save(a);
        return toResponse(a);
    }

    /* ---- Venue reviews: PENDING -> REVIEWING/ACCEPTED/REJECTED ---- */
    @Transactional
    public ApplicationResponse review(AuthPrincipal actor, UUID id, ApplicationStatus newStatus) {
        Application a = requireApplication(id);
        Opportunity opp = opportunityRepository.findById(a.getOpportunityId())
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));
        if (actor.authorType() != AuthorType.VENUE || !opp.getVenueId().equals(actor.authorId())) {
            throw new ForbiddenException("Only the owning venue can review applications");
        }
        if (newStatus == ApplicationStatus.WITHDRAWN) {
            throw new BadRequestException("A venue cannot withdraw an application");
        }
        a.setStatus(newStatus);
        a = applicationRepository.save(a);

        // Notify artist of the status change
        notifications.emit(UserRole.ARTIST, a.getArtistId(),
                NotificationType.APPLICATION_STATUS_CHANGED, RelatedEntityType.APPLICATION, a.getId(),
                "Application " + newStatus.name().toLowerCase(),
                "Your application to \"" + opp.getTitle() + "\" is now " + newStatus.name().toLowerCase());

        // Accepting opens a negotiation thread (idempotent)
    final Opportunity finalOpp = opp;
    final Application finalApplication = a;

    if (newStatus == ApplicationStatus.ACCEPTED) {
        negotiationRepository.findByApplicationId(finalApplication.getId()).orElseGet(() -> {
        Negotiation n = new Negotiation();
        n.setApplicationId(finalApplication.getId());

        Negotiation saved = negotiationRepository.save(n);

        notifications.emit(
                UserRole.ARTIST,
                finalApplication.getArtistId(),
                NotificationType.APPLICATION_STATUS_CHANGED,
                RelatedEntityType.NEGOTIATION,
                saved.getId(),
                "Negotiation opened",
                "A negotiation has opened for \"" + finalOpp.getTitle() + "\""
        );

        return saved;
    });
}

    return toResponse(a);
}

    /* ---- Venue: applications received for an opportunity ---- */
    @Transactional(readOnly = true)
    public List<ApplicationResponse> forOpportunity(AuthPrincipal actor, UUID opportunityId) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));
        if (actor.authorType() != AuthorType.VENUE || !opp.getVenueId().equals(actor.authorId())) {
            throw new ForbiddenException("Only the owning venue can view these applications");
        }
        return applicationRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId)
                .stream().map(this::toResponse).toList();
    }

    /* ---- Venue: all applications across my opportunities ---- */
    @Transactional(readOnly = true)
    public List<ApplicationResponse> receivedByVenue(AuthPrincipal actor) {
        if (actor.authorType() != AuthorType.VENUE) {
            throw new ForbiddenException("Only venues have received applications");
        }
        List<UUID> oppIds = opportunityRepository
                .findByVenueIdOrderByCreatedAtDesc(actor.authorId())
                .stream().map(Opportunity::getId).toList();
        return oppIds.stream()
                .flatMap(oid -> applicationRepository.findByOpportunityIdOrderByCreatedAtDesc(oid).stream())
                .map(this::toResponse)
                .toList();
    }

    /* ---- Artist: my applications ---- */
    @Transactional(readOnly = true)
    public List<ApplicationResponse> mine(AuthPrincipal actor) {
        if (actor.authorType() != AuthorType.ARTIST) {
            throw new ForbiddenException("Only artists have applications");
        }
        return applicationRepository.findByArtistIdOrderByCreatedAtDesc(actor.authorId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ApplicationResponse get(AuthPrincipal actor, UUID id) {
        Application a = requireApplication(id);
        Opportunity opp = opportunityRepository.findById(a.getOpportunityId()).orElse(null);
        boolean isArtist = actor.authorType() == AuthorType.ARTIST && a.getArtistId().equals(actor.authorId());
        boolean isVenue = opp != null && actor.authorType() == AuthorType.VENUE
                && opp.getVenueId().equals(actor.authorId());
        if (!isArtist && !isVenue) {
            throw new ForbiddenException("Not authorized to view this application");
        }
        return toResponse(a);
    }

    /* ---- helpers ---- */
    private Application requireApplication(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    private ApplicationResponse toResponse(Application a) {
        Opportunity opp = opportunityRepository.findById(a.getOpportunityId()).orElse(null);
        String oppTitle = opp == null ? "Unknown" : opp.getTitle();
        UUID venueId = opp == null ? null : opp.getVenueId();
        String venueName = venueId == null ? "Unknown Venue"
                : venueRepository.findById(venueId).map(Venue::getDisplayName).orElse("Unknown Venue");
        Artist artist = artistRepository.findById(a.getArtistId()).orElse(null);
        String artistName = artist == null ? "Unknown Artist" : artist.getDisplayName();
        String artistAvatar = artist == null ? null : artist.getAvatarUrl();
        UUID negotiationId = negotiationRepository.findByApplicationId(a.getId())
                .map(Negotiation::getId).orElse(null);
        return ApplicationResponse.of(a, oppTitle, venueId, venueName, artistName, artistAvatar, negotiationId);
    }

    private String artistName(UUID artistId) {
        return artistRepository.findById(artistId).map(Artist::getDisplayName).orElse("An artist");
    }
}
