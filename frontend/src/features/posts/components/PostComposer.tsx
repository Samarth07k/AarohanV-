import { useState, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ImagePlus, X, Loader2 } from 'lucide-react';
import type { PostType } from '@/types/post';
import { GlassCard } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/input';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { PostTypeSelector } from './PostTypeSelector';
import { useCreatePost } from '../hooks/useCreatePost';
import { useMediaUpload } from '../hooks/useMediaUpload';
import { mediaUploadService } from '../services/mediaUploadService';
import { postService } from '../services/postService';
import { useAuthStore } from '@/store/authStore';
import { useQueryClient } from '@tanstack/react-query';

interface Pending { previewUrl: string; storedUrl: string; width?: number; height?: number; }

export function PostComposer() {
  const actor = useAuthStore((s) => s.actor);
  const qc = useQueryClient();
  const [postType, setPostType] = useState<PostType>('GENERAL');
  const [content, setContent] = useState('');
  const [media, setMedia] = useState<Pending | null>(null);
  const fileRef = useRef<HTMLInputElement>(null);

  const upload = useMediaUpload();
  const create = useCreatePost();
  const initials = (actor?.displayName || '?').slice(0, 2).toUpperCase();

  async function onPickFile(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    const previewUrl = URL.createObjectURL(file);
    const { url, width, height } = await upload.mutateAsync(file);
    setMedia({ previewUrl, storedUrl: url, width, height });
  }

  async function submit() {
    if (!content.trim() && !media) return;
    // Create the post, then attach media (Blueprint 9.1 flow)
    const post = await postService.create({ postType, content: content.trim() });
    if (media) {
      await mediaUploadService.attach(post.id, {
        mediaType: 'IMAGE', url: media.storedUrl,
        width: media.width, height: media.height, displayOrder: 0,
      });
    }
    qc.invalidateQueries({ queryKey: ['feed', 'home'] });
    qc.invalidateQueries({ queryKey: ['author-posts'] });
    setContent(''); setMedia(null); setPostType('GENERAL');
  }

  const busy = create.isPending || upload.isPending;

  return (
    <GlassCard className="p-5 mb-4">
      <div className="flex gap-3">
        <Avatar className="h-10 w-10 shrink-0">
          {actor?.avatarUrl && <AvatarImage src={actor.avatarUrl} />}
          <AvatarFallback>{initials}</AvatarFallback>
        </Avatar>
        <div className="flex-1 min-w-0">
          <Textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Share an update, a clip, a photo…"
            className="border-0 bg-transparent px-0 focus-visible:ring-0 min-h-[64px] resize-none"
          />

          <AnimatePresence>
            {media && (
              <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }} exit={{ opacity: 0, height: 0 }}
                className="relative mb-3 rounded-card overflow-hidden">
                <img src={media.previewUrl} alt="" className="w-full max-h-64 object-cover" />
                <button onClick={() => setMedia(null)}
                  className="absolute top-2 right-2 rounded-full bg-ink/60 p-1 text-white hover:bg-ink">
                  <X className="h-4 w-4" />
                </button>
              </motion.div>
            )}
          </AnimatePresence>

          <div className="mb-3"><PostTypeSelector value={postType} onChange={setPostType} /></div>

          <div className="flex items-center justify-between">
            <Button type="button" variant="ghost" size="sm" onClick={() => fileRef.current?.click()} disabled={busy}>
              {upload.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <ImagePlus className="h-4 w-4" />}
              Photo
            </Button>
            <input ref={fileRef} type="file" accept="image/*" hidden onChange={onPickFile} />
            <Button onClick={submit} disabled={busy || (!content.trim() && !media)}>
              {create.isPending ? 'Posting…' : 'Post'}
            </Button>
          </div>
        </div>
      </div>
    </GlassCard>
  );
}
