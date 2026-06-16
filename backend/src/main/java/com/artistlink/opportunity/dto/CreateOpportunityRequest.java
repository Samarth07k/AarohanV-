package com.artistlink.opportunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateOpportunityRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 5000) String description,
        Instant eventDate,
        Integer budgetMin,
        Integer budgetMax
) {}
