package com.artistlink.engagement;

import com.artistlink.auth.AuthPrincipal;
import com.artistlink.auth.CurrentActor;
import com.artistlink.engagement.dto.LikeActorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /** Idempotent — 200 if already liked (Blueprint 10.4). */
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> like(@PathVariable UUID postId) {
        AuthPrincipal actor = CurrentActor.require();
        likeService.like(actor, postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> unlike(@PathVariable UUID postId) {
        AuthPrincipal actor = CurrentActor.require();
        likeService.unlike(actor, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<LikeActorResponse>> list(@PathVariable UUID postId) {
        return ResponseEntity.ok(likeService.listActors(postId).stream()
                .map(LikeActorResponse::from).toList());
    }
}
