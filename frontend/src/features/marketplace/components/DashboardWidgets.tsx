import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { useMyApplications, useReceivedApplications } from '../hooks/useApplications';
import { useMyBookings } from '../hooks/useBookings';
import { useMyOpportunities } from '../hooks/useOpportunities';
import { StatusBadge } from './StatusBadge';
import { money } from './format';

export function ApplicationsWidget() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const isVenue = role === 'VENUE';
  const basePath = isVenue ? '/venue' : '/artist';
  const mine = useMyApplications();
  const received = useReceivedApplications();
  const data = (isVenue ? received.data : mine.data) ?? [];
  const recent = data.slice(0, 4);

  return (
    <div>
      {recent.length === 0 ? (
        <p className="text-sm text-ink/50">{isVenue ? 'No applications received yet.' : 'No applications yet.'}</p>
      ) : (
        <div className="space-y-2">
          {recent.map((a) => (
            <div key={a.id} className="flex items-center justify-between gap-2">
              <span className="text-sm truncate">{isVenue ? a.artistName : a.opportunityTitle}</span>
              <StatusBadge status={a.status} />
            </div>
          ))}
          <Link to={`${basePath}/applications`} className="inline-flex items-center gap-1 text-sm text-primary font-medium">
            View all <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      )}
    </div>
  );
}

export function NegotiationsWidget() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const isVenue = role === 'VENUE';
  const basePath = isVenue ? '/venue' : '/artist';
  const mine = useMyApplications();
  const received = useReceivedApplications();
  const data = (isVenue ? received.data : mine.data) ?? [];
  const negotiating = data.filter((a) => a.negotiationId).slice(0, 4);

  return negotiating.length === 0 ? (
    <p className="text-sm text-ink/50">No active negotiations.</p>
  ) : (
    <div className="space-y-2">
      {negotiating.map((a) => (
        <Link key={a.id} to={`${basePath}/negotiations/${a.negotiationId}`}
          className="flex items-center justify-between gap-2 hover:text-primary">
          <span className="text-sm truncate">{isVenue ? a.artistName : a.opportunityTitle}</span>
          <StatusBadge status={a.status} />
        </Link>
      ))}
    </div>
  );
}

export function BookingsWidget() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const basePath = role === 'VENUE' ? '/venue' : '/artist';
  const { data } = useMyBookings();
  const recent = (data ?? []).slice(0, 4);

  return recent.length === 0 ? (
    <p className="text-sm text-ink/50">No bookings yet.</p>
  ) : (
    <div className="space-y-2">
      {recent.map((b) => (
        <div key={b.id} className="flex items-center justify-between gap-2">
          <span className="text-sm truncate">{role === 'ARTIST' ? b.venueName : b.artistName}</span>
          <span className="text-sm font-semibold text-primary">{money(b.agreedAmount)}</span>
        </div>
      ))}
      <Link to={`${basePath}/bookings`} className="inline-flex items-center gap-1 text-sm text-primary font-medium">
        View all <ArrowRight className="h-4 w-4" />
      </Link>
    </div>
  );
}

export function OpportunitiesWidget() {
  const { data } = useMyOpportunities();
  const open = (data ?? []).filter((o) => o.status === 'OPEN');
  return (
    <div>
      <div className="font-display text-2xl text-primary">{open.length}</div>
      <div className="text-sm text-ink/50">open {open.length === 1 ? 'opportunity' : 'opportunities'}</div>
      <Link to="/venue/opportunities" className="inline-flex items-center gap-1 text-sm text-primary font-medium mt-2">
        Manage <ArrowRight className="h-4 w-4" />
      </Link>
    </div>
  );
}
