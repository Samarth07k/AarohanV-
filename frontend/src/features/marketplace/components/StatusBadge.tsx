import { cn } from '@/lib/utils';

const STYLES: Record<string, string> = {
  // opportunity
  OPEN: 'bg-primary/10 text-primary',
  CLOSED: 'bg-ink/10 text-ink/60',
  CANCELLED: 'bg-destructive/10 text-destructive',
  // application
  PENDING: 'bg-secondary/40 text-accent',
  REVIEWING: 'bg-accent/15 text-accent',
  ACCEPTED: 'bg-primary/15 text-primary',
  REJECTED: 'bg-destructive/10 text-destructive',
  WITHDRAWN: 'bg-ink/10 text-ink/50',
  // negotiation
  AGREED: 'bg-primary/15 text-primary',
  DECLINED: 'bg-destructive/10 text-destructive',
  // booking
  CONFIRMED: 'bg-primary/15 text-primary',
  COMPLETED: 'bg-accent/15 text-accent',
};

export function StatusBadge({ status }: { status: string }) {
  return (
    <span className={cn('inline-block rounded-full px-2.5 py-0.5 text-xs font-medium', STYLES[status] ?? 'bg-ink/10 text-ink/60')}>
      {status.charAt(0) + status.slice(1).toLowerCase()}
    </span>
  );
}
