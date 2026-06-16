import { type ReactNode } from 'react';
import { motion } from 'framer-motion';
import { GlassCard } from '@/components/ui/card';

export function PageHeader({ title, subtitle }: { title: string; subtitle?: string }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="mb-6"
    >
      <h1 className="font-display text-3xl">{title}</h1>
      {subtitle && <p className="text-ink/60 mt-1">{subtitle}</p>}
    </motion.div>
  );
}

export function Widget({ title, badge, children }: { title: string; badge?: string; children: ReactNode }) {
  return (
    <GlassCard className="p-5">
      <div className="flex items-center justify-between mb-3">
        <h2 className="font-display text-lg">{title}</h2>
        {badge && <span className="text-xs rounded-full bg-primary/10 px-2.5 py-0.5 text-primary font-medium">{badge}</span>}
      </div>
      {children}
    </GlassCard>
  );
}

export function ComingSoon({ phase }: { phase: string }) {
  return (
    <p className="text-sm text-ink/50 py-2">
      Wired in <span className="font-medium text-primary">{phase}</span>. The data layer,
      API contract, and component slot are already defined per the blueprint.
    </p>
  );
}
