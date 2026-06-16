import { useParams } from 'react-router-dom';
import { Loader2 } from 'lucide-react';
import { GlassCard } from '@/components/ui/card';
import { useAuthStore } from '@/store/authStore';
import { useNegotiation } from '@/features/marketplace/hooks/useNegotiation';
import { NegotiationThread } from '@/features/marketplace/components/NegotiationThread';

export function NegotiationDetailPage() {
  const { id } = useParams();
  const role = useAuthStore((s) => s.actor?.authorType);
  const { data: negotiation, isLoading } = useNegotiation(id);
  const basePath = role === 'VENUE' ? '/venue' : '/artist';

  if (isLoading || !negotiation) {
    return <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>;
  }
  return <NegotiationThread negotiation={negotiation} basePath={basePath} />;
}
