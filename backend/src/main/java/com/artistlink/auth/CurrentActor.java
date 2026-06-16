package com.artistlink.auth;

import com.artistlink.common.exception.ForbiddenException;
import org.springframework.security.core.context.SecurityContextHolder;

/** Resolves the authenticated actor from the security context. */
public final class CurrentActor {
    private CurrentActor() {}

    public static AuthPrincipal require() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthPrincipal p)) {
            throw new ForbiddenException("Authentication required");
        }
        return p;
    }
}
