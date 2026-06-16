package com.artistlink.messaging.dto;

import com.artistlink.messaging.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID conversationId,
        String senderType,
        UUID senderId,
        String content,
        boolean read,
        Instant createdAt
) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(m.getId(), m.getConversationId(), m.getSenderType().name(),
                m.getSenderId(), m.getContent(), m.getReadAt() != null, m.getCreatedAt());
    }
}
