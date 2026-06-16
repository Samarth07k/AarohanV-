package com.artistlink.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findByNegotiationId(UUID negotiationId);
    List<Booking> findByArtistIdOrderByCreatedAtDesc(UUID artistId);
    List<Booking> findByVenueIdOrderByCreatedAtDesc(UUID venueId);
}
