package com.artistlink.common;

import com.artistlink.artist.ArtistRepository;
import com.artistlink.common.exception.BadRequestException;
import com.artistlink.venue.VenueRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Blueprint 9.4 — polymorphic author pattern.
 * author_id / actor_id / follower_id / followee_id are NOT foreign keys; they
 * reference artists.id OR venues.id depending on the type column. Referential
 * integrity is enforced here, at the application layer.
 */
@Component
public class PolymorphicAuthorValidator {

    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;

    public PolymorphicAuthorValidator(ArtistRepository artistRepository, VenueRepository venueRepository) {
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
    }

    public void validateExists(AuthorType type, UUID id) {
        boolean exists = switch (type) {
            case ARTIST -> artistRepository.existsById(id);
            case VENUE -> venueRepository.existsById(id);
        };
        if (!exists) {
            throw new BadRequestException(type + " with id " + id + " does not exist");
        }
    }

    public String resolveDisplayName(AuthorType type, UUID id) {
        return switch (type) {
            case ARTIST -> artistRepository.findById(id).map(a -> a.getDisplayName()).orElse("Unknown Artist");
            case VENUE -> venueRepository.findById(id).map(v -> v.getDisplayName()).orElse("Unknown Venue");
        };
    }

    public String resolveAvatarUrl(AuthorType type, UUID id) {
        return switch (type) {
            case ARTIST -> artistRepository.findById(id).map(a -> a.getAvatarUrl()).orElse(null);
            case VENUE -> venueRepository.findById(id).map(v -> v.getAvatarUrl()).orElse(null);
        };
    }
}
