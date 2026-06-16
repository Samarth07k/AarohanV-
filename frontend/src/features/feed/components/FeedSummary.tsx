import { Link } from 'react-router-dom';
import { useHomeFeed } from '../hooks/useHomeFeed';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { ArrowRight, Loader2 } from 'lucide-react';

export function FeedSummary() {
  const { data, isLoading } = useHomeFeed();
  const items = (data?.pages.flatMap((p) => p.items) ?? []).slice(0, 3);

  if (isLoading) {
    return <div className="flex justify-center py-4"><Loader2 className="h-4 w-4 animate-spin text-primary/50" /></div>;
  }
  if (items.length === 0) {
    return <p className="text-sm text-ink/50">No recent posts yet. Visit the feed to start sharing.</p>;
  }
  return (
    <div className="space-y-3">
      {items.map((item) => (
        <div key={item.post.id} className="flex items-center gap-3">
          <Avatar className="h-8 w-8">
            {item.author.avatarUrl && <AvatarImage src={item.author.avatarUrl} />}
            <AvatarFallback className="text-xs">{(item.author.displayName || '?').slice(0, 2).toUpperCase()}</AvatarFallback>
          </Avatar>
          <div className="min-w-0 flex-1">
            <span className="text-sm font-medium">{item.author.displayName}</span>
            <p className="text-xs text-ink/50 truncate">{item.post.content || 'Shared media'}</p>
          </div>
        </div>
      ))}
      <Link to="/feed" className="inline-flex items-center gap-1 text-sm text-primary font-medium">
        View feed <ArrowRight className="h-4 w-4" />
      </Link>
    </div>
  );
}
