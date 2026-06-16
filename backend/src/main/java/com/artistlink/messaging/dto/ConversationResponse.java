package com.artistlink.messaging.dto;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        UUID bookingId,
        UUID artistId,
        String artistName,
        String artistAvatarUrl,
        UUID venueId,
        String venueName,
        String venueAvatarUrl,
        String lastMessage,
        Instant updatedAt
) {}
