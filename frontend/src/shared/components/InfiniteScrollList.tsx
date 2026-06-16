import { type ReactNode } from 'react';
import { useInfiniteScroll } from '@/shared/hooks/useInfiniteScroll';
import { Loader2 } from 'lucide-react';

interface InfiniteScrollListProps<T> {
  items: T[];
  renderItem: (item: T, index: number) => ReactNode;
  hasNextPage: boolean;
  isFetchingNextPage: boolean;
  fetchNextPage: () => void;
  emptyMessage?: string;
  className?: string;
}

/**
 * Generic infinite-scroll list (Blueprint 5: shared/components/InfiniteScrollList).
 * Wraps an Intersection Observer sentinel; reused by Feed and Comments.
 */
export function InfiniteScrollList<T>({
  items,
  renderItem,
  hasNextPage,
  isFetchingNextPage,
  fetchNextPage,
  emptyMessage = 'Nothing here yet.',
  className,
}: InfiniteScrollListProps<T>) {
  const sentinelRef = useInfiniteScroll(fetchNextPage, {
    enabled: hasNextPage && !isFetchingNextPage,
  });

  if (items.length === 0 && !isFetchingNextPage) {
    return <p className="py-12 text-center text-ink/50">{emptyMessage}</p>;
  }

  return (
    <div className={className}>
      {items.map((item, i) => renderItem(item, i))}
      <div ref={sentinelRef} className="h-8" />
      {isFetchingNextPage && (
        <div className="flex justify-center py-6 text-primary/60">
          <Loader2 className="h-5 w-5 animate-spin" />
        </div>
      )}
    </div>
  );
}
