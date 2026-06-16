package com.artistlink.post;

import com.artistlink.common.AuthorType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "author_type", nullable = false, columnDefinition = "post_author_type")
    private AuthorType authorType;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "post_type", nullable = false, columnDefinition = "post_type")
    private PostType postType;

    @Column(nullable = false)
    private String content = "";

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "linked_entity_type", columnDefinition = "linked_entity_type")
    private LinkedEntityType linkedEntityType;

    @Column(name = "linked_entity_id")
    private UUID linkedEntityId;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private int commentCount = 0;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "post_visibility")
    private PostVisibility visibility = PostVisibility.PUBLIC;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "post_status")
    private PostStatus status = PostStatus.PUBLISHED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public AuthorType getAuthorType() { return authorType; }
    public void setAuthorType(AuthorType authorType) { this.authorType = authorType; }
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public PostType getPostType() { return postType; }
    public void setPostType(PostType postType) { this.postType = postType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LinkedEntityType getLinkedEntityType() { return linkedEntityType; }
    public void setLinkedEntityType(LinkedEntityType v) { this.linkedEntityType = v; }
    public UUID getLinkedEntityId() { return linkedEntityId; }
    public void setLinkedEntityId(UUID v) { this.linkedEntityId = v; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int v) { this.likeCount = v; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int v) { this.commentCount = v; }
    public PostVisibility getVisibility() { return visibility; }
    public void setVisibility(PostVisibility v) { this.visibility = v; }
    public PostStatus getStatus() { return status; }
    public void setStatus(PostStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
