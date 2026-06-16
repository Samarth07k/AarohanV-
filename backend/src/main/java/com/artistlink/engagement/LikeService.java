package com.artistlink.engagement;

import com.artistlink.auth.AuthPrincipal;
import com.artistlink.common.AuthorType;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.post.Post;
import com.artistlink.post.PostRepository;
import com.artistlink.post.PostStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    /** Idempotent (Blueprint 10.4): 200 if already liked, no double count. */
    @Transactional
    public void like(AuthPrincipal actor, UUID postId) {
        Post post = requirePost(postId);
        boolean exists = likeRepository.existsByPostIdAndActorTypeAndActorId(
                postId, actor.authorType(), actor.authorId());
        if (exists) return;

        Like like = new Like();
        like.setPostId(postId);
        like.setActorType(actor.authorType());
        like.setActorId(actor.authorId());
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
        // POST_LIKED notification (batched) is wired in Phase C.
    }

    @Transactional
    public void unlike(AuthPrincipal actor, UUID postId) {
        Post post = requirePost(postId);
        boolean exists = likeRepository.existsByPostIdAndActorTypeAndActorId(
                postId, actor.authorType(), actor.authorId());
        if (!exists) return;

        likeRepository.deleteByPostIdAndActorTypeAndActorId(
                postId, actor.authorType(), actor.authorId());
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<Like> listActors(UUID postId) {
        requirePost(postId);
        return likeRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    public List<UUID> likedAmong(AuthorType actorType, UUID actorId, List<UUID> postIds) {
        if (postIds.isEmpty()) return List.of();
        return likeRepository.findLikedPostIds(actorType, actorId, postIds);
    }

    private Post requirePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (post.getStatus() == PostStatus.DELETED) {
            throw new NotFoundException("Post not found");
        }
        return post;
    }
}
