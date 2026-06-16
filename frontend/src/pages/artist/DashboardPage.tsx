import { PageHeader, Widget, ComingSoon } from '@/shared/components/PageScaffold';
import { useAuthStore } from '@/store/authStore';
import { FeedSummary } from '@/features/feed/components/FeedSummary';
import { ApplicationsWidget, NegotiationsWidget, BookingsWidget } from '@/features/marketplace/components/DashboardWidgets';
import { NotificationsWidget } from '@/features/marketplace/components/NotificationsWidget';

export function ArtistDashboardPage() {
  const actor = useAuthStore((s) => s.actor);
  return (
    <div>
      <PageHeader title={`Welcome, ${actor?.displayName || 'Artist'}`} subtitle="Your Aarohan dashboard" />
      <div className="grid gap-4">
        <Widget title="Feed Summary"><FeedSummary /></Widget>
        <div className="grid sm:grid-cols-2 gap-4">
          <Widget title="Applications"><ApplicationsWidget /></Widget>
          <Widget title="Negotiations"><NegotiationsWidget /></Widget>
        </div>
        <Widget title="Bookings"><BookingsWidget /></Widget>
        <Widget title="Notifications"><NotificationsWidget /></Widget>
        <Widget title="Content Performance" badge="NEW"><ComingSoon phase="Phase D" /></Widget>
      </div>
    </div>
  );
}
