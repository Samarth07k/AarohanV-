package com.artistlink.notification;

import com.artistlink.user.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "recipient_type", nullable = false, columnDefinition = "user_role")
    private UserRole recipientType;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "notification_type")
    private NotificationType type;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "related_entity_type", columnDefinition = "related_entity_type")
    private RelatedEntityType relatedEntityType;

    @Column(name = "related_entity_id")
    private UUID relatedEntityId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body = "";

    @Column(nullable = false)
    private int count = 1;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public UserRole getRecipientType() { return recipientType; }
    public void setRecipientType(UserRole v) { this.recipientType = v; }
    public UUID getRecipientId() { return recipientId; }
    public void setRecipientId(UUID v) { this.recipientId = v; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType v) { this.type = v; }
    public RelatedEntityType getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(RelatedEntityType v) { this.relatedEntityType = v; }
    public UUID getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(UUID v) { this.relatedEntityId = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getBody() { return body; }
    public void setBody(String v) { this.body = v; }
    public int getCount() { return count; }
    public void setCount(int v) { this.count = v; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant v) { this.readAt = v; }
    public Instant getCreatedAt() { return createdAt; }
}
