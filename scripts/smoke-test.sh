#!/usr/bin/env bash
# ArtistLink marketplace end-to-end smoke test.
# Requires the stack running (docker compose up). Uses the API at :8080.
# Exercises: venue creates opportunity -> artist applies -> venue accepts ->
# offers exchanged -> accept -> booking + conversation -> message both ways.
set -euo pipefail
API="${API:-http://localhost:8080}"
j() { python3 -c "import sys,json;print(json.load(sys.stdin)$1)"; }

echo "== Register venue =="
VENUE=$(curl -s -X POST "$API/auth/register" -H 'Content-Type: application/json' -d '{
  "email":"venue+'"$RANDOM"'@demo.test","password":"password123","role":"VENUE",
  "displayName":"The Greenhouse","location":"Berlin"}')
VTOKEN=$(echo "$VENUE" | j "['accessToken']")
echo "venue token acquired"

echo "== Register artist =="
ARTIST=$(curl -s -X POST "$API/auth/register" -H 'Content-Type: application/json' -d '{
  "email":"artist+'"$RANDOM"'@demo.test","password":"password123","role":"ARTIST",
  "displayName":"Luna Vega","location":"Lisbon"}')
ATOKEN=$(echo "$ARTIST" | j "['accessToken']")
echo "artist token acquired"

echo "== Venue creates opportunity =="
OPP=$(curl -s -X POST "$API/opportunities" -H "Authorization: Bearer $VTOKEN" -H 'Content-Type: application/json' -d '{
  "title":"Friday Night Live","description":"Jazz set, 2x45min","budgetMin":400,"budgetMax":600}')
OPP_ID=$(echo "$OPP" | j "['id']")
echo "opportunity: $OPP_ID"

echo "== Artist discovers + applies =="
curl -s "$API/opportunities" -H "Authorization: Bearer $ATOKEN" >/dev/null
APP=$(curl -s -X POST "$API/applications" -H "Authorization: Bearer $ATOKEN" -H 'Content-Type: application/json' -d '{
  "opportunityId":"'"$OPP_ID"'","coverMessage":"Would love to play this set."}')
APP_ID=$(echo "$APP" | j "['id']")
echo "application: $APP_ID"

echo "== Venue accepts application (opens negotiation) =="
curl -s -X PATCH "$API/applications/$APP_ID/status" -H "Authorization: Bearer $VTOKEN" -H 'Content-Type: application/json' -d '{"status":"ACCEPTED"}' >/dev/null
NEG=$(curl -s "$API/negotiations/by-application/$APP_ID" -H "Authorization: Bearer $VTOKEN")
NEG_ID=$(echo "$NEG" | j "['id']")
echo "negotiation: $NEG_ID"

echo "== Venue sends offer =="
curl -s -X POST "$API/negotiations/$NEG_ID/offers" -H "Authorization: Bearer $VTOKEN" -H 'Content-Type: application/json' -d '{"amount":500,"terms":"2x45min, sound check included"}' >/dev/null

echo "== Artist counter-offers =="
curl -s -X POST "$API/negotiations/$NEG_ID/offers" -H "Authorization: Bearer $ATOKEN" -H 'Content-Type: application/json' -d '{"amount":550,"terms":"OK with sound check"}' >/dev/null

echo "== Venue accepts -> booking + conversation =="
RESULT=$(curl -s -X POST "$API/negotiations/$NEG_ID/accept" -H "Authorization: Bearer $VTOKEN")
BOOKING_ID=$(echo "$RESULT" | j "['bookingId']")
echo "booking: $BOOKING_ID"

echo "== Fetch booking + conversation =="
BOOKINGS=$(curl -s "$API/bookings/mine" -H "Authorization: Bearer $ATOKEN")
CONV_ID=$(echo "$BOOKINGS" | j "[0]['conversationId']")
echo "conversation: $CONV_ID"

echo "== Message both ways =="
curl -s -X POST "$API/conversations/$CONV_ID/messages" -H "Authorization: Bearer $VTOKEN" -H 'Content-Type: application/json' -d '{"content":"Looking forward to Friday!"}' >/dev/null
curl -s -X POST "$API/conversations/$CONV_ID/messages" -H "Authorization: Bearer $ATOKEN" -H 'Content-Type: application/json' -d '{"content":"Me too, see you then."}' >/dev/null
MSGS=$(curl -s "$API/conversations/$CONV_ID/messages" -H "Authorization: Bearer $ATOKEN")
COUNT=$(echo "$MSGS" | python3 -c "import sys,json;print(len(json.load(sys.stdin)))")
echo "messages in thread: $COUNT"

echo "== Notifications =="
echo "venue unread: $(curl -s "$API/notifications/unread-count" -H "Authorization: Bearer $VTOKEN" | j "['count']")"
echo "artist unread: $(curl -s "$API/notifications/unread-count" -H "Authorization: Bearer $ATOKEN" | j "['count']")"

echo ""
echo "✅ End-to-end marketplace flow completed successfully."
