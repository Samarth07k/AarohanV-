import { useParams, useNavigate } from 'react-router-dom';
import { Loader2, MessageCircle } from 'lucide-react';
import { PageHeader } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { useAuthStore } from '@/store/authStore';
import { useConversations } from '@/features/marketplace/hooks/useMessaging';
import { MessageThread } from '@/features/marketplace/components/MessageThread';
import { cn } from '@/lib/utils';

export function MessagingPage() {
  const role = useAuthStore((s) => s.actor?.authorType);
  const basePath = role === 'VENUE' ? '/venue' : '/artist';
  const { conversationId } = useParams();
  const navigate = useNavigate();
  const { data: convos, isLoading } = useConversations();

  return (
    <div>
      <PageHeader title="Messaging" subtitle="Artist ↔ Venue conversations, unlocked by bookings" />
      <div className="grid md:grid-cols-[300px_1fr] gap-4">
        {/* Conversation list */}
        <div className="space-y-2">
          {isLoading ? (
            <GlassCard className="p-6 flex justify-center"><Loader2 className="h-5 w-5 animate-spin text-primary/60" /></GlassCard>
          ) : convos && convos.length > 0 ? (
            convos.map((c) => {
              const name = role === 'ARTIST' ? c.venueName : c.artistName;
              const active = c.id === conversationId;
              return (
                <button key={c.id} onClick={() => navigate(`${basePath}/messaging/${c.id}`)}
                  className={cn('w-full text-left rounded-card border p-3 transition-colors',
                    active ? 'border-primary bg-primary/5' : 'border-border bg-white/50 hover:bg-white/80')}>
                  <div className="flex items-center gap-2">
                    <Avatar className="h-8 w-8"><AvatarFallback className="text-xs">{name.slice(0, 2).toUpperCase()}</AvatarFallback></Avatar>
                    <div className="min-w-0 flex-1">
                      <div className="text-sm font-medium truncate">{name}</div>
                      <div className="text-xs text-ink/40 truncate">{c.lastMessage ?? 'No messages yet'}</div>
                    </div>
                  </div>
                </button>
              );
            })
          ) : (
            <GlassCard className="p-6 text-center text-sm text-ink/50">
              No conversations yet. They open automatically when a booking is confirmed.
            </GlassCard>
          )}
        </div>

        {/* Thread */}
        <div>
          {conversationId ? (
            <MessageThread conversationId={conversationId} />
          ) : (
            <GlassCard className="h-[70vh] flex flex-col items-center justify-center text-ink/40">
              <MessageCircle className="h-10 w-10 mb-2" />
              <p>Select a conversation to start messaging.</p>
            </GlassCard>
          )}
        </div>
      </div>
    </div>
  );
}
