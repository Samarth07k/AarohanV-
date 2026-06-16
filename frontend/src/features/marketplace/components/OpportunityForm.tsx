import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { GlassCard } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input, Textarea } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useCreateOpportunity } from '../hooks/useOpportunities';

const schema = z.object({
  title: z.string().min(3, 'Title is too short').max(200),
  description: z.string().max(5000).optional(),
  eventDate: z.string().optional(),
  budgetMin: z.coerce.number().int().min(0).optional(),
  budgetMax: z.coerce.number().int().min(0).optional(),
}).refine((v) => v.budgetMin == null || v.budgetMax == null || v.budgetMin <= v.budgetMax, {
  message: 'Min budget cannot exceed max', path: ['budgetMax'],
});

type Values = z.infer<typeof schema>;

export function OpportunityForm({ onDone }: { onDone?: () => void }) {
  const { register, handleSubmit, reset, formState: { errors } } = useForm<Values>({ resolver: zodResolver(schema) });
  const create = useCreateOpportunity();

  return (
    <GlassCard className="p-5 mb-4">
      <h3 className="font-display text-lg mb-3">Post a new opportunity</h3>
      <form
        onSubmit={handleSubmit((v) => {
          create.mutate(
            {
              title: v.title,
              description: v.description ?? '',
              eventDate: v.eventDate ? new Date(v.eventDate).toISOString() : undefined,
              budgetMin: v.budgetMin,
              budgetMax: v.budgetMax,
            },
            { onSuccess: () => { reset(); onDone?.(); } }
          );
        })}
        className="space-y-3"
      >
        <div className="space-y-1.5">
          <Label htmlFor="title">Title</Label>
          <Input id="title" placeholder="Friday Night Live" {...register('title')} />
          {errors.title && <p className="text-xs text-destructive">{errors.title.message}</p>}
        </div>
        <div className="space-y-1.5">
          <Label htmlFor="description">Description</Label>
          <Textarea id="description" placeholder="Tell artists about the slot, the room, the vibe…" {...register('description')} />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1.5">
            <Label htmlFor="eventDate">Event date</Label>
            <Input id="eventDate" type="date" {...register('eventDate')} />
          </div>
          <div className="grid grid-cols-2 gap-2">
            <div className="space-y-1.5">
              <Label htmlFor="budgetMin">Budget min</Label>
              <Input id="budgetMin" type="number" min={0} placeholder="400" {...register('budgetMin')} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="budgetMax">Budget max</Label>
              <Input id="budgetMax" type="number" min={0} placeholder="600" {...register('budgetMax')} />
            </div>
          </div>
        </div>
        {errors.budgetMax && <p className="text-xs text-destructive">{errors.budgetMax.message}</p>}
        <div className="flex justify-end gap-2">
          {onDone && <Button type="button" variant="ghost" onClick={onDone}>Cancel</Button>}
          <Button type="submit" disabled={create.isPending}>{create.isPending ? 'Publishing…' : 'Publish opportunity'}</Button>
        </div>
      </form>
    </GlassCard>
  );
}
