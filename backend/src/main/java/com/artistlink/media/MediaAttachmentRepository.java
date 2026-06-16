package com.artistlink.media;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MediaAttachmentRepository extends JpaRepository<MediaAttachment, UUID> {
    List<MediaAttachment> findByPostIdOrderByDisplayOrderAsc(UUID postId);
    List<MediaAttachment> findByPostIdInOrderByDisplayOrderAsc(List<UUID> postIds);
    long countByPostId(UUID postId);
}
