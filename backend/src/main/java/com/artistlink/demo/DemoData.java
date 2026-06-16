package com.artistlink.demo;

import java.util.List;

/**
 * Curated demo ecosystem catalog matching the Aarohan product spec.
 * Artists: Aarav Sharma, Meera Joshi, Kabir Khan, Riya Patel.
 * Venues: Green Room Cafe, Moonlight Coffee House, Studio 27, Riverside Arts Collective.
 * Avatars/covers use deterministic placeholder URLs so profiles render without bundled binaries.
 */
final class DemoData {

    private DemoData() {}

    record ArtistSeed(String displayName, String category, String location, String bio, String[] genres) {}
    record VenueSeed(String displayName, String kind, String location, String bio, int capacity) {}
    record PostSeed(String type, String content, boolean withImage) {}

    static String avatar(String seed) {
        return "https://api.dicebear.com/7.x/thumbs/svg?seed=" + seed.replace(" ", "%20");
    }
    static String cover(int n) { return "https://picsum.photos/seed/aarohan-cover-" + n + "/1200/400"; }
    static String photo(int n)  { return "https://picsum.photos/seed/aarohan-post-" + n + "/900/600"; }

    // index 0 → artist1@aarohan.demo, index 1 → artist2@aarohan.demo, etc.
    static final List<ArtistSeed> ARTISTS = List.of(
        new ArtistSeed("Aarav Sharma", "Singer-Songwriter", "Mumbai",
            "Soulful singer-songwriter blending Hindustani classical with indie folk. " +
            "Regular performer at intimate Mumbai cafés and cultural spaces.",
            new String[]{"Indie Folk", "Classical Fusion"}),
        new ArtistSeed("Meera Joshi", "Classical Vocalist", "Delhi",
            "Trained Hindustani classical vocalist bringing ragas to contemporary spaces. " +
            "Teaching and performing across Delhi's art circuit for over a decade.",
            new String[]{"Hindustani Classical", "Devotional"}),
        new ArtistSeed("Kabir Khan", "Jazz Guitarist", "Bangalore",
            "Jazz guitarist with a warm, late-night tone. Holds residencies at Bangalore's " +
            "top jazz bars and is equally at home in intimate acoustic sets.",
            new String[]{"Jazz", "Blues"}),
        new ArtistSeed("Riya Patel", "Electronic Artist", "Pune",
            "Electronic producer and vocalist crafting dreamy ambient soundscapes for cafés, " +
            "galleries, and rooftop events. Known for immersive live sets.",
            new String[]{"Ambient", "Electronic"})
    );

    // index 0 → venue1@aarohan.demo, index 1 → venue2@aarohan.demo, etc.
    static final List<VenueSeed> VENUES = List.of(
        new VenueSeed("Green Room Cafe", "Café", "Mumbai",
            "A plant-filled café with a small corner stage, artisan coffee, " +
            "and an audience that genuinely listens.", 60),
        new VenueSeed("Moonlight Coffee House", "Coffee House", "Delhi",
            "Intimate coffee house known for warm ambience and curated live music nights " +
            "every weekend. A favourite on Delhi's underground arts circuit.", 80),
        new VenueSeed("Studio 27", "Music Studio & Venue", "Bangalore",
            "Recording studio by day, intimate performance space by night. " +
            "Perfect for stripped-back acoustic, jazz, and experimental sets.", 120),
        new VenueSeed("Riverside Arts Collective", "Art Space", "Pune",
            "A riverside arts collective hosting music, poetry, and visual art — " +
            "a creative hub for Pune's growing arts community.", 200)
    );

    // 12 posts cycled across authors (index % ARTISTS.size())
    static final List<PostSeed> POSTS = List.of(
        new PostSeed("ANNOUNCEMENT",    "Thrilled to announce a new residency this month! Come say hi.", false),
        new PostSeed("EVENT_PHOTO",     "Last night's set was pure magic. Thank you for singing along.", true),
        new PostSeed("UPCOMING_EVENT",  "Playing this Friday — doors at 8, music at 9.", false),
        new PostSeed("GENERAL",         "New song in the works. Here's a little corner of the process.", true),
        new PostSeed("ANNOUNCEMENT",    "Just crossed 1,000 followers. Grateful for this community.", false),
        new PostSeed("EVENT_PHOTO",     "Full house and warm hearts. What a room.", true),
        new PostSeed("UPCOMING_EVENT",  "Two more shows added next week by popular demand.", false),
        new PostSeed("GENERAL",         "Sound-check smiles before tonight's show.", true),
        new PostSeed("ANNOUNCEMENT",    "Honoured to be featured at this month's showcase.", false),
        new PostSeed("EVENT_PHOTO",     "That moment when the whole café went quiet for the last song.", true),
        new PostSeed("GENERAL",         "Rehearsing something new for you all. Soon.", false),
        new PostSeed("UPCOMING_EVENT",  "Rooftop set this weekend — bring a friend.", true)
    );

    static final List<String> VENUE_LINES = List.of(
        "Hi! We're so glad to have you for the date. Anything you need from us?",
        "Great — we'll have the sound system ready by 7.",
        "Parking is round the back, just buzz at the side door.",
        "Looking forward to it. The room's going to love you."
    );

    static final List<String> ARTIST_LINES = List.of(
        "Thank you! Just a couple of mics and a stool if possible.",
        "Perfect, I'll arrive around 6:30 for setup.",
        "Got it, thanks for the tip on parking.",
        "Can't wait — see you Friday!"
    );

    static final List<String> COVER_MESSAGES = List.of(
        "I'd love to play this slot — I think my sound is a great fit for your room.",
        "Big fan of your space. Would be honoured to perform.",
        "Available on the date and excited about the vibe you've described.",
        "I've played similar rooms and always leave the crowd wanting one more song.",
        "This looks like exactly the kind of evening I love performing for.",
        "My style matches the atmosphere perfectly — let's make something memorable.",
        "I've been looking for a slot like this. My setlist is ready.",
        "The crowd you describe is exactly who I play for."
    );
}
