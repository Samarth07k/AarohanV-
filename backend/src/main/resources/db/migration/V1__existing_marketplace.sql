-- =====================================================================
-- V1 — Marketplace baseline (existing blueprint). UNCHANGED by social layer.
-- =====================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;  -- gen_random_uuid()

-- ---- Enums ----------------------------------------------------------
CREATE TYPE user_role AS ENUM ('ARTIST', 'VENUE');

CREATE TYPE opportunity_status AS ENUM ('OPEN', 'CLOSED', 'CANCELLED');
CREATE TYPE application_status AS ENUM ('PENDING', 'REVIEWING', 'ACCEPTED', 'REJECTED', 'WITHDRAWN');
CREATE TYPE negotiation_status AS ENUM ('OPEN', 'AGREED', 'DECLINED');
CREATE TYPE offer_party AS ENUM ('ARTIST', 'VENUE');
CREATE TYPE booking_status AS ENUM ('CONFIRMED', 'COMPLETED', 'CANCELLED');
CREATE TYPE message_sender_type AS ENUM ('ARTIST', 'VENUE');

CREATE TYPE notification_type AS ENUM (
    'NEW_APPLICATION',
    'APPLICATION_STATUS_CHANGED',
    'NEGOTIATION_OFFER_RECEIVED',
    'NEGOTIATION_AGREED',
    'BOOKING_CONFIRMED',
    'BOOKING_CANCELLED',
    'NEW_MESSAGE',
    'OPPORTUNITY_CLOSED'
);
CREATE TYPE related_entity_type AS ENUM (
    'OPPORTUNITY', 'APPLICATION', 'NEGOTIATION', 'BOOKING', 'MESSAGE'
);

-- ---- Users ----------------------------------------------------------
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role user_role NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ---- Artists --------------------------------------------------------
CREATE TABLE artists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    display_name TEXT NOT NULL,
    bio TEXT NOT NULL DEFAULT '',
    location TEXT,
    genres TEXT[] NOT NULL DEFAULT '{}',
    avatar_url TEXT,
    cover_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ---- Venues ---------------------------------------------------------
CREATE TABLE venues (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    display_name TEXT NOT NULL,
    bio TEXT NOT NULL DEFAULT '',
    location TEXT,
    capacity INT,
    avatar_url TEXT,
    cover_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ---- Refresh tokens -------------------------------------------------
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- ---- Opportunities --------------------------------------------------
CREATE TABLE opportunities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id UUID NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    event_date TIMESTAMPTZ,
    budget_min INT,
    budget_max INT,
    status opportunity_status NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_opportunities_venue ON opportunities(venue_id, created_at DESC);
CREATE INDEX idx_opportunities_status ON opportunities(status, created_at DESC);

-- ---- Applications ---------------------------------------------------
CREATE TABLE applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    opportunity_id UUID NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    cover_message TEXT NOT NULL DEFAULT '',
    status application_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(opportunity_id, artist_id)
);
CREATE INDEX idx_applications_opportunity ON applications(opportunity_id);
CREATE INDEX idx_applications_artist ON applications(artist_id, created_at DESC);

-- ---- Negotiations ---------------------------------------------------
CREATE TABLE negotiations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL UNIQUE REFERENCES applications(id) ON DELETE CASCADE,
    status negotiation_status NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE negotiation_offers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negotiation_id UUID NOT NULL REFERENCES negotiations(id) ON DELETE CASCADE,
    offered_by offer_party NOT NULL,
    amount INT NOT NULL,
    terms TEXT NOT NULL DEFAULT '',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_negotiation_offers_negotiation ON negotiation_offers(negotiation_id, created_at);

-- ---- Bookings -------------------------------------------------------
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negotiation_id UUID NOT NULL UNIQUE REFERENCES negotiations(id) ON DELETE CASCADE,
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    venue_id UUID NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    agreed_amount INT NOT NULL,
    event_date TIMESTAMPTZ,
    status booking_status NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_bookings_artist ON bookings(artist_id, created_at DESC);
CREATE INDEX idx_bookings_venue ON bookings(venue_id, created_at DESC);

-- ---- Conversations + Messages (Artist <-> Venue, unlocked by Booking)
CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    venue_id UUID NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_conversations_artist ON conversations(artist_id);
CREATE INDEX idx_conversations_venue ON conversations(venue_id);

CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_type message_sender_type NOT NULL,
    sender_id UUID NOT NULL,
    content TEXT NOT NULL,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_messages_conversation ON messages(conversation_id, created_at DESC);

-- ---- Notifications --------------------------------------------------
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_type user_role NOT NULL,
    recipient_id UUID NOT NULL,
    type notification_type NOT NULL,
    related_entity_type related_entity_type,
    related_entity_id UUID,
    title TEXT NOT NULL,
    body TEXT NOT NULL DEFAULT '',
    count INT NOT NULL DEFAULT 1,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_type, recipient_id, created_at DESC);
