package com.artistlink.feed;

import com.artistlink.auth.AuthPrincipal;
import com.artistlink.common.CursorPagination;
import com.artistlink.common.PageResponse;
import com.artistlink.post.Post;
import com.artistlink.post.PostRepository;
import com.artistlink.post.PostStatus;
import com.artistlink.post.PostVisibility;
import com.artistlink.post.dto.FeedItemResponse;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedService {

    private static final int DEFAULT_PAGE = 20;

    private final PostRepository postRepository;
    private final FeedItemAssembler assembler;

    public FeedService(PostRepository postRepository, FeedItemAssembler assembler) {
        this.postRepository = postRepository;
        this.assembler = assembler;
    }

    @Transactional(readOnly = true)
    public PageResponse<FeedItemResponse> homeFeed(
            AuthPrincipal currentActor,
            String cursor,
            Integer limit
    ) {
        int size = (limit == null || limit <= 0 || limit > 50)
                ? DEFAULT_PAGE
                : limit;

        CursorPagination.Cursor c = CursorPagination.decode(cursor);

        List<Post> posts = (c == null)
                ? postRepository.findHomeFeedFirstPage(
                        PostStatus.PUBLISHED,
                        PostVisibility.PUBLIC,
                        Limit.of(size + 1)
                )
                : postRepository.findHomeFeedAfter(
                        PostStatus.PUBLISHED,
                        PostVisibility.PUBLIC,
                        c.createdAt(),
                        c.id(),
                        Limit.of(size + 1)
                );

        boolean hasMore = posts.size() > size;
        List<Post> page = hasMore
                ? posts.subList(0, size)
                : posts;

        List<FeedItemResponse> items = assembler.assemble(page, currentActor);

        String next = null;
        if (hasMore && !page.isEmpty()) {
            Post last = page.get(page.size() - 1);
            next = CursorPagination.encode(
                    last.getCreatedAt(),
                    last.getId()
            );
        }

        return PageResponse.of(items, next);
    }
}