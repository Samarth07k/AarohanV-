package com.artistlink.notification;

public enum NotificationType {
    NEW_APPLICATION,
    APPLICATION_STATUS_CHANGED,
    NEGOTIATION_OFFER_RECEIVED,
    NEGOTIATION_AGREED,
    BOOKING_CONFIRMED,
    BOOKING_CANCELLED,
    NEW_MESSAGE,
    OPPORTUNITY_CLOSED,
    // Social (Phase A/B) — present in enum, not produced by marketplace
    NEW_FOLLOWER,
    POST_LIKED,
    POST_COMMENTED,
    MENTIONED_IN_POST
}
