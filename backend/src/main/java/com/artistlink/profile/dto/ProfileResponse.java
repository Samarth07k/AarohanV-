package com.artistlink.profile.dto;

import com.artistlink.common.AuthorType;

import java.util.List;
import java.util.UUID;

/** Identity payload for GET /artists|venues/:id (Blueprint 14). */
public record ProfileResponse(
        AuthorType type,
        UUID id,
        String displayName,
        String bio,
        String location,
        String avatarUrl,
        String coverUrl,
        List<String> genres,    // artists only; empty for venues
        Integer capacity        // venues only; null for artists
) {}
