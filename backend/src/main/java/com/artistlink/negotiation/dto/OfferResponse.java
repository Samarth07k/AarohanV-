package com.artistlink.negotiation.dto;

import com.artistlink.negotiation.NegotiationOffer;

import java.time.Instant;
import java.util.UUID;

public record OfferResponse(
        UUID id,
        UUID negotiationId,
        String offeredBy,
        int amount,
        String terms,
        Instant createdAt
) {
    public static OfferResponse from(NegotiationOffer o) {
        return new OfferResponse(o.getId(), o.getNegotiationId(), o.getOfferedBy().name(),
                o.getAmount(), o.getTerms(), o.getCreatedAt());
    }
}
