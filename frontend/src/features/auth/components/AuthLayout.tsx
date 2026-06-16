import { type ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Coffee } from 'lucide-react';
import { GlassCard } from '@/components/ui/card';

export function AuthLayout({ title, subtitle, children, footer }: {
  title: string;
  subtitle?: string;
  children: ReactNode;
  footer?: ReactNode;
}) {
  return (
    <div className="al-botanical-bg relative min-h-screen flex items-center justify-center px-4 py-12">
      <Link to="/" className="absolute top-6 left-6 flex items-center gap-2 font-display text-lg text-primary">
        <Coffee className="h-5 w-5" /> Aarohan
      </Link>
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-md"
      >
        <GlassCard className="p-8">
          <h1 className="font-display text-3xl mb-1">{title}</h1>
          {subtitle && <p className="text-ink/60 mb-6">{subtitle}</p>}
          {children}
          {footer && <div className="mt-6 text-center text-sm text-ink/60">{footer}</div>}
        </GlassCard>
      </motion.div>
    </div>
  );
}
