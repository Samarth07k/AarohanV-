import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import type { Application } from '@/types/marketplace';
import { GlassCard } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { StatusBadge } from './StatusBadge';
import { timeAgo } from './format';
import { useReviewApplication, useWithdrawApplication } from '../hooks/useApplications';
import { useAuthStore } from '@/store/authStore';

export function ApplicationCard({ app, basePath }: { app: Application; basePath: string }) {
  const role = useAuthStore((s) => s.actor?.authorType);
  const review = useReviewApplication();
  const withdraw = useWithdrawApplication();
  const navigate = useNavigate();
  const initials = (role === 'VENUE' ? app.artistName : app.venueName).slice(0, 2).toUpperCase();

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
      <GlassCard className="p-5 mb-3">
        <div className="flex items-center gap-3 mb-3">
          <Avatar className="h-10 w-10">
            {app.artistAvatarUrl && <AvatarImage src={app.artistAvatarUrl} />}
            <AvatarFallback>{initials}</AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0">
            <div className="font-semibold text-sm truncate">
              {role === 'VENUE' ? app.artistName : app.opportunityTitle}
            </div>
            <div className="text-xs text-ink/40">
              {role === 'VENUE' ? `for "${app.opportunityTitle}"` : app.venueName} · {timeAgo(app.createdAt)}
            </div>
          </div>
          <StatusBadge status={app.status} />
        </div>

        {app.coverMessage && <p className="text-sm text-ink/70 mb-3 whitespace-pre-wrap">{app.coverMessage}</p>}

        <div className="flex flex-wrap items-center gap-2">
          {/* Venue actions */}
          {role === 'VENUE' && app.status === 'PENDING' && (
            <>
              <Button size="sm" variant="outline" onClick={() => review.mutate({ id: app.id, status: 'REVIEWING' })}>Mark reviewing</Button>
              <Button size="sm" onClick={() => review.mutate({ id: app.id, status: 'ACCEPTED' })}>Accept → negotiate</Button>
              <Button size="sm" variant="ghost" onClick={() => review.mutate({ id: app.id, status: 'REJECTED' })}>Reject</Button>
            </>
          )}
          {role === 'VENUE' && app.status === 'REVIEWING' && (
            <>
              <Button size="sm" onClick={() => review.mutate({ id: app.id, status: 'ACCEPTED' })}>Accept → negotiate</Button>
              <Button size="sm" variant="ghost" onClick={() => review.mutate({ id: app.id, status: 'REJECTED' })}>Reject</Button>
            </>
          )}
          {/* Both: open negotiation once it exists */}
          {app.negotiationId && (
            <Button size="sm" variant="accent" onClick={() => navigate(`${basePath}/negotiations/${app.negotiationId}`)}>
              Open negotiation
            </Button>
          )}
          {/* Artist withdraw */}
          {role === 'ARTIST' && (app.status === 'PENDING' || app.status === 'REVIEWING') && (
            <Button size="sm" variant="ghost" onClick={() => withdraw.mutate(app.id)}>Withdraw</Button>
          )}
        </div>
      </GlassCard>
    </motion.div>
  );
}
