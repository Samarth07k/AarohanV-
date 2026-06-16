import { motion } from 'framer-motion';
import type { FeedItem } from '@/types/post';
import { GlassCard } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { MessageCircle } from 'lucide-react';
import { LikeButton } from '@/features/engagement/components/LikeButton';
import { MediaGallery } from './MediaGallery';

function timeAgo(iso: string): string {
  const diff = Date.now() - new Date(iso).getTime();
  const m = Math.floor(diff / 60000);
  if (m < 1) return 'just now';
  if (m < 60) return `${m}m`;
  const h = Math.floor(m / 60);
  if (h < 24) return `${h}h`;
  return `${Math.floor(h / 24)}d`;
}

const TYPE_LABELS: Record<string, string> = {
  GENERAL: '', ANNOUNCEMENT: 'Announcement', EVENT_PHOTO: 'Event Photo',
  UPCOMING_EVENT: 'Upcoming Event', PERFORMANCE_CLIP: 'Performance',
  ACHIEVEMENT: 'Achievement', ARTIST_SPOTLIGHT: 'Spotlight',
  VENUE_UPDATE: 'Venue Update', VENUE_ACHIEVEMENT: 'Achievement',
};

export function PostCard({ item }: { item: FeedItem }) {
  const { post, author, mediaAttachments, isLikedByCurrentUser } = item;
  const initials = (author.displayName || '?').slice(0, 2).toUpperCase();
  const typeLabel = TYPE_LABELS[post.postType] ?? '';

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
      <GlassCard className="p-5 mb-4">
        <div className="flex items-center gap-3 mb-3">
          <Avatar className="h-10 w-10">
            {author.avatarUrl && <AvatarImage src={author.avatarUrl} />}
            <AvatarFallback>{initials}</AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0">
            <div className="font-semibold text-sm truncate">{author.displayName}</div>
            <div className="text-xs text-ink/40">
              <span className="capitalize">{author.type.toLowerCase()}</span> · {timeAgo(post.createdAt)}
            </div>
          </div>
          {typeLabel && (
            <span className="text-xs rounded-full bg-accent/10 px-2.5 py-0.5 text-accent font-medium">{typeLabel}</span>
          )}
        </div>

        {post.content && <p className="text-sm text-ink/80 mb-3 whitespace-pre-wrap">{post.content}</p>}
        {mediaAttachments.length > 0 && <div className="mb-3"><MediaGallery media={mediaAttachments} /></div>}

        <div className="flex items-center gap-5 pt-1">
          <LikeButton postId={post.id} liked={isLikedByCurrentUser} count={post.likeCount} />
          <span className="inline-flex items-center gap-1.5 text-sm text-ink/40">
            <MessageCircle className="h-4 w-4" /> {post.commentCount}
          </span>
        </div>
      </GlassCard>
    </motion.div>
  );
}
