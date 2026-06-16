-- =====================================================================
-- V2 — Social enums + posts table (Blueprint 9.1 / 9.2)
-- =====================================================================

-- ENUMS (new)
CREATE TYPE post_author_type AS ENUM ('ARTIST', 'VENUE');
CREATE TYPE post_type AS ENUM (
    'ACHIEVEMENT', 'PERFORMANCE_CLIP', 'EVENT_PHOTO', 'ANNOUNCEMENT',
    'UPCOMING_EVENT', 'VENUE_UPDATE', 'ARTIST_SPOTLIGHT', 'VENUE_ACHIEVEMENT',
    'GENERAL'
);
CREATE TYPE post_status AS ENUM ('PUBLISHED', 'HIDDEN', 'DELETED');
CREATE TYPE post_visibility AS ENUM ('PUBLIC', 'FOLLOWERS_ONLY');
CREATE TYPE media_type AS ENUM ('IMAGE', 'VIDEO');
CREATE TYPE linked_entity_type AS ENUM ('OPPORTUNITY', 'BOOKING', 'ARTIST', 'VENUE');

-- POSTS
CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_type post_author_type NOT NULL,
    author_id UUID NOT NULL,                 -- artistId or venueId; no FK due to polymorphism
    post_type post_type NOT NULL,
    content TEXT NOT NULL DEFAULT '',
    linked_entity_type linked_entity_type,
    linked_entity_id UUID,
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    visibility post_visibility NOT NULL DEFAULT 'PUBLIC',
    status post_status NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Composite index for author's own post list + profile "Posts" tab
CREATE INDEX idx_posts_author ON posts(author_type, author_id, created_at DESC);
-- Index for feed queries (published, public, recent-first)
CREATE INDEX idx_posts_feed ON posts(status, visibility, created_at DESC)
    WHERE status = 'PUBLISHED';
