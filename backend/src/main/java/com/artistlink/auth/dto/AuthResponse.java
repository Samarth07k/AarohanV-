package com.artistlink.auth.dto;

import com.artistlink.common.AuthorType;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        ActorInfo actor
) {
    public record ActorInfo(UUID userId, AuthorType authorType, UUID authorId,
                            String email, String displayName, String avatarUrl) {}
}
