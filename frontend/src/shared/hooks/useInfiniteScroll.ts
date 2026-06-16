import { useEffect, useRef } from 'react';

/**
 * Intersection Observer trigger (Blueprint 7.3). Calls onIntersect when the
 * sentinel enters the viewport. Reused by Feed, Comments, Followers/Following,
 * and Notifications.
 */
export function useInfiniteScroll(
  onIntersect: () => void,
  options: { enabled?: boolean; rootMargin?: string } = {}
) {
  const { enabled = true, rootMargin = '200px' } = options;
  const sentinelRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const el = sentinelRef.current;
    if (!el || !enabled) return;
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0]?.isIntersecting) onIntersect();
      },
      { rootMargin }
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, [onIntersect, enabled, rootMargin]);

  return sentinelRef;
}
