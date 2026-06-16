package com.artistlink.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateApplicationRequest(
        @NotNull UUID opportunityId,
        @Size(max = 3000) String coverMessage
) {}
