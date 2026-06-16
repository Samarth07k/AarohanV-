package com.artistlink.auth;

import com.artistlink.artist.Artist;
import com.artistlink.artist.ArtistRepository;
import com.artistlink.auth.dto.*;
import com.artistlink.common.AuthorType;
import com.artistlink.common.exception.BadRequestException;
import com.artistlink.common.exception.ForbiddenException;
import com.artistlink.user.User;
import com.artistlink.user.UserRepository;
import com.artistlink.user.UserRole;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, ArtistRepository artistRepository,
                       VenueRepository venueRepository, RefreshTokenService refreshTokenService,
                       JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BadRequestException("Email already registered");
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(req.role());
        user = userRepository.save(user);

        AuthorType authorType;
        UUID authorId;
        String avatarUrl;
        if (req.role() == UserRole.ARTIST) {
            Artist artist = new Artist();
            artist.setUserId(user.getId());
            artist.setDisplayName(req.displayName());
            artist.setLocation(req.location());
            artist = artistRepository.save(artist);
            authorType = AuthorType.ARTIST;
            authorId = artist.getId();
            avatarUrl = artist.getAvatarUrl();
        } else {
            Venue venue = new Venue();
            venue.setUserId(user.getId());
            venue.setDisplayName(req.displayName());
            venue.setLocation(req.location());
            venue = venueRepository.save(venue);
            authorType = AuthorType.VENUE;
            authorId = venue.getId();
            avatarUrl = venue.getAvatarUrl();
        }

        return issue(user, authorType, authorId, req.displayName(), avatarUrl);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ForbiddenException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ForbiddenException("Invalid credentials");
        }

        AuthorType authorType;
        UUID authorId;
        String displayName;
        String avatarUrl;
        if (user.getRole() == UserRole.ARTIST) {
            Artist a = artistRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ForbiddenException("Artist profile missing"));
            authorType = AuthorType.ARTIST;
            authorId = a.getId();
            displayName = a.getDisplayName();
            avatarUrl = a.getAvatarUrl();
        } else {
            Venue v = venueRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ForbiddenException("Venue profile missing"));
            authorType = AuthorType.VENUE;
            authorId = v.getId();
            displayName = v.getDisplayName();
            avatarUrl = v.getAvatarUrl();
        }
        return issue(user, authorType, authorId, displayName, avatarUrl);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest req) {
        UUID userId = refreshTokenService.rotate(req.refreshToken());
        return reissueForUser(userId);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private AuthResponse issue(User user, AuthorType authorType, UUID authorId,
                               String displayName, String avatarUrl) {
        String access = jwtService.issueAccessToken(user.getId(), authorType, authorId, user.getEmail());
        String refresh = refreshTokenService.issue(user.getId());
        return new AuthResponse(access, refresh,
                new AuthResponse.ActorInfo(user.getId(), authorType, authorId,
                        user.getEmail(), displayName, avatarUrl));
    }

    /** Used by the refresh endpoint to re-issue tokens for a known user. */
    @Transactional
    public AuthResponse reissueForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        AuthorType authorType;
        UUID authorId;
        String displayName;
        String avatarUrl;
        if (user.getRole() == UserRole.ARTIST) {
            Artist a = artistRepository.findByUserId(user.getId()).orElseThrow();
            authorType = AuthorType.ARTIST; authorId = a.getId();
            displayName = a.getDisplayName(); avatarUrl = a.getAvatarUrl();
        } else {
            Venue v = venueRepository.findByUserId(user.getId()).orElseThrow();
            authorType = AuthorType.VENUE; authorId = v.getId();
            displayName = v.getDisplayName(); avatarUrl = v.getAvatarUrl();
        }
        return issue(user, authorType, authorId, displayName, avatarUrl);
    }
}
