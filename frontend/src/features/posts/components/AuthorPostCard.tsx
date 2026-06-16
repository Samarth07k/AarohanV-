import { motion } from 'framer-motion';
import type { Post } from '@/types/post';
import type { MediaAttachment } from '@/types/mediaAttachment';
import { GlassCard } from '@/components/ui/card';
import { Heart, MessageCircle } from 'lucide-react';
import { MediaGallery } from './MediaGallery';

// PostResponse from the author endpoint includes mediaAttachments
type AuthorPost = Post & { mediaAttachments?: MediaAttachment[] };

export function AuthorPostCard({ post }: { post: AuthorPost }) {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
      <GlassCard className="p-5 mb-4">
        {post.content && <p className="text-sm text-ink/80 mb-3 whitespace-pre-wrap">{post.content}</p>}
        {post.mediaAttachments && post.mediaAttachments.length > 0 && (
          <div className="mb-3"><MediaGallery media={post.mediaAttachments} /></div>
        )}
        <div className="flex items-center gap-5 text-sm text-ink/40">
          <span className="inline-flex items-center gap-1.5"><Heart className="h-4 w-4" /> {post.likeCount}</span>
          <span className="inline-flex items-center gap-1.5"><MessageCircle className="h-4 w-4" /> {post.commentCount}</span>
        </div>
      </GlassCard>
    </motion.div>
  );
}
