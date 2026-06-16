package com.artistlink.negotiation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NegotiationRepository extends JpaRepository<Negotiation, UUID> {
    Optional<Negotiation> findByApplicationId(UUID applicationId);
}
