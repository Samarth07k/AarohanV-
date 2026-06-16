package com.artistlink.notification;

import com.artistlink.common.CursorPagination;
import com.artistlink.common.PageResponse;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.notification.dto.NotificationResponse;
import com.artistlink.user.UserRole;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final int PAGE = 20;
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    /** Emit a marketplace notification. recipientType maps ARTIST/VENUE to user_role. */
    @Transactional
    public void emit(UserRole recipientType, UUID recipientId, NotificationType type,
                     RelatedEntityType relatedType, UUID relatedId, String title, String body) {
        Notification n = new Notification();
        n.setRecipientType(recipientType);
        n.setRecipientId(recipientId);
        n.setType(type);
        n.setRelatedEntityType(relatedType);
        n.setRelatedEntityId(relatedId);
        n.setTitle(title);
        n.setBody(body == null ? "" : body);
        repository.save(n);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(UserRole type, UUID id, String cursor, Integer limit) {
        int size = (limit == null || limit <= 0 || limit > 50) ? PAGE : limit;
        CursorPagination.Cursor c = CursorPagination.decode(cursor);
        List<Notification> rows = (c == null)
                ? repository.findFirstPage(type, id, Limit.of(size + 1))
                : repository.findAfter(type, id, c.createdAt(), c.id(), Limit.of(size + 1));
        boolean hasMore = rows.size() > size;
        List<Notification> page = hasMore ? rows.subList(0, size) : rows;
        List<NotificationResponse> items = page.stream().map(NotificationResponse::from).toList();
        String next = null;
        if (hasMore && !page.isEmpty()) {
            Notification last = page.get(page.size() - 1);
            next = CursorPagination.encode(last.getCreatedAt(), last.getId());
        }
        return PageResponse.of(items, next);
    }

    @Transactional(readOnly = true)
    public long unreadCount(UserRole type, UUID id) {
        return repository.countByRecipientTypeAndRecipientIdAndReadAtIsNull(type, id);
    }

    @Transactional
    public void markRead(UserRole type, UUID id, UUID notificationId) {
        Notification n = repository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        if (n.getRecipientType() != type || !n.getRecipientId().equals(id)) {
            throw new NotFoundException("Notification not found");
        }
        if (n.getReadAt() == null) {
            n.setReadAt(Instant.now());
            repository.save(n);
        }
    }

    @Transactional
    public void markAllRead(UserRole type, UUID id) {
        repository.findFirstPage(type, id, Limit.of(500)).forEach(n -> {
            if (n.getReadAt() == null) {
                n.setReadAt(Instant.now());
                repository.save(n);
            }
        });
    }
}
