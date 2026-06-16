import { PageHeader, ComingSoon } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';

export function PlaceholderPage({ title, subtitle, phase }: { title: string; subtitle?: string; phase: string }) {
  return (
    <div>
      <PageHeader title={title} subtitle={subtitle} />
      <GlassCard className="p-6"><ComingSoon phase={phase} /></GlassCard>
    </div>
  );
}
