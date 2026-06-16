package com.artistlink.common;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Cursor-based pagination per Blueprint 7.2:
 *   cursor = base64(created_at_of_last_item + "_" + id_of_last_item)
 * Query uses (created_at, id) < (:cursorCreatedAt, :cursorId) ORDER BY created_at DESC, id DESC.
 */
public final class CursorPagination {

    private CursorPagination() {}

    public record Cursor(Instant createdAt, UUID id) {}

    public static String encode(Instant createdAt, UUID id) {
        String raw = createdAt.toString() + "_" + id.toString();
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Cursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            int sep = raw.lastIndexOf('_');
            if (sep < 0) {
                return null;
            }
            Instant createdAt = Instant.parse(raw.substring(0, sep));
            UUID id = UUID.fromString(raw.substring(sep + 1));
            return new Cursor(createdAt, id);
        } catch (RuntimeException e) {
            return null; // malformed cursor → treat as first page
        }
    }
}
