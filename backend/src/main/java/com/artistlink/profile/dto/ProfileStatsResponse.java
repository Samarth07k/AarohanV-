package com.artistlink.profile.dto;

/**
 * Aggregate stats for GET /artists|venues/:id/profile-stats (Blueprint 10.7).
 * Marketplace counts are placeholders until those services are wired; social
 * counts are live from the denormalized columns.
 */
public record ProfileStatsResponse(
        int followers,
        int following,
        int posts,
        int opportunities,   // venues
        int applications,
        int negotiations,
        int bookings
) {}
