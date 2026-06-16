package com.artistlink.negotiation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendOfferRequest(
        @NotNull @Min(0) Integer amount,
        @Size(max = 2000) String terms
) {}
