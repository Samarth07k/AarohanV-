import { Heart } from 'lucide-react';
import { motion } from 'framer-motion';
import { useLike } from '../hooks/useLike';
import { cn } from '@/lib/utils';

export function LikeButton({ postId, liked, count }: { postId: string; liked: boolean; count: number }) {
  const like = useLike();
  return (
    <button
      onClick={() => like.mutate({ postId, liked: !liked })}
      className={cn('inline-flex items-center gap-1.5 text-sm transition-colors',
        liked ? 'text-destructive' : 'text-ink/50 hover:text-destructive')}
      aria-pressed={liked}
    >
      <motion.span whileTap={{ scale: 1.3 }}>
        <Heart className={cn('h-4 w-4', liked && 'fill-current')} />
      </motion.span>
      <span>{count}</span>
    </button>
  );
}
