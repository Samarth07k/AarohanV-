import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Calendar, MapPin, Users, Loader2 } from 'lucide-react';
import { PageHeader } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/store/authStore';
import { useOpportunity, useOpportunityApplications, useCloseOpportunity } from '@/features/marketplace/hooks/useOpportunities';
import { StatusBadge } from '@/features/marketplace/components/StatusBadge';
import { budgetRange, eventDate } from '@/features/marketplace/components/format';
import { ApplyForm } from '@/features/marketplace/components/ApplyForm';
import { ApplicationCard } from '@/features/marketplace/components/ApplicationCard';

export function OpportunityDetailPage() {
  const { id } = useParams();
  const role = useAuthStore((s) => s.actor?.authorType);
  const { data: opp, isLoading } = useOpportunity(id);
  const [showApply, setShowApply] = useState(false);
  const close = useCloseOpportunity();
  const isVenue = role === 'VENUE';
  const { data: applications } = useOpportunityApplications(isVenue ? id : undefined);

  if (isLoading || !opp) {
    return <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>;
  }

  const basePath = isVenue ? '/venue' : '/artist';

  return (
    <div>
      <PageHeader title={opp.title} />
      <GlassCard className="p-6 mb-4">
        <div className="flex items-center justify-between mb-3">
          <StatusBadge status={opp.status} />
          <span className="text-lg font-semibold text-primary">{budgetRange(opp.budgetMin, opp.budgetMax)}</span>
        </div>
        <p className="text-ink/80 whitespace-pre-wrap mb-4">{opp.description || 'No description provided.'}</p>
        <div className="flex flex-wrap gap-x-5 gap-y-2 text-sm text-ink/50">
          <span className="inline-flex items-center gap-1.5"><MapPin className="h-4 w-4" /> {opp.venueName}{opp.venueLocation ? ` · ${opp.venueLocation}` : ''}</span>
          <span className="inline-flex items-center gap-1.5"><Calendar className="h-4 w-4" /> {eventDate(opp.eventDate)}</span>
          <span className="inline-flex items-center gap-1.5"><Users className="h-4 w-4" /> {opp.applicationCount} applied</span>
        </div>

        {/* Artist actions */}
        {!isVenue && opp.status === 'OPEN' && (
          <div className="mt-5">
            {opp.hasApplied ? (
              <p className="text-sm text-accent font-medium">You've already applied to this opportunity.</p>
            ) : showApply ? null : (
              <Button onClick={() => setShowApply(true)}>Apply now</Button>
            )}
          </div>
        )}

        {/* Venue actions */}
        {isVenue && opp.status === 'OPEN' && (
          <div className="mt-5">
            <Button variant="ghost" onClick={() => close.mutate(opp.id)}>Close opportunity</Button>
          </div>
        )}
      </GlassCard>

      {!isVenue && showApply && !opp.hasApplied && (
        <div className="mb-4"><ApplyForm opportunityId={opp.id} onDone={() => setShowApply(false)} /></div>
      )}

      {/* Venue: applications received */}
      {isVenue && (
        <div>
          <h2 className="font-display text-xl mb-3">Applications ({applications?.length ?? 0})</h2>
          {applications && applications.length > 0 ? (
            applications.map((a) => <ApplicationCard key={a.id} app={a} basePath={basePath} />)
          ) : (
            <GlassCard className="p-6 text-center text-ink/50">No applications yet.</GlassCard>
          )}
        </div>
      )}
    </div>
  );
}
