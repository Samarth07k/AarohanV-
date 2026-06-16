package com.artistlink.media;

import com.artistlink.auth.CurrentActor;
import com.artistlink.media.dto.AttachMediaRequest;
import com.artistlink.media.dto.UploadUrlRequest;
import com.artistlink.media.dto.UploadUrlResponse;
import com.artistlink.post.dto.MediaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class MediaController {

    private final MediaUploadService mediaUploadService;

    public MediaController(MediaUploadService mediaUploadService) {
        this.mediaUploadService = mediaUploadService;
    }

    /** Pre-signed upload URL (Blueprint 10.2). */
    @PostMapping("/media/upload-url")
    public ResponseEntity<UploadUrlResponse> uploadUrl(@Valid @RequestBody UploadUrlRequest req) {
        CurrentActor.require();
        return ResponseEntity.ok(mediaUploadService.createUploadUrl(req));
    }

    @PostMapping("/posts/{postId}/media")
    public ResponseEntity<MediaResponse> attach(@PathVariable UUID postId,
                                                @Valid @RequestBody AttachMediaRequest req) {
        CurrentActor.require();
        return ResponseEntity.ok(MediaResponse.from(mediaUploadService.attach(postId, req)));
    }

    @DeleteMapping("/posts/{postId}/media/{mediaId}")
    public ResponseEntity<Void> remove(@PathVariable UUID postId, @PathVariable UUID mediaId) {
        CurrentActor.require();
        mediaUploadService.remove(postId, mediaId);
        return ResponseEntity.noContent().build();
    }
}
