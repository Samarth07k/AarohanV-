package com.artistlink.notification.dto;

import com.artistlink.notification.Notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String relatedEntityType,
        UUID relatedEntityId,
        String title,
        String body,
        int count,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getType().name(),
                n.getRelatedEntityType() == null ? null : n.getRelatedEntityType().name(),
                n.getRelatedEntityId(), n.getTitle(), n.getBody(), n.getCount(),
                n.getReadAt() != null, n.getCreatedAt());
    }
}
