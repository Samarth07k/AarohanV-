import { useEffect, useRef, useState } from 'react';
import { Send } from 'lucide-react';
import { GlassCard } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { useConversation, useMessages, useSendMessage, useMarkConversationRead } from '../hooks/useMessaging';
import { useAuthStore } from '@/store/authStore';

export function MessageThread({ conversationId }: { conversationId: string }) {
  const role = useAuthStore((s) => s.actor?.authorType);
  const { data: convo } = useConversation(conversationId);
  const { data: messages } = useMessages(conversationId);
  const send = useSendMessage(conversationId);
  const markRead = useMarkConversationRead(conversationId);
  const [text, setText] = useState('');
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth' }); }, [messages?.length]);
  useEffect(() => { markRead.mutate(); /* eslint-disable-next-line */ }, [conversationId]);

  const counterparty = convo ? (role === 'ARTIST' ? convo.venueName : convo.artistName) : '';
  const initials = counterparty.slice(0, 2).toUpperCase();

  return (
    <GlassCard className="flex flex-col h-[70vh]">
      <div className="flex items-center gap-3 border-b border-border p-4">
        <Avatar className="h-9 w-9"><AvatarFallback>{initials}</AvatarFallback></Avatar>
        <div className="font-semibold">{counterparty || 'Conversation'}</div>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-2">
        {(messages ?? []).map((m) => {
          const mine = m.senderType === role;
          return (
            <div key={m.id} className={`flex ${mine ? 'justify-end' : 'justify-start'}`}>
              <div className={`max-w-[75%] rounded-card px-3 py-2 text-sm ${mine ? 'bg-primary text-bg' : 'bg-white/70 border border-border text-ink'}`}>
                {m.content}
              </div>
            </div>
          );
        })}
        {messages && messages.length === 0 && (
          <p className="text-center text-sm text-ink/40 py-8">Say hello to start the conversation.</p>
        )}
        <div ref={bottomRef} />
      </div>

      <div className="border-t border-border p-3 flex gap-2">
        <Input
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter' && text.trim()) { send.mutate(text.trim()); setText(''); } }}
          placeholder="Type a message…"
        />
        <Button size="icon" disabled={!text.trim() || send.isPending}
          onClick={() => { send.mutate(text.trim()); setText(''); }}>
          <Send className="h-4 w-4" />
        </Button>
      </div>
    </GlassCard>
  );
}
