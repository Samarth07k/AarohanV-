import type { MediaAttachment } from '@/types/mediaAttachment';
import { cn } from '@/lib/utils';

export function MediaGallery({ media }: { media: MediaAttachment[] }) {
  if (!media.length) return null;
  return (
    <div className={cn('grid gap-2 rounded-card overflow-hidden',
      media.length === 1 ? 'grid-cols-1' : 'grid-cols-2')}>
      {media.map((m) => (
        <div key={m.id} className="relative bg-secondary/20 aspect-video">
          {m.mediaType === 'VIDEO' ? (
            <video src={m.url} poster={m.thumbnailUrl ?? undefined} controls className="h-full w-full object-cover" />
          ) : (
            <img src={m.url} alt="" loading="lazy" className="h-full w-full object-cover" />
          )}
        </div>
      ))}
    </div>
  );
}
