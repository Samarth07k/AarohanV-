import { Loader2 } from 'lucide-react';
import { PageHeader } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';
import { useAuthStore } from '@/store/authStore';
import { useMyApplications, useReceivedApplications } from '@/features/marketplace/hooks/useApplications';
import { ApplicationCard } from '@/features/marketplace/components/ApplicationCard';

export function ApplicationsPage() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const isVenue = role === 'VENUE';
  const mine = useMyApplications();
  const received = useReceivedApplications();
  const query = isVenue ? received : mine;
  const basePath = isVenue ? '/venue' : '/artist';

  return (
    <div>
      <PageHeader
        title={isVenue ? 'Applications Received' : 'My Applications'}
        subtitle={isVenue ? 'Review and respond to artists' : 'Track your applications'}
      />
      {query.isLoading ? (
        <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>
      ) : (query.data && query.data.length > 0 ? (
        query.data.map((a) => <ApplicationCard key={a.id} app={a} basePath={basePath} />)
      ) : (
        <GlassCard className="p-8 text-center text-ink/50">
          {isVenue ? 'No applications received yet.' : "You haven't applied to anything yet."}
        </GlassCard>
      ))}
    </div>
  );
}
