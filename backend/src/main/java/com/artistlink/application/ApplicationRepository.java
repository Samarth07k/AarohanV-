package com.artistlink.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Optional<Application> findByOpportunityIdAndArtistId(UUID opportunityId, UUID artistId);

    boolean existsByOpportunityIdAndArtistId(UUID opportunityId, UUID artistId);

    long countByOpportunityId(UUID opportunityId);

    List<Application> findByOpportunityIdOrderByCreatedAtDesc(UUID opportunityId);

    List<Application> findByArtistIdOrderByCreatedAtDesc(UUID artistId);
}
