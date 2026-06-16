package com.artistlink.opportunity;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {

    /* Discovery: all OPEN opportunities, newest first, cursor paginated */
    @Query("""
        SELECT o FROM Opportunity o
        WHERE o.status = com.artistlink.opportunity.OpportunityStatus.OPEN
        ORDER BY o.createdAt DESC, o.id DESC
    """)
    List<Opportunity> findOpenFirstPage(Limit limit);

    @Query("""
        SELECT o FROM Opportunity o
        WHERE o.status = com.artistlink.opportunity.OpportunityStatus.OPEN
          AND (o.createdAt < :cc OR (o.createdAt = :cc AND o.id < :ci))
        ORDER BY o.createdAt DESC, o.id DESC
    """)
    List<Opportunity> findOpenAfter(@Param("cc") Instant cc, @Param("ci") UUID ci, Limit limit);

    /* Venue's own opportunities (any status) */
    List<Opportunity> findByVenueIdOrderByCreatedAtDesc(UUID venueId);
}
