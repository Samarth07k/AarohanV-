package com.artistlink.media;

import com.artistlink.common.exception.NotFoundException;
import com.artistlink.media.dto.AttachMediaRequest;
import com.artistlink.media.dto.UploadUrlRequest;
import com.artistlink.media.dto.UploadUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Media upload flow (Blueprint 10.2). In production this issues a pre-signed
 * S3/CDN PUT URL. Here it returns a local-disk-backed stub URL so the flow runs
 * end to end without cloud credentials. The client "uploads" to uploadUrl, then
 * attaches the resulting fileUrl to a post.
 */
@Service
public class MediaUploadService {

    private final MediaAttachmentRepository repository;
    private final String baseUrl;

    public MediaUploadService(MediaAttachmentRepository repository,
                              @Value("${artistlink.media.base-url}") String baseUrl) {
        this.repository = repository;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public UploadUrlResponse createUploadUrl(UploadUrlRequest req) {
        String key = UUID.randomUUID() + "-" + sanitize(req.fileName());
        String fileUrl = baseUrl + "/" + key;
        // uploadUrl == fileUrl for the local stub (PUT and GET same path)
        return new UploadUrlResponse(fileUrl, fileUrl, key);
    }

    public MediaAttachment attach(UUID postId, AttachMediaRequest req) {
        MediaAttachment m = new MediaAttachment();
        m.setPostId(postId);
        m.setMediaType(req.mediaType());
        m.setUrl(req.url());
        m.setThumbnailUrl(req.thumbnailUrl());
        m.setWidth(req.width());
        m.setHeight(req.height());
        m.setDurationSeconds(req.durationSeconds());
        m.setDisplayOrder(req.displayOrder() == null ? 0 : req.displayOrder());
        return repository.save(m);
    }

    public void remove(UUID postId, UUID mediaId) {
        MediaAttachment m = repository.findById(mediaId)
                .orElseThrow(() -> new NotFoundException("Media not found"));
        if (!m.getPostId().equals(postId)) {
            throw new NotFoundException("Media does not belong to this post");
        }
        repository.delete(m);
    }

    public List<MediaAttachment> forPost(UUID postId) {
        return repository.findByPostIdOrderByDisplayOrderAsc(postId);
    }

    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
