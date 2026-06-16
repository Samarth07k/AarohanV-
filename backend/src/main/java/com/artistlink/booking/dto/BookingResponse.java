package com.artistlink.booking.dto;

import com.artistlink.booking.Booking;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID negotiationId,
        UUID artistId,
        String artistName,
        String artistAvatarUrl,
        UUID venueId,
        String venueName,
        String venueLocation,
        int agreedAmount,
        Instant eventDate,
        String status,
        UUID conversationId,    // the unlocked Artist <-> Venue thread
        Instant createdAt
) {}
