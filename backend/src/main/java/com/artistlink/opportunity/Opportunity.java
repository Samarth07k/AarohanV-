package com.artistlink.opportunity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description = "";

    @Column(name = "event_date")
    private Instant eventDate;

    @Column(name = "budget_min")
    private Integer budgetMin;

    @Column(name = "budget_max")
    private Integer budgetMax;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "opportunity_status")
    private OpportunityStatus status = OpportunityStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID v) { this.venueId = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public Instant getEventDate() { return eventDate; }
    public void setEventDate(Instant v) { this.eventDate = v; }
    public Integer getBudgetMin() { return budgetMin; }
    public void setBudgetMin(Integer v) { this.budgetMin = v; }
    public Integer getBudgetMax() { return budgetMax; }
    public void setBudgetMax(Integer v) { this.budgetMax = v; }
    public OpportunityStatus getStatus() { return status; }
    public void setStatus(OpportunityStatus v) { this.status = v; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
