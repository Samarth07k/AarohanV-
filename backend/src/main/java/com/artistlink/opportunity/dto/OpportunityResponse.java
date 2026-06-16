package com.artistlink.opportunity.dto;

import com.artistlink.opportunity.Opportunity;

import java.time.Instant;
import java.util.UUID;

public record OpportunityResponse(
        UUID id,
        UUID venueId,
        String venueName,
        String venueLocation,
        String title,
        String description,
        Instant eventDate,
        Integer budgetMin,
        Integer budgetMax,
        String status,
        long applicationCount,
        boolean hasApplied,        // for the current artist viewing
        UUID myApplicationId,      // their application id if applied
        Instant createdAt
) {
    public static OpportunityResponse of(Opportunity o, String venueName, String venueLocation,
                                         long applicationCount, boolean hasApplied, UUID myApplicationId) {
        return new OpportunityResponse(
                o.getId(), o.getVenueId(), venueName, venueLocation,
                o.getTitle(), o.getDescription(), o.getEventDate(),
                o.getBudgetMin(), o.getBudgetMax(), o.getStatus().name(),
                applicationCount, hasApplied, myApplicationId, o.getCreatedAt());
    }
}
