import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { useNotifications } from '../hooks/useNotifications';
import { timeAgo } from './format';

export function NotificationsWidget() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const basePath = role === 'VENUE' ? '/venue' : '/artist';
  const { data } = useNotifications();
  const recent = (data?.pages.flatMap((p) => p.items) ?? []).slice(0, 4);

  return recent.length === 0 ? (
    <p className="text-sm text-ink/50">No notifications yet.</p>
  ) : (
    <div className="space-y-2">
      {recent.map((n) => (
        <div key={n.id} className="flex items-start gap-2">
          {!n.read && <span className="mt-1.5 h-2 w-2 rounded-full bg-primary shrink-0" />}
          <div className={!n.read ? '' : 'opacity-60'}>
            <div className="text-sm font-medium">{n.title}</div>
            <div className="text-xs text-ink/50">{timeAgo(n.createdAt)}</div>
          </div>
        </div>
      ))}
      <Link to={`${basePath}/notifications`} className="inline-flex items-center gap-1 text-sm text-primary font-medium">
        View all <ArrowRight className="h-4 w-4" />
      </Link>
    </div>
  );
}
