package com.artistlink.opportunity.dto;

import com.artistlink.opportunity.OpportunityStatus;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateOpportunityRequest(
        @Size(max = 200) String title,
        @Size(max = 5000) String description,
        Instant eventDate,
        Integer budgetMin,
        Integer budgetMax,
        OpportunityStatus status
) {}
