package com.artistlink.post;

import com.artistlink.auth.AuthPrincipal;
import com.artistlink.auth.CurrentActor;
import com.artistlink.common.AuthorType;
import com.artistlink.common.PageResponse;
import com.artistlink.post.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> create(@Valid @RequestBody CreatePostRequest req) {
        AuthPrincipal actor = CurrentActor.require();
        return ResponseEntity.ok(postService.create(actor, req));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.get(id));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody UpdatePostRequest req) {
        AuthPrincipal actor = CurrentActor.require();
        return ResponseEntity.ok(postService.update(actor, id, req));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        AuthPrincipal actor = CurrentActor.require();
        postService.softDelete(actor, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/artists/{artistId}/posts")
    public ResponseEntity<PageResponse<PostResponse>> artistPosts(
            @PathVariable UUID artistId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(postService.byAuthor(AuthorType.ARTIST, artistId, cursor, limit));
    }

    @GetMapping("/venues/{venueId}/posts")
    public ResponseEntity<PageResponse<PostResponse>> venuePosts(
            @PathVariable UUID venueId,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(postService.byAuthor(AuthorType.VENUE, venueId, cursor, limit));
    }
}
