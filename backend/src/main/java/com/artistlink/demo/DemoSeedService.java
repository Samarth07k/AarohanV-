package com.artistlink.demo;

import com.artistlink.application.ApplicationStatus;
import com.artistlink.application.ApplicationService;
import com.artistlink.application.dto.ApplicationResponse;
import com.artistlink.application.dto.CreateApplicationRequest;
import com.artistlink.artist.Artist;
import com.artistlink.artist.ArtistRepository;
import com.artistlink.auth.AuthPrincipal;
import com.artistlink.auth.AuthService;
import com.artistlink.auth.dto.AuthResponse;
import com.artistlink.auth.dto.RegisterRequest;
import com.artistlink.booking.BookingRepository;
import com.artistlink.booking.BookingStatus;
import com.artistlink.common.AuthorType;
import com.artistlink.media.MediaType;
import com.artistlink.media.MediaUploadService;
import com.artistlink.media.dto.AttachMediaRequest;
import com.artistlink.messaging.MessagingService;
import com.artistlink.messaging.dto.ConversationResponse;
import com.artistlink.negotiation.NegotiationService;
import com.artistlink.negotiation.dto.NegotiationResponse;
import com.artistlink.negotiation.dto.SendOfferRequest;
import com.artistlink.opportunity.OpportunityService;
import com.artistlink.opportunity.dto.CreateOpportunityRequest;
import com.artistlink.opportunity.dto.OpportunityResponse;
import com.artistlink.post.PostType;
import com.artistlink.post.PostService;
import com.artistlink.post.dto.CreatePostRequest;
import com.artistlink.post.dto.PostResponse;
import com.artistlink.user.UserRepository;
import com.artistlink.user.UserRole;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Curated demo ecosystem for the Aarohan marketplace.
 *
 * Demo accounts (all use password: Demo123!)
 *   Artists : artist1@aarohan.demo … artist4@aarohan.demo
 *   Venues  : venue1@aarohan.demo  … venue4@aarohan.demo
 *
 * Seeding is idempotent — checks for artist1@aarohan.demo before running.
 * Everything is created through real services so referential integrity and
 * notifications behave exactly as in normal use.
 */
@Service
public class DemoSeedService {

    private static final Logger log = LoggerFactory.getLogger(DemoSeedService.class);

    static final String DEMO_PASSWORD = "Demo123!";

    // Canonical idempotency anchor — if this account exists, seed has already run.
    private static final String SEED_ANCHOR_EMAIL = "artist1@aarohan.demo";

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final PostService postService;
    private final MediaUploadService mediaUploadService;
    private final OpportunityService opportunityService;
    private final ApplicationService applicationService;
    private final NegotiationService negotiationService;
    private final MessagingService messagingService;

    public DemoSeedService(AuthService authService, UserRepository userRepository,
                           ArtistRepository artistRepository, VenueRepository venueRepository,
                           BookingRepository bookingRepository, PostService postService,
                           MediaUploadService mediaUploadService, OpportunityService opportunityService,
                           ApplicationService applicationService, NegotiationService negotiationService,
                           MessagingService messagingService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.bookingRepository = bookingRepository;
        this.postService = postService;
        this.mediaUploadService = mediaUploadService;
        this.opportunityService = opportunityService;
        this.applicationService = applicationService;
        this.negotiationService = negotiationService;
        this.messagingService = messagingService;
    }

    public boolean alreadySeeded() {
        return userRepository.existsByEmail(SEED_ANCHOR_EMAIL);
    }

    private record Actor(AuthPrincipal principal, String displayName) {}
    private record OppRef(UUID id, Actor venue) {}

    @Transactional
    public void seed() {
        if (alreadySeeded()) {
            log.info("[demo-seed] Demo data already present — skipping.");
            return;
        }
        log.info("[demo-seed] Seeding Aarohan demo ecosystem…");

        // ---- Artists: artist1@aarohan.demo … artist4@aarohan.demo ----
        List<Actor> artists = new ArrayList<>();
        for (int i = 0; i < DemoData.ARTISTS.size(); i++) {
            String email = "artist" + (i + 1) + "@aarohan.demo";
            artists.add(registerArtist(email, DemoData.ARTISTS.get(i)));
        }

        // ---- Venues: venue1@aarohan.demo … venue4@aarohan.demo ----
        List<Actor> venues = new ArrayList<>();
        for (int i = 0; i < DemoData.VENUES.size(); i++) {
            String email = "venue" + (i + 1) + "@aarohan.demo";
            venues.add(registerVenue(email, DemoData.VENUES.get(i)));
        }

        Actor artist1 = artists.get(0); // Aarav Sharma
        Actor artist2 = artists.get(1); // Meera Joshi
        Actor artist3 = artists.get(2); // Kabir Khan
        Actor artist4 = artists.get(3); // Riya Patel

        Actor venue1 = venues.get(0);   // Green Room Cafe
        Actor venue2 = venues.get(1);   // Moonlight Coffee House
        Actor venue3 = venues.get(2);   // Studio 27
        Actor venue4 = venues.get(3);   // Riverside Arts Collective

        // ---- 12 posts cycled across all artists ----
        int img = 1;
        for (int i = 0; i < DemoData.POSTS.size(); i++) {
            DemoData.PostSeed ps = DemoData.POSTS.get(i);
            Actor author = artists.get(i % artists.size());
            PostResponse post = postService.create(author.principal(),
                    new CreatePostRequest(PostType.valueOf(ps.type()), ps.content(), null, null, null, null));
            if (ps.withImage()) {
                mediaUploadService.attach(post.id(),
                        new AttachMediaRequest(MediaType.IMAGE, DemoData.photo(img++), null, 900, 600, null, 0));
            }
        }

        // ---- 8 opportunities across all 4 venues (2 each) ----
        String[][] oppSpecs = {
            // venue1 — Green Room Cafe
            {"Friday Night Live",      "We're after a warm, intimate set for our Friday crowd.",   "400", "600"},
            {"Sunday Jazz Brunch",      "Easy-going jazz to pair with coffee and pastries.",         "300", "450"},
            // venue2 — Moonlight Coffee House
            {"Open Mic Showcase",       "A featured slot to headline our monthly open mic.",         "250", "400"},
            {"Acoustic Dinner Night",   "Background-to-foreground acoustic set during dinner service.", "350", "550"},
            // venue3 — Studio 27
            {"Rooftop Sunset Set",      "Golden-hour music with a view over the city.",              "500", "800"},
            {"Late Night Sessions",     "Intimate late-night set for our post-dinner crowd.",        "450", "650"},
            // venue4 — Riverside Arts Collective
            {"Poetry & Music Evening",  "An evening of spoken word, live music and natural wine.",  "200", "350"},
            {"Weekend Arts Festival",   "Headline slot at our monthly riverside arts weekend.",     "600", "900"},
        };

        Actor[] oppVenues = {venue1, venue1, venue2, venue2, venue3, venue3, venue4, venue4};

        List<OppRef> opps = new ArrayList<>();
        for (int i = 0; i < oppSpecs.length; i++) {
            Instant when = Instant.now().plus(7L + i * 4L, ChronoUnit.DAYS);
            OpportunityResponse o = opportunityService.create(oppVenues[i].principal(),
                    new CreateOpportunityRequest(oppSpecs[i][0], oppSpecs[i][1], when,
                            Integer.parseInt(oppSpecs[i][2]), Integer.parseInt(oppSpecs[i][3])));
            opps.add(new OppRef(o.id(), oppVenues[i]));
        }

        // ---- Applications ----
        // opp[0] (Green Room Fri Night): artist2, artist3, artist4 apply
        apply(artist2, opps.get(0), 1);
        apply(artist3, opps.get(0), 2);
        apply(artist4, opps.get(0), 3);

        // opp[1] (Sunday Jazz Brunch): artist3 applies (artist1 applies in Journey B below)
        apply(artist3, opps.get(1), 2);

        // opp[2] (Open Mic): artist1 applies (PENDING — visible in venue2's inbox)
        apply(artist1, opps.get(2), 4);

        // ---- Journey A: artist1 → opp[0] → ACCEPTED → negotiation with offer history (still OPEN) ----
        ApplicationResponse appA = apply(artist1, opps.get(0), 0);
        if (appA != null) {
            applicationService.review(venue1.principal(), appA.id(), ApplicationStatus.ACCEPTED);
            NegotiationResponse nA = negotiationService.getByApplication(venue1.principal(), appA.id());
            negotiationService.sendOffer(venue1.principal(), nA.id(),
                    new SendOfferRequest(450, "2x45min sets, sound check at 6:30"));
            negotiationService.sendOffer(artist1.principal(), nA.id(),
                    new SendOfferRequest(525, "Could we meet at 525 including travel?"));
            // Left OPEN — an active negotiation awaiting venue1's response
        }

        // ---- Journey B: artist1 → opp[1] → ACCEPTED → negotiation AGREED → booking + conversation ----
        ApplicationResponse appB = apply(artist1, opps.get(1), 0);
        if (appB != null) {
            applicationService.review(venue1.principal(), appB.id(), ApplicationStatus.ACCEPTED);
            NegotiationResponse nB = negotiationService.getByApplication(venue1.principal(), appB.id());
            negotiationService.sendOffer(venue1.principal(), nB.id(),
                    new SendOfferRequest(500, "Headline slot, 2x45min, dinner included"));
            negotiationService.accept(artist1.principal(), nB.id()); // → booking + conversation
        }

        // ---- Journey C: artist2 → opp[4] → ACCEPTED → AGREED → confirmed booking ----
        ApplicationResponse appC = apply(artist2, opps.get(4), 1);
        if (appC != null) {
            applicationService.review(venue3.principal(), appC.id(), ApplicationStatus.ACCEPTED);
            NegotiationResponse nC = negotiationService.getByApplication(venue3.principal(), appC.id());
            negotiationService.sendOffer(venue3.principal(), nC.id(),
                    new SendOfferRequest(550, "Rooftop golden-hour set, 60min"));
            negotiationService.accept(artist2.principal(), nC.id());
        }

        // ---- Journey D: artist3 → opp[6] → ACCEPTED → AGREED → COMPLETED booking ----
        ApplicationResponse appD = apply(artist3, opps.get(6), 2);
        if (appD != null) {
            applicationService.review(venue4.principal(), appD.id(), ApplicationStatus.ACCEPTED);
            NegotiationResponse nD = negotiationService.getByApplication(venue4.principal(), appD.id());
            negotiationService.sendOffer(venue4.principal(), nD.id(),
                    new SendOfferRequest(280, "Spoken word + music set, 45min"));
            negotiationService.accept(artist3.principal(), nD.id());
            bookingRepository.findByNegotiationId(nD.id()).ifPresent(b -> {
                b.setStatus(BookingStatus.COMPLETED);
                bookingRepository.save(b);
            });
        }

        // ---- Seed realistic messages in every conversation that now exists ----
        for (Actor v : venues) seedMessages(v);

        printCredentialsBanner(artists, venues);

        log.info("[demo-seed] Done. 4 artists, 4 venues, 8 opportunities, 3 bookings seeded.");
    }

    // ---- helpers ----

    private ApplicationResponse apply(Actor artist, OppRef opp, int coverIdx) {
        try {
            return applicationService.submit(artist.principal(),
                    new CreateApplicationRequest(opp.id(),
                            DemoData.COVER_MESSAGES.get(coverIdx % DemoData.COVER_MESSAGES.size())));
        } catch (RuntimeException e) {
            log.debug("[demo-seed] application skipped ({}): {}", artist.displayName(), e.getMessage());
            return null;
        }
    }

    private void seedMessages(Actor venueActor) {
        List<ConversationResponse> convos;
        try {
            convos = messagingService.myConversations(venueActor.principal());
        } catch (RuntimeException e) {
            return;
        }
        for (ConversationResponse c : convos) {
            try {
                if (!messagingService.messages(venueActor.principal(), c.id()).isEmpty()) continue;
            } catch (RuntimeException e) {
                continue;
            }
            // Use the correct authorId for messaging (not userId)
            AuthPrincipal venueP  = new AuthPrincipal(c.venueId(),  AuthorType.VENUE,  c.venueId(),  null);
            AuthPrincipal artistP = new AuthPrincipal(c.artistId(), AuthorType.ARTIST, c.artistId(), null);
            for (int t = 0; t < 3; t++) {
                try {
                    messagingService.send(venueP,  c.id(), DemoData.VENUE_LINES.get(t));
                    messagingService.send(artistP, c.id(), DemoData.ARTIST_LINES.get(t));
                } catch (RuntimeException e) {
                    break;
                }
            }
        }
    }

    private Actor registerArtist(String email, DemoData.ArtistSeed s) {
        AuthResponse resp = authService.register(new RegisterRequest(
                email, DEMO_PASSWORD, UserRole.ARTIST, s.displayName(), s.location()));
        UUID artistId = resp.actor().authorId();
        Artist a = artistRepository.findById(artistId).orElseThrow();
        a.setBio(s.bio());
        a.setGenres(s.genres());
        a.setAvatarUrl(DemoData.avatar(s.displayName()));
        a.setCoverUrl(DemoData.cover(Math.abs(s.displayName().hashCode()) % 50));
        artistRepository.save(a);
        return new Actor(new AuthPrincipal(resp.actor().userId(), AuthorType.ARTIST, artistId, email), s.displayName());
    }

    private Actor registerVenue(String email, DemoData.VenueSeed s) {
        AuthResponse resp = authService.register(new RegisterRequest(
                email, DEMO_PASSWORD, UserRole.VENUE, s.displayName(), s.location()));
        UUID venueId = resp.actor().authorId();
        Venue v = venueRepository.findById(venueId).orElseThrow();
        v.setBio(s.bio());
        v.setCapacity(s.capacity());
        v.setAvatarUrl(DemoData.avatar(s.displayName()));
        v.setCoverUrl(DemoData.cover(Math.abs(s.displayName().hashCode()) % 50));
        venueRepository.save(v);
        return new Actor(new AuthPrincipal(resp.actor().userId(), AuthorType.VENUE, venueId, email), s.displayName());
    }

    private void printCredentialsBanner(List<Actor> artists, List<Actor> venues) {
        String sep = "=".repeat(60);
        log.info(sep);
        log.info("  AAROHAN DEMO CREDENTIALS  (password: {})", DEMO_PASSWORD);
        log.info(sep);
        log.info("  ARTISTS");
        for (int i = 0; i < artists.size(); i++) {
            log.info("    artist{}@aarohan.demo  →  {}", (i + 1), artists.get(i).displayName());
        }
        log.info("  VENUES");
        for (int i = 0; i < venues.size(); i++) {
            log.info("    venue{}@aarohan.demo   →  {}", (i + 1), venues.get(i).displayName());
        }
        log.info(sep);
    }
}
