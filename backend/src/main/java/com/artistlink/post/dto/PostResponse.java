package com.artistlink.post.dto;

import com.artistlink.common.AuthorType;
import com.artistlink.post.Post;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        AuthorType authorType,
        UUID authorId,
        String postType,
        String content,
        String linkedEntityType,
        UUID linkedEntityId,
        int likeCount,
        int commentCount,
        String visibility,
        String status,
        List<MediaResponse> mediaAttachments,
        Instant createdAt,
        Instant updatedAt
) {
    public static PostResponse from(Post p, List<MediaResponse> media) {
        return new PostResponse(
                p.getId(), p.getAuthorType(), p.getAuthorId(),
                p.getPostType().name(), p.getContent(),
                p.getLinkedEntityType() == null ? null : p.getLinkedEntityType().name(),
                p.getLinkedEntityId(), p.getLikeCount(), p.getCommentCount(),
                p.getVisibility().name(), p.getStatus().name(),
                media, p.getCreatedAt(), p.getUpdatedAt());
    }
}
