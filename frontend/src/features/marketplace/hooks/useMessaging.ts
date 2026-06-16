import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { messagingService } from '../services/marketplaceService';

export function useConversations() {
  return useQuery({ queryKey: ['conversations'], queryFn: () => messagingService.conversations() });
}

export function useConversation(id: string | undefined) {
  return useQuery({
    queryKey: ['conversation', id],
    queryFn: () => messagingService.conversation(id!),
    enabled: Boolean(id),
  });
}

export function useMessages(id: string | undefined) {
  return useQuery({
    queryKey: ['messages', id],
    queryFn: () => messagingService.messages(id!),
    enabled: Boolean(id),
    refetchInterval: 5000, // light polling so the demo thread feels live
  });
}

export function useSendMessage(conversationId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (content: string) => messagingService.send(conversationId, content),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['messages', conversationId] });
      qc.invalidateQueries({ queryKey: ['conversations'] });
    },
  });
}

export function useMarkConversationRead(conversationId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: () => messagingService.markRead(conversationId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['messages', conversationId] }),
  });
}
