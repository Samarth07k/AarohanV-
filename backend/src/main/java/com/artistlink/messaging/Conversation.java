package com.artistlink.messaging;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "booking_id", nullable = false, unique = true)
    private UUID bookingId;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID v) { this.bookingId = v; }
    public UUID getArtistId() { return artistId; }
    public void setArtistId(UUID v) { this.artistId = v; }
    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID v) { this.venueId = v; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
