import { Link } from 'react-router-dom';
import { Loader2, Bell, CheckCheck } from 'lucide-react';
import { PageHeader } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/store/authStore';
import { useNotifications, useMarkNotificationRead, useMarkAllNotificationsRead } from '@/features/marketplace/hooks/useNotifications';
import { InfiniteScrollList } from '@/shared/components/InfiniteScrollList';
import { timeAgo } from '@/features/marketplace/components/format';
import { cn } from '@/lib/utils';
import type { AppNotification } from '@/types/marketplace';

function linkFor(basePath: string, n: AppNotification): string {
  switch (n.relatedEntityType) {
    case 'NEGOTIATION': return `${basePath}/negotiations/${n.relatedEntityId}`;
    case 'BOOKING': return `${basePath}/bookings`;
    case 'MESSAGE': return `${basePath}/messaging`;
    case 'APPLICATION': return `${basePath}/applications`;
    case 'OPPORTUNITY': return `${basePath}/opportunities/${n.relatedEntityId}`;
    default: return `${basePath}/dashboard`;
  }
}

export function NotificationsPage() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const basePath = role === 'VENUE' ? '/venue' : '/artist';
  const { data, isLoading, hasNextPage, isFetchingNextPage, fetchNextPage } = useNotifications();
  const markRead = useMarkNotificationRead();
  const markAll = useMarkAllNotificationsRead();
  const items = data?.pages.flatMap((p) => p.items) ?? [];

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <PageHeader title="Notifications" />
        <Button variant="ghost" size="sm" onClick={() => markAll.mutate()}><CheckCheck className="h-4 w-4" /> Mark all read</Button>
      </div>
      {isLoading ? (
        <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>
      ) : (
        <InfiniteScrollList
          items={items}
          renderItem={(n) => (
            <Link key={n.id} to={linkFor(basePath, n)} onClick={() => !n.read && markRead.mutate(n.id)}>
              <GlassCard className={cn('p-4 mb-2 flex items-start gap-3', !n.read && 'border-primary/30')}>
                <div className={cn('mt-0.5 rounded-full p-2', n.read ? 'bg-ink/5 text-ink/40' : 'bg-primary/10 text-primary')}>
                  <Bell className="h-4 w-4" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="font-medium text-sm">{n.title}</span>
                    {!n.read && <span className="h-2 w-2 rounded-full bg-primary" />}
                  </div>
                  <p className="text-sm text-ink/60">{n.body}</p>
                  <span className="text-xs text-ink/40">{timeAgo(n.createdAt)}</span>
                </div>
              </GlassCard>
            </Link>
          )}
          hasNextPage={Boolean(hasNextPage)}
          isFetchingNextPage={isFetchingNextPage}
          fetchNextPage={fetchNextPage}
          emptyMessage="No notifications yet."
        />
      )}
    </div>
  );
}
