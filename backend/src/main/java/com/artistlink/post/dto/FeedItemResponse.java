package com.artistlink.post.dto;

import com.artistlink.common.AuthorType;

import java.util.List;

/** Read-time denormalized feed projection — Blueprint 7.5 / 10.8. */
public record FeedItemResponse(
        PostResponse post,
        AuthorInfo author,
        List<MediaResponse> mediaAttachments,
        boolean isLikedByCurrentUser,
        boolean isFollowingAuthor
) {
    public record AuthorInfo(AuthorType type, java.util.UUID id, String displayName, String avatarUrl) {}
}
