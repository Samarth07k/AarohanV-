import { useState } from 'react';
import { GlassCard } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useSubmitApplication } from '../hooks/useApplications';

export function ApplyForm({ opportunityId, onDone }: { opportunityId: string; onDone?: () => void }) {
  const [coverMessage, setCoverMessage] = useState('');
  const submit = useSubmitApplication();
  return (
    <GlassCard className="p-5">
      <h3 className="font-display text-lg mb-3">Apply to this opportunity</h3>
      <div className="space-y-3">
        <div className="space-y-1.5">
          <Label htmlFor="cover">Cover message</Label>
          <Textarea id="cover" value={coverMessage} onChange={(e) => setCoverMessage(e.target.value)}
            placeholder="Introduce yourself and why you're a great fit…" />
        </div>
        <div className="flex justify-end gap-2">
          {onDone && <Button variant="ghost" onClick={onDone}>Cancel</Button>}
          <Button
            disabled={submit.isPending}
            onClick={() => submit.mutate({ opportunityId, coverMessage }, { onSuccess: () => onDone?.() })}
          >
            {submit.isPending ? 'Submitting…' : 'Submit application'}
          </Button>
        </div>
      </div>
    </GlassCard>
  );
}
