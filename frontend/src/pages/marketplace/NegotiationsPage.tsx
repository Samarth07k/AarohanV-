import { Loader2 } from 'lucide-react';
import { PageHeader } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';
import { useAuthStore } from '@/store/authStore';
import { useMyApplications, useReceivedApplications } from '@/features/marketplace/hooks/useApplications';
import { ApplicationCard } from '@/features/marketplace/components/ApplicationCard';

/** Lists applications that have an active negotiation thread. */
export function NegotiationsPage() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const isVenue = role === 'VENUE';
  const mine = useMyApplications();
  const received = useReceivedApplications();
  const query = isVenue ? received : mine;
  const basePath = isVenue ? '/venue' : '/artist';
  const negotiating = (query.data ?? []).filter((a) => a.negotiationId);

  return (
    <div>
      <PageHeader title="Negotiations" subtitle="Active offer threads" />
      {query.isLoading ? (
        <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>
      ) : negotiating.length > 0 ? (
        negotiating.map((a) => <ApplicationCard key={a.id} app={a} basePath={basePath} />)
      ) : (
        <GlassCard className="p-8 text-center text-ink/50">No active negotiations.</GlassCard>
      )}
    </div>
  );
}
