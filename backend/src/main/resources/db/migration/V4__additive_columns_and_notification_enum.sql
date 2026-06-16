-- =====================================================================
-- V4 — Additive columns on existing tables + notification enum (Blueprint 9.3)
-- =====================================================================

-- ARTISTS — additive only
ALTER TABLE artists ADD COLUMN followers_count INT NOT NULL DEFAULT 0;
ALTER TABLE artists ADD COLUMN following_count INT NOT NULL DEFAULT 0;
ALTER TABLE artists ADD COLUMN posts_count INT NOT NULL DEFAULT 0;

-- VENUES — additive only
ALTER TABLE venues ADD COLUMN followers_count INT NOT NULL DEFAULT 0;
ALTER TABLE venues ADD COLUMN following_count INT NOT NULL DEFAULT 0;
ALTER TABLE venues ADD COLUMN posts_count INT NOT NULL DEFAULT 0;

-- NOTIFICATIONS — enum extension (Postgres requires ALTER TYPE; must be
-- committed before use, so each runs as its own statement here).
ALTER TYPE notification_type ADD VALUE IF NOT EXISTS 'NEW_FOLLOWER';
ALTER TYPE notification_type ADD VALUE IF NOT EXISTS 'POST_LIKED';
ALTER TYPE notification_type ADD VALUE IF NOT EXISTS 'POST_COMMENTED';
ALTER TYPE notification_type ADD VALUE IF NOT EXISTS 'MENTIONED_IN_POST';

-- relatedEntityType extended with POST, COMMENT, FOLLOW
ALTER TYPE related_entity_type ADD VALUE IF NOT EXISTS 'POST';
ALTER TYPE related_entity_type ADD VALUE IF NOT EXISTS 'COMMENT';
ALTER TYPE related_entity_type ADD VALUE IF NOT EXISTS 'FOLLOW';
