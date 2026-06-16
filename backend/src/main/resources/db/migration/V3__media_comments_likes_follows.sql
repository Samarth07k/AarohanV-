-- =====================================================================
-- V3 — Media, Comments, Likes, Follows (Blueprint 9.2)
-- =====================================================================

-- MEDIA ATTACHMENTS
CREATE TABLE media_attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    media_type media_type NOT NULL,
    url TEXT NOT NULL,
    thumbnail_url TEXT,
    width INT,
    height INT,
    duration_seconds INT,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_media_attachments_post_id ON media_attachments(post_id);

-- COMMENTS
CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_type post_author_type NOT NULL,
    author_id UUID NOT NULL,
    content TEXT NOT NULL,
    status post_status NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_comments_post_id ON comments(post_id, created_at);

-- LIKES
CREATE TABLE likes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    actor_type post_author_type NOT NULL,
    actor_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(post_id, actor_type, actor_id)
);
CREATE INDEX idx_likes_post_id ON likes(post_id);
CREATE INDEX idx_likes_actor ON likes(actor_type, actor_id);

-- FOLLOWS
CREATE TABLE follows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    follower_type post_author_type NOT NULL,
    follower_id UUID NOT NULL,
    followee_type post_author_type NOT NULL,
    followee_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(follower_type, follower_id, followee_type, followee_id),
    CHECK (NOT (follower_type = followee_type AND follower_id = followee_id))
);
-- For "who do I follow" (Following Feed source)
CREATE INDEX idx_follows_follower ON follows(follower_type, follower_id);
-- For "who follows me" (followers count, follower lists)
CREATE INDEX idx_follows_followee ON follows(followee_type, followee_id);
