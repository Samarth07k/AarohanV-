package com.artistlink.messaging;

import com.artistlink.artist.Artist;
import com.artistlink.artist.ArtistRepository;
import com.artistlink.auth.AuthPrincipal;
import com.artistlink.common.AuthorType;
import com.artistlink.common.exception.ForbiddenException;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.messaging.dto.ConversationResponse;
import com.artistlink.messaging.dto.MessageResponse;
import com.artistlink.notification.NotificationService;
import com.artistlink.notification.NotificationType;
import com.artistlink.notification.RelatedEntityType;
import com.artistlink.user.UserRole;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final NotificationService notifications;

    public MessagingService(ConversationRepository conversationRepository,
                            MessageRepository messageRepository,
                            ArtistRepository artistRepository,
                            VenueRepository venueRepository,
                            NotificationService notifications) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> myConversations(AuthPrincipal actor) {
        List<Conversation> convos = actor.authorType() == AuthorType.ARTIST
                ? conversationRepository.findByArtistIdOrderByUpdatedAtDesc(actor.authorId())
                : conversationRepository.findByVenueIdOrderByUpdatedAtDesc(actor.authorId());
        return convos.stream().map(this::toConversationResponse).toList();
    }

    @Transactional(readOnly = true)
    public ConversationResponse getConversation(AuthPrincipal actor, UUID id) {
        return toConversationResponse(requireParticipant(actor, id));
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> messages(AuthPrincipal actor, UUID conversationId) {
        requireParticipant(actor, conversationId);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream().map(MessageResponse::from).toList();
    }

    /** Send a message. Messaging is Artist <-> Venue only, and only between booking parties. */
    @Transactional
    public MessageResponse send(AuthPrincipal actor, UUID conversationId, String content) {
        Conversation c = requireParticipant(actor, conversationId);

        Message m = new Message();
        m.setConversationId(conversationId);
        m.setSenderType(actor.authorType() == AuthorType.ARTIST
                ? MessageSenderType.ARTIST : MessageSenderType.VENUE);
        m.setSenderId(actor.authorId());
        m.setContent(content);
        m = messageRepository.save(m);

        // Bump conversation ordering
        conversationRepository.save(c);

        // Notify the recipient (the other party)
        boolean senderIsArtist = actor.authorType() == AuthorType.ARTIST;
        UserRole recipientType = senderIsArtist ? UserRole.VENUE : UserRole.ARTIST;
        UUID recipientId = senderIsArtist ? c.getVenueId() : c.getArtistId();
        String senderName = senderIsArtist
                ? artistRepository.findById(c.getArtistId()).map(Artist::getDisplayName).orElse("Artist")
                : venueRepository.findById(c.getVenueId()).map(Venue::getDisplayName).orElse("Venue");
        notifications.emit(recipientType, recipientId,
                NotificationType.NEW_MESSAGE, RelatedEntityType.MESSAGE, m.getId(),
                "New message", senderName + ": " + preview(content));

        return MessageResponse.from(m);
    }

    @Transactional
    public void markRead(AuthPrincipal actor, UUID conversationId) {
        requireParticipant(actor, conversationId);
        MessageSenderType me = actor.authorType() == AuthorType.ARTIST
                ? MessageSenderType.ARTIST : MessageSenderType.VENUE;
        messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).forEach(m -> {
            // mark messages from the OTHER party as read
            if (m.getSenderType() != me && m.getReadAt() == null) {
                m.setReadAt(Instant.now());
                messageRepository.save(m);
            }
        });
    }

    private Conversation requireParticipant(AuthPrincipal actor, UUID conversationId) {
        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NotFoundException("Conversation not found"));
        boolean ok = (actor.authorType() == AuthorType.ARTIST && c.getArtistId().equals(actor.authorId()))
                || (actor.authorType() == AuthorType.VENUE && c.getVenueId().equals(actor.authorId()));
        if (!ok) {
            throw new ForbiddenException("You are not a participant in this conversation");
        }
        return c;
    }

    private ConversationResponse toConversationResponse(Conversation c) {
        Artist artist = artistRepository.findById(c.getArtistId()).orElse(null);
        Venue venue = venueRepository.findById(c.getVenueId()).orElse(null);
        List<Message> msgs = messageRepository.findByConversationIdOrderByCreatedAtAsc(c.getId());
        String last = msgs.isEmpty() ? null : msgs.get(msgs.size() - 1).getContent();
        return new ConversationResponse(
                c.getId(), c.getBookingId(),
                c.getArtistId(), artist == null ? "Unknown Artist" : artist.getDisplayName(),
                artist == null ? null : artist.getAvatarUrl(),
                c.getVenueId(), venue == null ? "Unknown Venue" : venue.getDisplayName(),
                venue == null ? null : venue.getAvatarUrl(),
                last, c.getUpdatedAt());
    }

    private String preview(String content) {
        return content.length() <= 60 ? content : content.substring(0, 57) + "…";
    }
}
