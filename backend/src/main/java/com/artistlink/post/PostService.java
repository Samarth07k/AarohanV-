package com.artistlink.post;

import com.artistlink.artist.Artist;
import com.artistlink.artist.ArtistRepository;
import com.artistlink.auth.AuthPrincipal;
import com.artistlink.common.AuthorType;
import com.artistlink.common.CursorPagination;
import com.artistlink.common.PageResponse;
import com.artistlink.common.PolymorphicAuthorValidator;
import com.artistlink.common.exception.ForbiddenException;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.media.MediaAttachment;
import com.artistlink.media.MediaAttachmentRepository;
import com.artistlink.post.dto.*;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private static final int DEFAULT_PAGE = 20;

    private final PostRepository postRepository;
    private final MediaAttachmentRepository mediaRepository;
    private final ArtistRepository artistRepository;
    private final VenueRepository venueRepository;
    private final PolymorphicAuthorValidator authorValidator;

    public PostService(PostRepository postRepository, MediaAttachmentRepository mediaRepository,
                       ArtistRepository artistRepository, VenueRepository venueRepository,
                       PolymorphicAuthorValidator authorValidator) {
        this.postRepository = postRepository;
        this.mediaRepository = mediaRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.authorValidator = authorValidator;
    }

    @Transactional
    public PostResponse create(AuthPrincipal actor, CreatePostRequest req) {
        authorValidator.validateExists(actor.authorType(), actor.authorId());

        Post post = new Post();
        post.setAuthorType(actor.authorType());
        post.setAuthorId(actor.authorId());
        post.setPostType(req.postType());
        post.setContent(req.content() == null ? "" : req.content());
        post.setLinkedEntityType(req.linkedEntityType());
        post.setLinkedEntityId(req.linkedEntityId());
        if (req.visibility() != null) post.setVisibility(req.visibility());
        post = postRepository.save(post);

        // Link any pre-uploaded media to this post
        List<MediaResponse> media = List.of();
        if (req.mediaIds() != null && !req.mediaIds().isEmpty()) {
            List<MediaAttachment> attachments = mediaRepository.findAllById(req.mediaIds());
            int order = 0;
            for (MediaAttachment m : attachments) {
                m.setPostId(post.getId());
                m.setDisplayOrder(order++);
            }
            mediaRepository.saveAll(attachments);
            media = attachments.stream().map(MediaResponse::from).toList();
        }

        incrementPostsCount(actor.authorType(), actor.authorId(), +1);
        return PostResponse.from(post, media);
    }

    @Transactional(readOnly = true)
    public PostResponse get(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (post.getStatus() == PostStatus.DELETED) {
            throw new NotFoundException("Post not found");
        }
        return PostResponse.from(post, mediaFor(post.getId()));
    }

    @Transactional
    public PostResponse update(AuthPrincipal actor, UUID id, UpdatePostRequest req) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        requireAuthor(actor, post);
        if (req.content() != null) post.setContent(req.content());
        if (req.visibility() != null) post.setVisibility(req.visibility());
        post = postRepository.save(post);
        return PostResponse.from(post, mediaFor(post.getId()));
    }

    @Transactional
    public void softDelete(AuthPrincipal actor, UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        requireAuthor(actor, post);
        if (post.getStatus() != PostStatus.DELETED) {
            post.setStatus(PostStatus.DELETED);
            postRepository.save(post);
            incrementPostsCount(post.getAuthorType(), post.getAuthorId(), -1);
        }
    }

    @Transactional(readOnly = true)
public PageResponse<PostResponse> byAuthor(
        AuthorType authorType,
        UUID authorId,
        String cursor,
        Integer limit
) {
    int size = (limit == null || limit <= 0 || limit > 50)
            ? DEFAULT_PAGE
            : limit;

    CursorPagination.Cursor c = CursorPagination.decode(cursor);

    List<Post> posts = (c == null)
            ? postRepository.findByAuthorFirstPage(
                    authorType,
                    authorId,
                    PostStatus.DELETED,
                    Limit.of(size + 1)
            )
            : postRepository.findByAuthorAfter(
                    authorType,
                    authorId,
                    PostStatus.DELETED,
                    c.createdAt(),
                    c.id(),
                    Limit.of(size + 1)
            );

    return toPage(posts, size);
}

    private PageResponse<PostResponse> toPage(List<Post> posts, int size) {
        boolean hasMore = posts.size() > size;
        List<Post> pagePosts = hasMore ? posts.subList(0, size) : posts;
        List<PostResponse> items = pagePosts.stream()
                .map(p -> PostResponse.from(p, mediaFor(p.getId())))
                .toList();
        String next = null;
        if (hasMore && !pagePosts.isEmpty()) {
            Post last = pagePosts.get(pagePosts.size() - 1);
            next = CursorPagination.encode(last.getCreatedAt(), last.getId());
        }
        return PageResponse.of(items, next);
    }

    private List<MediaResponse> mediaFor(UUID postId) {
        return mediaRepository.findByPostIdOrderByDisplayOrderAsc(postId)
                .stream().map(MediaResponse::from).toList();
    }

    private void requireAuthor(AuthPrincipal actor, Post post) {
        if (post.getAuthorType() != actor.authorType() || !post.getAuthorId().equals(actor.authorId())) {
            throw new ForbiddenException("Only the author can modify this post");
        }
    }

    private void incrementPostsCount(AuthorType type, UUID id, int delta) {
        if (type == AuthorType.ARTIST) {
            Artist a = artistRepository.findById(id).orElseThrow();
            a.setPostsCount(Math.max(0, a.getPostsCount() + delta));
            artistRepository.save(a);
        } else {
            Venue v = venueRepository.findById(id).orElseThrow();
            v.setPostsCount(Math.max(0, v.getPostsCount() + delta));
            venueRepository.save(v);
        }
    }
}
