import { Loader2 } from 'lucide-react';
import { PageHeader } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';
import { useAuthStore } from '@/store/authStore';
import { useMyBookings } from '@/features/marketplace/hooks/useBookings';
import { BookingCard } from '@/features/marketplace/components/BookingCard';

export function BookingsPage() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const basePath = role === 'VENUE' ? '/venue' : '/artist';
  const { data, isLoading } = useMyBookings();

  const upcoming = (data ?? []).filter((b) => b.status === 'CONFIRMED');
  const past = (data ?? []).filter((b) => b.status !== 'CONFIRMED');

  return (
    <div>
      <PageHeader title="Bookings" subtitle="Your confirmed gigs" />
      {isLoading ? (
        <GlassCard className="p-8 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>
      ) : (data && data.length > 0 ? (
        <>
          {upcoming.length > 0 && (
            <div className="mb-6">
              <h2 className="font-display text-lg mb-3">Upcoming &amp; confirmed</h2>
              {upcoming.map((b) => <BookingCard key={b.id} booking={b} basePath={basePath} />)}
            </div>
          )}
          {past.length > 0 && (
            <div>
              <h2 className="font-display text-lg mb-3">Completed &amp; cancelled</h2>
              {past.map((b) => <BookingCard key={b.id} booking={b} basePath={basePath} />)}
            </div>
          )}
        </>
      ) : (
        <GlassCard className="p-8 text-center text-ink/50">No bookings yet.</GlassCard>
      ))}
    </div>
  );
}
