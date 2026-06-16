package com.artistlink.booking;

import com.artistlink.artist.Artist;
import com.artistlink.artist.ArtistRepository;
import com.artistlink.auth.AuthPrincipal;
import com.artistlink.booking.dto.BookingResponse;
import com.artistlink.common.AuthorType;
import com.artistlink.common.exception.BadRequestException;
import com.artistlink.common.exception.ForbiddenException;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.messaging.Conversation;
import com.artistlink.messaging.ConversationRepository;
import com.artistlink.notification.NotificationService;
import com.artistlink.notification.NotificationType;
import com.artistlink.notification.RelatedEntityType;
import com.artistlink.user.UserRole;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ConversationRepository conversationRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final NotificationService notifications;

    public BookingService(BookingRepository bookingRepository,
                          ConversationRepository conversationRepository,
                          ArtistRepository artistRepository,
                          VenueRepository venueRepository,
                          NotificationService notifications) {
        this.bookingRepository = bookingRepository;
        this.conversationRepository = conversationRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> mine(AuthPrincipal actor) {
        List<Booking> bookings = actor.authorType() == AuthorType.ARTIST
                ? bookingRepository.findByArtistIdOrderByCreatedAtDesc(actor.authorId())
                : bookingRepository.findByVenueIdOrderByCreatedAtDesc(actor.authorId());
        return bookings.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse get(AuthPrincipal actor, UUID id) {
        Booking b = requireParticipant(actor, id);
        return toResponse(b);
    }

    @Transactional
    public BookingResponse updateStatus(AuthPrincipal actor, UUID id, BookingStatus status) {
        Booking b = requireParticipant(actor, id);
        if (status == BookingStatus.CONFIRMED) {
            throw new BadRequestException("A booking is already confirmed on creation");
        }
        b.setStatus(status);
        b = bookingRepository.save(b);

        if (status == BookingStatus.CANCELLED) {
            // Notify the other party
            UserRole other = actor.authorType() == AuthorType.ARTIST ? UserRole.VENUE : UserRole.ARTIST;
            UUID otherId = actor.authorType() == AuthorType.ARTIST ? b.getVenueId() : b.getArtistId();
            notifications.emit(other, otherId,
                    NotificationType.BOOKING_CANCELLED, RelatedEntityType.BOOKING, b.getId(),
                    "Booking cancelled", "A booking has been cancelled");
        }
        return toResponse(b);
    }

    private Booking requireParticipant(AuthPrincipal actor, UUID id) {
        Booking b = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        boolean ok = (actor.authorType() == AuthorType.ARTIST && b.getArtistId().equals(actor.authorId()))
                || (actor.authorType() == AuthorType.VENUE && b.getVenueId().equals(actor.authorId()));
        if (!ok) {
            throw new ForbiddenException("Not authorized to view this booking");
        }
        return b;
    }

    private BookingResponse toResponse(Booking b) {
        Artist artist = artistRepository.findById(b.getArtistId()).orElse(null);
        Venue venue = venueRepository.findById(b.getVenueId()).orElse(null);
        UUID conversationId = conversationRepository.findByBookingId(b.getId())
                .map(Conversation::getId).orElse(null);
        return new BookingResponse(
                b.getId(), b.getNegotiationId(),
                b.getArtistId(), artist == null ? "Unknown Artist" : artist.getDisplayName(),
                artist == null ? null : artist.getAvatarUrl(),
                b.getVenueId(), venue == null ? "Unknown Venue" : venue.getDisplayName(),
                venue == null ? null : venue.getLocation(),
                b.getAgreedAmount(), b.getEventDate(), b.getStatus().name(),
                conversationId, b.getCreatedAt());
    }
}
