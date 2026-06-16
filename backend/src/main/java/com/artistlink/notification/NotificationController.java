package com.artistlink.notification;

import com.artistlink.auth.AuthPrincipal;
import com.artistlink.auth.CurrentActor;
import com.artistlink.common.AuthorType;
import com.artistlink.common.PageResponse;
import com.artistlink.notification.dto.NotificationResponse;
import com.artistlink.user.UserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    private UserRole role(AuthPrincipal p) {
        return p.authorType() == AuthorType.ARTIST ? UserRole.ARTIST : UserRole.VENUE;
    }

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> list(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit) {
        AuthPrincipal p = CurrentActor.require();
        return ResponseEntity.ok(service.list(role(p), p.authorId(), cursor, limit));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        AuthPrincipal p = CurrentActor.require();
        return ResponseEntity.ok(Map.of("count", service.unreadCount(role(p), p.authorId())));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable UUID id) {
        AuthPrincipal p = CurrentActor.require();
        service.markRead(role(p), p.authorId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        AuthPrincipal p = CurrentActor.require();
        service.markAllRead(role(p), p.authorId());
        return ResponseEntity.noContent().build();
    }
}
