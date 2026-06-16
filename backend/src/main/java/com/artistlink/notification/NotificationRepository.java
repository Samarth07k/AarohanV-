package com.artistlink.notification;

import com.artistlink.user.UserRole;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("""
        SELECT n FROM Notification n
        WHERE n.recipientType = :type AND n.recipientId = :id
        ORDER BY n.createdAt DESC, n.id DESC
    """)
    List<Notification> findFirstPage(@Param("type") UserRole type, @Param("id") UUID id, Limit limit);

    @Query("""
        SELECT n FROM Notification n
        WHERE n.recipientType = :type AND n.recipientId = :id
          AND (n.createdAt < :cursorCreatedAt
               OR (n.createdAt = :cursorCreatedAt AND n.id < :cursorId))
        ORDER BY n.createdAt DESC, n.id DESC
    """)
    List<Notification> findAfter(@Param("type") UserRole type, @Param("id") UUID id,
                                 @Param("cursorCreatedAt") Instant cursorCreatedAt,
                                 @Param("cursorId") UUID cursorId, Limit limit);

    long countByRecipientTypeAndRecipientIdAndReadAtIsNull(UserRole type, UUID id);
}
