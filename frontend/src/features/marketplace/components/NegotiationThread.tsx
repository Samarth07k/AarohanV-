import { useState } from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { CheckCircle2, XCircle, ArrowRight } from 'lucide-react';
import type { Negotiation } from '@/types/marketplace';
import { GlassCard } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input, Textarea } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { StatusBadge } from './StatusBadge';
import { money, timeAgo } from './format';
import { useSendOffer, useAcceptOffer, useRejectNegotiation } from '../hooks/useNegotiation';
import { useAuthStore } from '@/store/authStore';

export function NegotiationThread({ negotiation, basePath }: { negotiation: Negotiation; basePath: string }) {
  const role = useAuthStore((s) => s.actor?.authorType);
  const [amount, setAmount] = useState<string>(negotiation.latestOffer ? String(negotiation.latestOffer.amount) : '');
  const [terms, setTerms] = useState('');

  const sendOffer = useSendOffer(negotiation.id);
  const accept = useAcceptOffer(negotiation.id);
  const reject = useRejectNegotiation(negotiation.id);

  const isOpen = negotiation.status === 'OPEN';
  const latest = negotiation.latestOffer;
  // You can accept only if the latest offer was made by the OTHER party
  const canAccept = isOpen && latest != null && latest.offeredBy !== role;

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="font-display text-2xl">{negotiation.opportunityTitle}</h2>
          <p className="text-sm text-ink/50">
            {role === 'VENUE' ? `with ${negotiation.artistName}` : `with ${negotiation.venueName}`}
          </p>
        </div>
        <StatusBadge status={negotiation.status} />
      </div>

      {/* Offer history */}
      <GlassCard className="p-5 mb-4">
        <h3 className="font-display text-lg mb-3">Offers</h3>
        {negotiation.offers.length === 0 ? (
          <p className="text-sm text-ink/50">No offers yet. {role === 'VENUE' ? 'Send the first offer below.' : 'Waiting for the venue to send an offer, or send yours below.'}</p>
        ) : (
          <div className="space-y-2">
            {negotiation.offers.map((o) => {
              const mine = o.offeredBy === role;
              return (
                <motion.div key={o.id} initial={{ opacity: 0, x: mine ? 16 : -16 }} animate={{ opacity: 1, x: 0 }}
                  className={`flex ${mine ? 'justify-end' : 'justify-start'}`}>
                  <div className={`max-w-[80%] rounded-card p-3 ${mine ? 'bg-primary text-bg' : 'bg-white/70 border border-border'}`}>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-xs font-semibold opacity-80">{o.offeredBy === 'VENUE' ? negotiation.venueName : negotiation.artistName}</span>
                      <span className="text-[10px] opacity-60">{timeAgo(o.createdAt)}</span>
                    </div>
                    <div className="font-display text-xl">{money(o.amount)}</div>
                    {o.terms && <p className={`text-sm mt-1 ${mine ? 'text-bg/90' : 'text-ink/70'}`}>{o.terms}</p>}
                  </div>
                </motion.div>
              );
            })}
          </div>
        )}
      </GlassCard>

      {/* Agreed → booking link */}
      {negotiation.status === 'AGREED' && negotiation.bookingId && (
        <GlassCard className="p-5 mb-4 border-primary/30">
          <div className="flex items-center gap-3">
            <CheckCircle2 className="h-6 w-6 text-primary" />
            <div className="flex-1">
              <div className="font-semibold">Agreement reached</div>
              <div className="text-sm text-ink/60">A booking has been confirmed{latest ? ` at ${money(latest.amount)}` : ''}.</div>
            </div>
            <Button asChild variant="accent" size="sm">
              <Link to={`${basePath}/bookings`}>View booking <ArrowRight className="h-4 w-4" /></Link>
            </Button>
          </div>
        </GlassCard>
      )}

      {negotiation.status === 'DECLINED' && (
        <GlassCard className="p-5 mb-4">
          <div className="flex items-center gap-3 text-ink/60">
            <XCircle className="h-5 w-5 text-destructive" /> This negotiation was declined.
          </div>
        </GlassCard>
      )}

      {/* Actions */}
      {isOpen && (
        <GlassCard className="p-5">
          <h3 className="font-display text-lg mb-3">Make a move</h3>
          <div className="space-y-3">
            <div className="grid sm:grid-cols-[160px_1fr] gap-3">
              <div className="space-y-1.5">
                <Label htmlFor="amount">Offer amount (€)</Label>
                <Input id="amount" type="number" min={0} value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="500" />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="terms">Terms (optional)</Label>
                <Input id="terms" value={terms} onChange={(e) => setTerms(e.target.value)} placeholder="2x45min sets, includes sound check" />
              </div>
            </div>
            <div className="flex flex-wrap gap-2">
              <Button
                disabled={sendOffer.isPending || !amount}
                onClick={() => sendOffer.mutate({ amount: Number(amount), terms }, { onSuccess: () => setTerms('') })}
              >
                {sendOffer.isPending ? 'Sending…' : (latest ? 'Send counter-offer' : 'Send offer')}
              </Button>
              {canAccept && (
                <Button variant="accent" disabled={accept.isPending} onClick={() => accept.mutate()}>
                  <CheckCircle2 className="h-4 w-4" /> Accept {money(latest!.amount)}
                </Button>
              )}
              <Button variant="ghost" disabled={reject.isPending} onClick={() => reject.mutate()}>Decline</Button>
            </div>
            {latest && latest.offeredBy === role && (
              <p className="text-xs text-ink/40">Waiting for the other party to respond to your offer.</p>
            )}
          </div>
        </GlassCard>
      )}
    </div>
  );
}
