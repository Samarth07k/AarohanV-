import { useInfiniteQuery } from '@tanstack/react-query';
import { postService } from '../services/postService';
import type { AuthorType } from '@/types';

export function useAuthorPosts(authorType: AuthorType | undefined, authorId: string | undefined) {
  return useInfiniteQuery({
    queryKey: ['author-posts', authorType, authorId],
    queryFn: ({ pageParam }) => postService.byAuthor(authorType!, authorId!, pageParam as string | undefined),
    initialPageParam: undefined as string | undefined,
    getNextPageParam: (last) => last.nextCursor ?? undefined,
    enabled: Boolean(authorType && authorId),
  });
}
