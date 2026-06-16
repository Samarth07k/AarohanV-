package com.artistlink.post.dto;

import com.artistlink.media.MediaAttachment;

import java.time.Instant;
import java.util.UUID;

public record MediaResponse(
        UUID id,
        UUID postId,
        String mediaType,
        String url,
        String thumbnailUrl,
        Integer width,
        Integer height,
        Integer durationSeconds,
        int displayOrder,
        Instant createdAt
) {
    public static MediaResponse from(MediaAttachment m) {
        return new MediaResponse(
                m.getId(), m.getPostId(), m.getMediaType().name(), m.getUrl(),
                m.getThumbnailUrl(), m.getWidth(), m.getHeight(),
                m.getDurationSeconds(), m.getDisplayOrder(), m.getCreatedAt());
    }
}
