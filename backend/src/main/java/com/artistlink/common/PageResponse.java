package com.artistlink.common;

import java.util.List;

/** Standard cursor-paginated response: { items, nextCursor }. */
public record PageResponse<T>(List<T> items, String nextCursor) {
    public static <T> PageResponse<T> of(List<T> items, String nextCursor) {
        return new PageResponse<>(items, nextCursor);
    }
}
