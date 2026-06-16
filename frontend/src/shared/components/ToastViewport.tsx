import { AnimatePresence, motion } from 'framer-motion';
import { useUIStore } from '@/store/uiStore';
import { cn } from '@/lib/utils';
import { X } from 'lucide-react';

export function ToastViewport() {
  const { toasts, dismissToast } = useUIStore();
  return (
    <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
      <AnimatePresence>
        {toasts.map((t) => (
          <motion.div
            key={t.id}
            initial={{ opacity: 0, y: 12, scale: 0.96 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, scale: 0.96 }}
            className={cn(
              'al-glass-card flex items-center gap-3 px-4 py-3 text-sm min-w-[240px]',
              t.variant === 'error' && 'border-destructive/40',
              t.variant === 'success' && 'border-primary/40'
            )}
          >
            <span className="flex-1">{t.message}</span>
            <button onClick={() => dismissToast(t.id)} className="text-ink/40 hover:text-ink">
              <X className="h-4 w-4" />
            </button>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  );
}
