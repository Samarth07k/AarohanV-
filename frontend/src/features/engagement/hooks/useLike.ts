import { useMutation, useQueryClient } from '@tanstack/react-query';
import { engagementService } from '../services/engagementService';
import type { FeedItem } from '@/types/post';
import type { InfiniteData } from '@tanstack/react-query';
import type { PageResponse } from '@/types';

type FeedData = InfiniteData<PageResponse<FeedItem>>;

/**
 * Optimistic like toggle (Blueprint 8.5 / 19). Toggles isLikedByCurrentUser and
 * adjusts likeCount in the cached Home Feed, reverting on error.
 */
export function useLike() {
  const qc = useQueryClient();

  const patchFeed = (postId: string, liked: boolean) => {
    qc.setQueriesData<FeedData>({ queryKey: ['feed', 'home'] }, (old) => {
      if (!old) return old;
      return {
        ...old,
        pages: old.pages.map((page) => ({
          ...page,
          items: page.items.map((item) =>
            item.post.id === postId
              ? {
                  ...item,
                  isLikedByCurrentUser: liked,
                  post: { ...item.post, likeCount: item.post.likeCount + (liked ? 1 : -1) },
                }
              : item
          ),
        })),
      };
    });
  };

  return useMutation({
    mutationFn: ({ postId, liked }: { postId: string; liked: boolean }) =>
      liked ? engagementService.like(postId) : engagementService.unlike(postId),
    onMutate: async ({ postId, liked }) => {
      await qc.cancelQueries({ queryKey: ['feed', 'home'] });
      patchFeed(postId, liked);
      return { postId, liked };
    },
    onError: (_e, vars) => {
      // revert
      patchFeed(vars.postId, !vars.liked);
    },
    onSettled: (_d, _e, vars) => {
      qc.invalidateQueries({ queryKey: ['post', vars.postId] });
    },
  });
}
