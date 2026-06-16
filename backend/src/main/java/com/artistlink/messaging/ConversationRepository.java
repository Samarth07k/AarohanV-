package com.artistlink.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByBookingId(UUID bookingId);
    List<Conversation> findByArtistIdOrderByUpdatedAtDesc(UUID artistId);
    List<Conversation> findByVenueIdOrderByUpdatedAtDesc(UUID venueId);
}
