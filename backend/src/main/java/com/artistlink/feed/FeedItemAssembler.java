package com.artistlink.feed;

import com.artistlink.auth.AuthPrincipal;
import com.artistlink.common.AuthorType;
import com.artistlink.common.PolymorphicAuthorValidator;
import com.artistlink.engagement.LikeService;
import com.artistlink.media.MediaAttachment;
import com.artistlink.media.MediaAttachmentRepository;
import com.artistlink.post.Post;
import com.artistlink.post.dto.FeedItemResponse;
import com.artistlink.post.dto.MediaResponse;
import com.artistlink.post.dto.PostResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Read-time feed composition (Blueprint 7.5 / 10.8). Assembles a list of posts
 * into denormalized FeedItem projections in a bounded number of queries: one
 * batch media fetch, one batch "liked by current actor" fetch, and author
 * identity resolution. No N+1 per item.
 *
 * isFollowingAuthor is always false until the Follow module ships in Phase B;
 * the field is part of the contract now so the frontend shape is stable.
 */
@Component
public class FeedItemAssembler {

    private final MediaAttachmentRepository mediaRepository;
    private final LikeService likeService;
    private final PolymorphicAuthorValidator authorValidator;

    public FeedItemAssembler(MediaAttachmentRepository mediaRepository, LikeService likeService,
                             PolymorphicAuthorValidator authorValidator) {
        this.mediaRepository = mediaRepository;
        this.likeService = likeService;
        this.authorValidator = authorValidator;
    }

    public List<FeedItemResponse> assemble(List<Post> posts, AuthPrincipal currentActor) {
        if (posts.isEmpty()) return List.of();

        List<UUID> postIds = posts.stream().map(Post::getId).toList();

        // Batch media for all posts
        Map<UUID, List<MediaResponse>> mediaByPost = mediaRepository
                .findByPostIdInOrderByDisplayOrderAsc(postIds).stream()
                .collect(Collectors.groupingBy(MediaAttachment::getPostId,
                        Collectors.mapping(MediaResponse::from, Collectors.toList())));

        // Batch "liked by current actor"
        Set<UUID> likedPostIds = currentActor == null ? Set.of()
                : Set.copyOf(likeService.likedAmong(currentActor.authorType(), currentActor.authorId(), postIds));

        return posts.stream().map(p -> {
            List<MediaResponse> media = mediaByPost.getOrDefault(p.getId(), List.of());
            String displayName = authorValidator.resolveDisplayName(p.getAuthorType(), p.getAuthorId());
            String avatarUrl = authorValidator.resolveAvatarUrl(p.getAuthorType(), p.getAuthorId());
            PostResponse postResponse = PostResponse.from(p, media);
            return new FeedItemResponse(
                    postResponse,
                    new FeedItemResponse.AuthorInfo(p.getAuthorType(), p.getAuthorId(), displayName, avatarUrl),
                    media,
                    likedPostIds.contains(p.getId()),
                    false /* isFollowingAuthor — Phase B */
            );
        }).toList();
    }
}
