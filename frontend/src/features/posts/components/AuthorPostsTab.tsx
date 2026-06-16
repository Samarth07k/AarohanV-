import { useAuthorPosts } from '../hooks/useAuthorPosts';
import { AuthorPostCard } from './AuthorPostCard';
import { InfiniteScrollList } from '@/shared/components/InfiniteScrollList';
import { GlassCard } from '@/components/ui/card';
import { Loader2 } from 'lucide-react';
import type { AuthorType } from '@/types';

export function AuthorPostsTab({ authorType, authorId }: { authorType: AuthorType; authorId: string }) {
  const { data, isLoading, hasNextPage, isFetchingNextPage, fetchNextPage } = useAuthorPosts(authorType, authorId);
  const posts = data?.pages.flatMap((p) => p.items) ?? [];

  if (isLoading) {
    return <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>;
  }
  return (
    <InfiniteScrollList
      items={posts}
      renderItem={(post) => <AuthorPostCard key={post.id} post={post as any} />}
      hasNextPage={Boolean(hasNextPage)}
      isFetchingNextPage={isFetchingNextPage}
      fetchNextPage={fetchNextPage}
      emptyMessage="No posts yet."
    />
  );
}
