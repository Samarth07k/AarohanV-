package com.artistlink.booking;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "negotiation_id", nullable = false, unique = true)
    private UUID negotiationId;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(name = "agreed_amount", nullable = false)
    private int agreedAmount;

    @Column(name = "event_date")
    private Instant eventDate;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "booking_status")
    private BookingStatus status = BookingStatus.CONFIRMED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public UUID getNegotiationId() { return negotiationId; }
    public void setNegotiationId(UUID v) { this.negotiationId = v; }
    public UUID getArtistId() { return artistId; }
    public void setArtistId(UUID v) { this.artistId = v; }
    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID v) { this.venueId = v; }
    public int getAgreedAmount() { return agreedAmount; }
    public void setAgreedAmount(int v) { this.agreedAmount = v; }
    public Instant getEventDate() { return eventDate; }
    public void setEventDate(Instant v) { this.eventDate = v; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus v) { this.status = v; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
