import type { PostType } from '@/types/post';
import { PHASE_A_POST_TYPES } from '../types';
import { useAuthStore } from '@/store/authStore';
import { cn } from '@/lib/utils';

export function PostTypeSelector({ value, onChange }: { value: PostType; onChange: (t: PostType) => void }) {
  const role = useAuthStore((s) => s.actor?.authorType);
  const options = PHASE_A_POST_TYPES.filter((o) => !role || o.roles.includes(role));
  return (
    <div className="flex flex-wrap gap-2">
      {options.map((o) => (
        <button
          key={o.value}
          type="button"
          onClick={() => onChange(o.value)}
          className={cn('rounded-full px-3 py-1 text-xs font-medium transition-colors',
            value === o.value ? 'bg-primary text-bg' : 'bg-ink/5 text-ink/60 hover:bg-ink/10')}
        >
          {o.label}
        </button>
      ))}
    </div>
  );
}
