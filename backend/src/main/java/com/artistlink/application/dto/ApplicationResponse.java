package com.artistlink.application.dto;

import com.artistlink.application.Application;

import java.time.Instant;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        UUID opportunityId,
        String opportunityTitle,
        UUID venueId,
        String venueName,
        UUID artistId,
        String artistName,
        String artistAvatarUrl,
        String coverMessage,
        String status,
        UUID negotiationId,   // present once a negotiation exists
        Instant createdAt
) {
    public static ApplicationResponse of(Application a, String opportunityTitle, UUID venueId,
                                         String venueName, String artistName, String artistAvatarUrl,
                                         UUID negotiationId) {
        return new ApplicationResponse(
                a.getId(), a.getOpportunityId(), opportunityTitle, venueId, venueName,
                a.getArtistId(), artistName, artistAvatarUrl, a.getCoverMessage(),
                a.getStatus().name(), negotiationId, a.getCreatedAt());
    }
}
