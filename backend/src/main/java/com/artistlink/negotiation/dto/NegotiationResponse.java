package com.artistlink.negotiation.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record NegotiationResponse(
        UUID id,
        UUID applicationId,
        UUID opportunityId,
        String opportunityTitle,
        UUID artistId,
        String artistName,
        String artistAvatarUrl,
        UUID venueId,
        String venueName,
        String status,
        List<OfferResponse> offers,
        OfferResponse latestOffer,
        UUID bookingId,            // present once agreed
        Instant createdAt
) {}
