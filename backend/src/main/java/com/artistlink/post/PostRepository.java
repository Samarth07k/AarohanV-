package com.artistlink.post;

import com.artistlink.common.AuthorType;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("""
        SELECT p FROM Post p
        WHERE p.status = :status
          AND p.visibility = :visibility
        ORDER BY p.createdAt DESC, p.id DESC
    """)
    List<Post> findHomeFeedFirstPage(
            @Param("status") PostStatus status,
            @Param("visibility") PostVisibility visibility,
            Limit limit
    );

    @Query("""
        SELECT p FROM Post p
        WHERE p.status = :status
          AND p.visibility = :visibility
          AND (p.createdAt < :cursorCreatedAt
               OR (p.createdAt = :cursorCreatedAt AND p.id < :cursorId))
        ORDER BY p.createdAt DESC, p.id DESC
    """)
    List<Post> findHomeFeedAfter(
            @Param("status") PostStatus status,
            @Param("visibility") PostVisibility visibility,
            @Param("cursorCreatedAt") Instant cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Limit limit
    );

    @Query("""
        SELECT p FROM Post p
        WHERE p.authorType = :authorType
          AND p.authorId = :authorId
          AND p.status <> :deletedStatus
        ORDER BY p.createdAt DESC, p.id DESC
    """)
    List<Post> findByAuthorFirstPage(
            @Param("authorType") AuthorType authorType,
            @Param("authorId") UUID authorId,
            @Param("deletedStatus") PostStatus deletedStatus,
            Limit limit
    );

    @Query("""
        SELECT p FROM Post p
        WHERE p.authorType = :authorType
          AND p.authorId = :authorId
          AND p.status <> :deletedStatus
          AND (p.createdAt < :cursorCreatedAt
               OR (p.createdAt = :cursorCreatedAt AND p.id < :cursorId))
        ORDER BY p.createdAt DESC, p.id DESC
    """)
    List<Post> findByAuthorAfter(
            @Param("authorType") AuthorType authorType,
            @Param("authorId") UUID authorId,
            @Param("deletedStatus") PostStatus deletedStatus,
            @Param("cursorCreatedAt") Instant cursorCreatedAt,
            @Param("cursorId") UUID cursorId,
            Limit limit
    );
}