import { useState } from 'react';
import { Plus } from 'lucide-react';
import { PageHeader } from '@/shared/components/PageScaffold';
import { Button } from '@/components/ui/button';
import { GlassCard } from '@/components/ui/card';
import { Loader2 } from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { useDiscoverOpportunities, useMyOpportunities } from '@/features/marketplace/hooks/useOpportunities';
import { OpportunityCard } from '@/features/marketplace/components/OpportunityCard';
import { OpportunityForm } from '@/features/marketplace/components/OpportunityForm';
import { InfiniteScrollList } from '@/shared/components/InfiniteScrollList';

function VenueOpportunities() {
  const [showForm, setShowForm] = useState(false);
  const { data, isLoading } = useMyOpportunities();
  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <PageHeader title="My Opportunities" subtitle="Create and manage your listings" />
        <Button onClick={() => setShowForm((s) => !s)}><Plus className="h-4 w-4" /> New</Button>
      </div>
      {showForm && <OpportunityForm onDone={() => setShowForm(false)} />}
      {isLoading ? (
        <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>
      ) : (data && data.length > 0 ? (
        data.map((o) => <OpportunityCard key={o.id} opp={o} to={`/venue/opportunities/${o.id}`} />)
      ) : (
        <GlassCard className="p-8 text-center text-ink/50">No opportunities yet. Post your first one.</GlassCard>
      ))}
    </div>
  );
}

function ArtistDiscovery() {
  const { data, isLoading, hasNextPage, isFetchingNextPage, fetchNextPage } = useDiscoverOpportunities();
  const items = data?.pages.flatMap((p) => p.items) ?? [];
  return (
    <div>
      <PageHeader title="Discover Opportunities" subtitle="Find your next gig" />
      {isLoading ? (
        <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>
      ) : (
        <InfiniteScrollList
          items={items}
          renderItem={(o) => <OpportunityCard key={o.id} opp={o} to={`/artist/opportunities/${o.id}`} />}
          hasNextPage={Boolean(hasNextPage)}
          isFetchingNextPage={isFetchingNextPage}
          fetchNextPage={fetchNextPage}
          emptyMessage="No open opportunities right now. Check back soon."
        />
      )}
    </div>
  );
}

export function OpportunitiesPage() {
  const role = useAuthStore((s) => s.actor?.authorType);
  return role === 'VENUE' ? <VenueOpportunities /> : <ArtistDiscovery />;
}
