import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { MapPin, Calendar, Users } from 'lucide-react';
import type { Opportunity } from '@/types/marketplace';
import { GlassCard } from '@/components/ui/card';
import { StatusBadge } from './StatusBadge';
import { budgetRange, eventDate } from './format';

export function OpportunityCard({ opp, to }: { opp: Opportunity; to: string }) {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
      <Link to={to}>
        <GlassCard className="p-5 mb-3 hover:-translate-y-0.5 transition-transform">
          <div className="flex items-start justify-between gap-3 mb-2">
            <h3 className="font-display text-lg leading-tight">{opp.title}</h3>
            <StatusBadge status={opp.status} />
          </div>
          <p className="text-sm text-ink/60 line-clamp-2 mb-3">{opp.description || 'No description provided.'}</p>
          <div className="flex flex-wrap gap-x-4 gap-y-1 text-xs text-ink/50">
            <span className="inline-flex items-center gap-1"><MapPin className="h-3.5 w-3.5" /> {opp.venueName}{opp.venueLocation ? ` · ${opp.venueLocation}` : ''}</span>
            <span className="inline-flex items-center gap-1"><Calendar className="h-3.5 w-3.5" /> {eventDate(opp.eventDate)}</span>
            <span className="inline-flex items-center gap-1"><Users className="h-3.5 w-3.5" /> {opp.applicationCount} applied</span>
          </div>
          <div className="mt-3 flex items-center justify-between">
            <span className="text-sm font-semibold text-primary">{budgetRange(opp.budgetMin, opp.budgetMax)}</span>
            {opp.hasApplied && <span className="text-xs text-accent font-medium">You applied</span>}
          </div>
        </GlassCard>
      </Link>
    </motion.div>
  );
}
