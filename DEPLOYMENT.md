# Aarohan Deployment Guide

## Architecture

```
Vercel (Frontend)  ──HTTPS──►  Render (Backend)  ──►  Render PostgreSQL
React + Vite                   Spring Boot                PostgreSQL 16
```

---

## 1 — Deploy the Database (Render PostgreSQL)

1. In your Render dashboard → **New → PostgreSQL**
2. Choose a name (e.g. `aarohan-db`) and region
3. After creation, copy:
   - **Internal Database URL** → you'll use this as `DB_URL` in the backend
   - **Username** → `DB_USER`
   - **Password** → `DB_PASSWORD` *(treat as a secret)*

---

## 2 — Deploy the Backend (Render Web Service)

### Option A — Render Dashboard (recommended for first deploy)

1. **New → Web Service** → connect your GitHub repo
2. Set **Root Directory** to `backend`
3. Set **Runtime** to `Docker`
4. Set **Dockerfile path** to `./Dockerfile`
5. Set **Health Check Path** to `/actuator/health`
6. Add these **Environment Variables** (Settings → Environment):

| Key | Value |
|-----|-------|
| `DB_URL` | `jdbc:postgresql://<internal-host>:5432/<db-name>` |
| `DB_USER` | your Render DB username |
| `DB_PASSWORD` | your Render DB password *(mark as secret)* |
| `JWT_SECRET` | output of `openssl rand -base64 32` *(mark as secret)* |
| `JWT_ACCESS_TTL` | `900` |
| `JWT_REFRESH_TTL` | `604800` |
| `CORS_ORIGINS` | `https://your-frontend.vercel.app` |
| `SERVER_PORT` | `8080` |
| `DEMO_SEED` | `true` |

7. Click **Deploy**
8. Wait for the build → visit `https://<your-service>.onrender.com/actuator/health`
   - Expected: `{"status":"UP"}`

### Option B — render.yaml (Blueprint)

Push `backend/render.yaml` and connect via **New → Blueprint** in Render.  
Fill in secret values through the dashboard after importing.

### Verify seeding worked

Check your service logs for the credentials banner:

```
============================================================
  AAROHAN DEMO CREDENTIALS  (password: Demo123!)
============================================================
  ARTISTS
    artist1@aarohan.demo  →  Aarav Sharma
    artist2@aarohan.demo  →  Meera Joshi
    artist3@aarohan.demo  →  Kabir Khan
    artist4@aarohan.demo  →  Riya Patel
  VENUES
    venue1@aarohan.demo   →  Green Room Cafe
    venue2@aarohan.demo   →  Moonlight Coffee House
    venue3@aarohan.demo   →  Studio 27
    venue4@aarohan.demo   →  Riverside Arts Collective
============================================================
```

---

## 3 — Deploy the Frontend (Vercel)

1. In Vercel dashboard → **New Project** → import your GitHub repo
2. Set **Root Directory** to `frontend`
3. Set **Framework Preset** to `Vite`
4. Add this **Environment Variable**:

| Key | Value |
|-----|-------|
| `VITE_API_BASE_URL` | `https://<your-render-service>.onrender.com` |

5. Click **Deploy**

> **Important:** `frontend/vercel.json` is already committed and configures Vercel to rewrite all paths to `index.html` for React Router to work correctly on hard refresh.

### Verify

- Visit your Vercel URL → landing page loads
- Click **Login** → log in with `artist1@aarohan.demo` / `Demo123!`
- Navigate to `/artist/opportunities` → 8 opportunities appear
- Hard-refresh on `/artist/dashboard` → page still loads (not 404)

---

## 4 — CORS Checklist

After both services are deployed, verify:

1. Backend `CORS_ORIGINS` contains your exact Vercel URL (no trailing slash)
2. Vercel `VITE_API_BASE_URL` contains your exact Render URL (no trailing slash)
3. In browser Network tab: all API requests include `Authorization: Bearer <token>`
4. No `CORS` errors in browser console

---

## 5 — Production Hardening Checklist

Before going live:

- [ ] Replace the default `JWT_SECRET` with a cryptographically random 256-bit secret
- [ ] Set `DEMO_SEED=false` if you do not want demo data on production
- [ ] Enable Render's auto-scaling or set instance type appropriately
- [ ] Configure a custom domain on Vercel and update `CORS_ORIGINS` on Render
- [ ] Set up Render's alert notifications for service downtime

---

## 6 — Local Development (Docker Compose)

```bash
# Start all services (DB + backend + frontend)
docker-compose up --build

# Backend available at:  http://localhost:8080
# Frontend available at: http://localhost:3000
# Swagger UI:            http://localhost:8080/swagger-ui.html

# Demo credentials (seeded automatically):
# artist1@aarohan.demo … artist4@aarohan.demo  /  Demo123!
# venue1@aarohan.demo  … venue4@aarohan.demo   /  Demo123!
```

---

## 7 — Environment Variable Reference

### Backend (Render)

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/artistlink` | PostgreSQL JDBC URL |
| `DB_USER` | `artistlink` | Database username |
| `DB_PASSWORD` | `artistlink` | Database password |
| `JWT_SECRET` | *(dev key — change in prod)* | Base64-encoded 256-bit HMAC secret |
| `JWT_ACCESS_TTL` | `900` | Access token TTL in seconds (15 min) |
| `JWT_REFRESH_TTL` | `604800` | Refresh token TTL in seconds (7 days) |
| `CORS_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Comma-separated allowed origins |
| `SERVER_PORT` | `8080` | HTTP port |
| `DEMO_SEED` | `true` | Seed demo data on startup (set `false` in prod) |

### Frontend (Vercel)

| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_API_BASE_URL` | `/api` (dev proxy) | Full backend URL in production |
