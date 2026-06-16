export function money(n: number | null | undefined): string {
  if (n == null) return '—';
  return '€' + n.toLocaleString();
}

export function budgetRange(min: number | null, max: number | null): string {
  if (min == null && max == null) return 'Budget TBD';
  if (min != null && max != null) return `€${min.toLocaleString()}–${max.toLocaleString()}`;
  if (min != null) return `From €${min.toLocaleString()}`;
  return `Up to €${max!.toLocaleString()}`;
}

export function eventDate(iso: string | null): string {
  if (!iso) return 'Date TBD';
  return new Date(iso).toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' });
}

export function timeAgo(iso: string): string {
  const diff = Date.now() - new Date(iso).getTime();
  const m = Math.floor(diff / 60000);
  if (m < 1) return 'just now';
  if (m < 60) return `${m}m ago`;
  const h = Math.floor(m / 60);
  if (h < 24) return `${h}h ago`;
  return `${Math.floor(h / 24)}d ago`;
}
