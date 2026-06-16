package com.artistlink.profile;

import com.artistlink.artist.Artist;
import com.artistlink.artist.ArtistRepository;
import com.artistlink.common.AuthorType;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.profile.dto.ProfileResponse;
import com.artistlink.profile.dto.ProfileStatsResponse;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
public class ProfileController {

    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;

    public ProfileController(ArtistRepository artistRepository, VenueRepository venueRepository) {
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<ProfileResponse> artist(@PathVariable UUID id) {
        Artist a = artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found"));
        List<String> genres = a.getGenres() == null ? List.of() : Arrays.asList(a.getGenres());
        return ResponseEntity.ok(new ProfileResponse(
                AuthorType.ARTIST, a.getId(), a.getDisplayName(), a.getBio(), a.getLocation(),
                a.getAvatarUrl(), a.getCoverUrl(), genres, null));
    }

    @GetMapping("/venues/{id}")
    public ResponseEntity<ProfileResponse> venue(@PathVariable UUID id) {
        Venue v = venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found"));
        return ResponseEntity.ok(new ProfileResponse(
                AuthorType.VENUE, v.getId(), v.getDisplayName(), v.getBio(), v.getLocation(),
                v.getAvatarUrl(), v.getCoverUrl(), List.of(), v.getCapacity()));
    }

    @GetMapping("/artists/{id}/profile-stats")
    public ResponseEntity<ProfileStatsResponse> artistStats(@PathVariable UUID id) {
        Artist a = artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found"));
        return ResponseEntity.ok(new ProfileStatsResponse(
                a.getFollowersCount(), a.getFollowingCount(), a.getPostsCount(),
                0, 0, 0, 0));
    }

    @GetMapping("/venues/{id}/profile-stats")
    public ResponseEntity<ProfileStatsResponse> venueStats(@PathVariable UUID id) {
        Venue v = venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found"));
        return ResponseEntity.ok(new ProfileStatsResponse(
                v.getFollowersCount(), v.getFollowingCount(), v.getPostsCount(),
                0, 0, 0, 0));
    }
}
