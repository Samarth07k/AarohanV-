package com.artistlink.negotiation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NegotiationOfferRepository extends JpaRepository<NegotiationOffer, UUID> {
    List<NegotiationOffer> findByNegotiationIdOrderByCreatedAtAsc(UUID negotiationId);
}
