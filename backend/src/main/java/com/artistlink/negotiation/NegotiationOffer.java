package com.artistlink.negotiation;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "negotiation_offers")
public class NegotiationOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "negotiation_id", nullable = false)
    private UUID negotiationId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "offered_by", nullable = false, columnDefinition = "offer_party")
    private OfferParty offeredBy;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String terms = "";

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public UUID getNegotiationId() { return negotiationId; }
    public void setNegotiationId(UUID v) { this.negotiationId = v; }
    public OfferParty getOfferedBy() { return offeredBy; }
    public void setOfferedBy(OfferParty v) { this.offeredBy = v; }
    public int getAmount() { return amount; }
    public void setAmount(int v) { this.amount = v; }
    public String getTerms() { return terms; }
    public void setTerms(String v) { this.terms = v; }
    public Instant getCreatedAt() { return createdAt; }
}
