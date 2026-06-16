import { useHomeFeed } from '../hooks/useHomeFeed';
import { PostComposer } from '@/features/posts/components/PostComposer';
import { PostCard } from '@/features/posts/components/PostCard';
import { InfiniteScrollList } from '@/shared/components/InfiniteScrollList';
import { GlassCard } from '@/components/ui/card';
import { Loader2 } from 'lucide-react';

export function HomeFeed() {
  const { data, isLoading, hasNextPage, isFetchingNextPage, fetchNextPage } = useHomeFeed();
  const items = data?.pages.flatMap((p) => p.items) ?? [];

  return (
    <div>
      <PostComposer />
      {isLoading ? (
        <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>
      ) : (
        <InfiniteScrollList
          items={items}
          renderItem={(item) => <PostCard key={item.post.id} item={item} />}
          hasNextPage={Boolean(hasNextPage)}
          isFetchingNextPage={isFetchingNextPage}
          fetchNextPage={fetchNextPage}
          emptyMessage="No posts yet. Be the first to share something."
        />
      )}
    </div>
  );
}
