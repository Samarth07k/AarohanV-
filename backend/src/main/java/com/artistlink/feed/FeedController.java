package com.artistlink.feed;

import com.artistlink.auth.AuthPrincipal;
import com.artistlink.auth.CurrentActor;
import com.artistlink.common.PageResponse;
import com.artistlink.post.dto.FeedItemResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/home")
    public ResponseEntity<PageResponse<FeedItemResponse>> home(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit) {
        AuthPrincipal actor = CurrentActor.require();
        return ResponseEntity.ok(feedService.homeFeed(actor, cursor, limit));
    }
}
