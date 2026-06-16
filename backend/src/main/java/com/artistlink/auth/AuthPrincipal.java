package com.artistlink.auth;

import com.artistlink.common.AuthorType;

import java.util.UUID;

/**
 * The current actor, derived from the JWT (Blueprint 11).
 * The token carries sub (userId), authorType (ARTIST|VENUE), and authorId.
 * Identity is taken from the token, never trusted from the request body.
 */
public record AuthPrincipal(UUID userId, AuthorType authorType, UUID authorId, String email) {
}
