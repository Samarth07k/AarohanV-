import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Calendar, MapPin, MessageCircle } from 'lucide-react';
import type { Booking } from '@/types/marketplace';
import { GlassCard } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { StatusBadge } from './StatusBadge';
import { money, eventDate } from './format';
import { useUpdateBookingStatus } from '../hooks/useBookings';
import { useAuthStore } from '@/store/authStore';

export function BookingCard({ booking, basePath }: { booking: Booking; basePath: string }) {
  const role = useAuthStore((s) => s.actor?.authorType);
  const update = useUpdateBookingStatus();
  const navigate = useNavigate();
  const counterparty = role === 'ARTIST' ? booking.venueName : booking.artistName;

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
      <GlassCard className="p-5 mb-3">
        <div className="flex items-start justify-between gap-3 mb-2">
          <div>
            <h3 className="font-display text-lg">{counterparty}</h3>
            <div className="text-sm font-semibold text-primary">{money(booking.agreedAmount)}</div>
          </div>
          <StatusBadge status={booking.status} />
        </div>
        <div className="flex flex-wrap gap-x-4 gap-y-1 text-xs text-ink/50 mb-3">
          <span className="inline-flex items-center gap-1"><Calendar className="h-3.5 w-3.5" /> {eventDate(booking.eventDate)}</span>
          {booking.venueLocation && <span className="inline-flex items-center gap-1"><MapPin className="h-3.5 w-3.5" /> {booking.venueLocation}</span>}
        </div>
        <div className="flex flex-wrap items-center gap-2">
          {booking.conversationId && (
            <Button size="sm" variant="accent" onClick={() => navigate(`${basePath}/messaging/${booking.conversationId}`)}>
              <MessageCircle className="h-4 w-4" /> Message {role === 'ARTIST' ? 'venue' : 'artist'}
            </Button>
          )}
          {booking.status === 'CONFIRMED' && (
            <>
              <Button size="sm" variant="outline" onClick={() => update.mutate({ id: booking.id, status: 'COMPLETED' })}>Mark completed</Button>
              <Button size="sm" variant="ghost" onClick={() => update.mutate({ id: booking.id, status: 'CANCELLED' })}>Cancel</Button>
            </>
          )}
        </div>
      </GlassCard>
    </motion.div>
  );
}
