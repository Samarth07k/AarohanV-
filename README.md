# Aarohan

Aarohan — a warm, creative community and booking platform connecting artists with cafés and cultural spaces. Built
strictly to **ArtistLink Complete Implementation Blueprint** — a two-layer system:
a **Social Discovery** layer (Posts, Media, Comments, Likes, Follows) for visibility
and reputation, and a **Marketplace** layer (Opportunity → Application → Negotiation
→ Booking → Messaging) for transactions. Messaging is Artist ↔ Venue only.

This repository is being built **phase by phase** per the blueprint roadmap. This
drop includes **Phase 0** (runnable full-stack slice) **+ Phase A** (the foundational
social layer): Posts with single-image media, Likes, the author's Posts tab, and the
Home Feed with the read-time composed `FeedItem` projection. Following, comments,
trending, and the marketplace services layer on top in Phases B–D without schema or
contract changes.

## Stack

- **Frontend:** React, TypeScript, Vite, Tailwind CSS, Shadcn-style UI, Framer
  Motion, React Query, Zustand, React Hook Form, Zod
- **Backend:** Spring Boot, Java 17, PostgreSQL, JWT + refresh tokens, Flyway
- **Infra:** Docker Compose (Postgres + backend + frontend)

## Run it (one command)

```bash
docker compose up --build
```

Then open:

- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Postgres:** localhost:5432 (db/user/pass: `artistlink`)

Register as an Artist or a Venue, and you land on the role dashboard with the full
navigable shell.

## Run locally without Docker (dev)

Backend (needs JDK 17 + Maven + a local Postgres named `artistlink`):

```bash
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev      # http://localhost:5173, proxies /api -> :8080
```

## What's implemented in Phase 0

**Database (all four migrations, verbatim from the blueprint)**

- `V1` marketplace baseline: users, artists, venues, opportunities, applications,
  negotiations, negotiation_offers, bookings, conversations, messages, notifications,
  refresh_tokens, plus all marketplace enums.
- `V2` social enums + `posts` (with `idx_posts_author`, `idx_posts_feed`).
- `V3` `media_attachments`, `comments`, `likes`, `follows` (with UNIQUE + CHECK
  constraints and all indexes).
- `V4` additive `followers_count` / `following_count` / `posts_count` on artists and
  venues; `notification_type` and `related_entity_type` enum extensions.

**Backend**

- JWT auth with access + refresh tokens (rotation, revoke-on-logout). Token carries
  `userId`, `authorType`, `authorId`. Identity is read from the token, never the body.
- `register` creates a `User` plus its role profile (Artist or Venue) in one
  transaction.
- Endpoints: `POST /auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`,
  `GET /auth/me`.
- `common/`: `AuthorType`, `CursorPagination` (base64 `created_at + "_" + id`),
  `PageResponse`, `PolymorphicAuthorValidator` (app-layer referential integrity for
  the polymorphic author pattern), global exception handler.
- `SecurityConfig` with the public/protected route split and CORS.

**Frontend**

- Boho Botanical design system: exact tokens (`#F9F8F6 / #3B5249 / #EDC9AF /
  #8C6239 / #2B2625`), glassmorphism recipe, soft/lift shadows, Fraunces + Inter.
- Premium glass **landing page** (hero with floating glass cards, artist/venue
  showcases, feed + opportunity previews, marketplace workflow section, social
  discovery section, testimonials, CTA) with Framer Motion throughout.
- **Cursor-follow botanical glow** (auto-disabled on touch devices).
- API client with the **refresh-on-401** single-flight interceptor; endpoint
  constants matching every blueprint contract.
- Zustand `authStore` (persisted) + `uiStore`; React Query provider.
- Auth screens: Login, Register (role-select), Forgot, Reset — React Hook Form + Zod.
- Authenticated **app shell** (desktop sidebar + mobile bottom tab bar), three-zone
  **profile** page, **feed** page with Home/Following/Trending tabs, role
  **dashboards** laid out with the blueprint's exact widget zones.
- Shared `InfiniteScrollList` + `useInfiniteScroll` (built once, reused everywhere).
- Responsive across desktop / tablet / mobile.

## What's implemented in Phase A (social foundation)

**Backend**

- `post/` — `Post` entity (polymorphic author, denormalized `like_count` /
  `comment_count`), `PostService` (create with `posts_count` increment in the same
  transaction + polymorphic validation, get, edit, soft-delete), cursor-paginated
  author + feed queries, `PostController` (`POST/GET/PUT/DELETE /posts`,
  `GET /artists|venues/:id/posts`).
- `media/` — `MediaAttachment`, `MediaUploadService` (signed-URL stub),
  `MediaController` (`POST /media/upload-url`, `POST /posts/:id/media`,
  `DELETE /posts/:id/media/:mediaId`).
- `engagement/` — `Like` with idempotent like/unlike maintaining `like_count`,
  `LikeController` (`POST/DELETE/GET /posts/:id/likes`).
- `feed/` — `FeedItemAssembler` (batched media + batched "liked-by-current-actor",
  no N+1), `FeedService` (Home Feed, cursor pagination), `GET /feed/home`.
- `profile/` — `GET /artists|venues/:id` (identity) and `/profile-stats` (live social
  counts; marketplace counts wired later).

**Frontend**

- `features/posts/` — `PostComposer` (type selector + single-image upload via the
  signed-URL flow), `PostCard`, `AuthorPostCard`, `MediaGallery`, `PostTypeSelector`;
  hooks `useCreatePost`, `usePost`, `useAuthorPosts`, `useMediaUpload`.
- `features/engagement/` — `LikeButton` + `useLike` with optimistic toggle and
  feed-cache patching/rollback (Blueprint 8.5).
- `features/feed/` — `useHomeFeed` (infinite query), `HomeFeed`, `FeedSummary`.
- `features/profile/` — `useProfileStats`; the profile **Posts tab** and **stats row**
  now render live data.
- Home Feed tab and both dashboards' **Feed Summary** widgets are live.

> Media note: the upload flow is a local stub — the composer shows the image during
> the session via an object URL, and the stored URL is a placeholder path. Wiring real
> S3/CDN PUT + static serving is a deployment concern, not a blueprint-core one.

## Folder structure

The frontend follows the blueprint's `features/` + `shared/` + `pages/` layout
(`features/auth`, `features/feed`, plus `posts/ engagement/ follow/ feed/` slots to
be filled in Phases A–C). The backend follows the blueprint's package layout
(`auth/ user/ artist/ venue/ common/`, plus `post/ media/ engagement/ follow/ feed/`
slots for later phases).

## Roadmap (next phases)

- **Phase A** — ✅ Posts (text + image), MediaAttachment, Like, author Posts tab, Home Feed.
- **Phase B** — Follow + counts, Following Feed, NEW_FOLLOWER notification.
- **Phase C** — Comments, like/comment notifications (batched), video, remaining post
  types, spotlight linking.
- **Phase D (deferred)** — Trending Feed, Activity Feed, dashboard aggregate widgets,
  ranking, scale optimizations.

At every phase boundary the marketplace layer keeps operating unchanged.

## Notes

- The frontend has been type-checked and production-built successfully (`npm run build`).
- The backend compiles under JDK 17 via the Docker Maven build stage. (Maven Central
  isn't reachable in the authoring sandbox, so the Java was hand-reviewed there and is
  built for real by `docker compose up`.)
- Forgot/Reset password screens are UI-complete; the email-delivery backend is a
  later, non-blueprint-core concern.

## Marketplace workflow (complete)

The full marketplace is implemented and operational end to end. Social roadmap work
(Follow, Comments, Trending, Activity, Analytics, Recommendations) is intentionally
paused after Phase A.

**Backend** — `opportunity/`, `application/`, `negotiation/`, `booking/`, `messaging/`,
`notification/` each with entity, repository, service, controller, DTOs, validation,
and ownership/participant security. Key flows:

- Opportunity: create / update / close / discover (cursor-paginated) / detail, with
  live application counts and per-artist "hasApplied".
- Application: submit (one per opportunity, enforced by the unique constraint),
  withdraw, venue review (PENDING → REVIEWING / ACCEPTED / REJECTED). Accepting
  opens a negotiation automatically.
- Negotiation: offer thread, counter-offers, accept (only the counterparty's latest
  offer), reject. Accepting marks the negotiation AGREED, the application ACCEPTED,
  creates the Booking, and unlocks the Conversation — all in one transaction.
- Booking: list by role, status tracking (confirmed → completed / cancelled).
- Messaging: Artist ↔ Venue only, restricted to the two booking parties, with read
  receipts.
- Notifications: emitted across the whole flow (NEW_APPLICATION,
  APPLICATION_STATUS_CHANGED, NEGOTIATION_OFFER_RECEIVED, NEGOTIATION_AGREED,
  BOOKING_CONFIRMED, BOOKING_CANCELLED, NEW_MESSAGE), with unread count and mark-read.

**Frontend** — `features/marketplace/` (services, hooks, components) plus
`pages/marketplace/`. Role-adaptive screens: venue manage/create vs artist discovery,
opportunity detail with apply + applications review, negotiation thread UI with the
offer history and accept/reject, bookings with status controls, a two-pane messaging
UI with light polling, and a notifications page. Both dashboards show live
applications / negotiations / bookings / notifications widgets.

### End-to-end smoke test

With the stack running:

```bash
./scripts/smoke-test.sh
```

It runs the full investor-demo flow (venue posts → artist applies → venue accepts →
offers exchanged → accept → booking + conversation → messages both ways) and prints
notification counts.

## Rebrand + Lo-Fi Café theme (this phase)

- Renamed ArtistLink → **Aarohan** across the navbar, app shell, auth screens,
  dashboards, landing page, footer, browser title, and metadata. Logo icon changed
  to a coffee cup.
- Applied the **Lo-Fi Café** palette globally via the design tokens only (no layout
  changes): background `#FDFBF7`, primary text `#3E2723`, muted sage `#8A9A86`
  (primary), terracotta `#D35400` (accent/CTA), warm ochre `#A67C52`, soft almond
  `#E6D9C8`. Shadows softened and warmed; glass, gradients, and cursor glow re-tinted.
  Because components reference semantic tokens, the swap propagates to buttons, cards,
  forms, inputs, modals, nav, dashboard widgets, feed/opportunity/booking cards, and
  the messaging UI without touching component markup.

## Demo seed system (curated, investor-demo scale)

A small **curated** ecosystem (not a generation engine), created entirely through the
real services so referential integrity and notifications behave normally. Idempotent:
it no-ops if the demo artist account already exists. Gated by
`artistlink.demo.seed-enabled` (env `DEMO_SEED`, default `true`; set `false` in
production).

Seeds: 5 artists (Singer, Guitarist, DJ, Poet, Band) and 5 venues (Café, Restaurant,
Rooftop, Art Space, Community), ~12 posts (several with images), 6 opportunities,
~9 applications, multiple negotiations (one left open with an offer history), three
confirmed bookings, one completed booking, seeded conversations with realistic
messages, and the notifications those actions generate.

The two demo accounts are wired into explicit journeys so **neither dashboard is empty
on login**: the demo artist has a pending application, an active negotiation with a
back-and-forth offer history, and a confirmed booking with a live conversation; the
demo venue sees incoming applications, an active negotiation, and confirmed bookings.

### Demo credentials

- **Artist** — `artist@aarohan.demo` / `Demo123!`
- **Venue** — `venue@aarohan.demo` / `Demo123!`
