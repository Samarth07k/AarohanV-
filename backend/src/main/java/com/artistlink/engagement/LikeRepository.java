package com.artistlink.engagement;

import com.artistlink.common.AuthorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByPostIdAndActorTypeAndActorId(UUID postId, AuthorType actorType, UUID actorId);

    void deleteByPostIdAndActorTypeAndActorId(UUID postId, AuthorType actorType, UUID actorId);

    List<Like> findByPostIdOrderByCreatedAtDesc(UUID postId);

    /** Which of these posts has the given actor liked? (feed projection) */
    @Query("""
        SELECT l.postId FROM Like l
        WHERE l.actorType = :actorType AND l.actorId = :actorId
          AND l.postId IN :postIds
    """)
    List<UUID> findLikedPostIds(@Param("actorType") AuthorType actorType,
                               @Param("actorId") UUID actorId,
                               @Param("postIds") List<UUID> postIds);
}
