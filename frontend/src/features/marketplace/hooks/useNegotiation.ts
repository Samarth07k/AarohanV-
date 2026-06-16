import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { negotiationService } from '../services/marketplaceService';
import { useUIStore } from '@/store/uiStore';

export function useNegotiation(id: string | undefined) {
  return useQuery({
    queryKey: ['negotiation', id],
    queryFn: () => negotiationService.get(id!),
    enabled: Boolean(id),
  });
}

export function useSendOffer(id: string) {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: (payload: { amount: number; terms: string }) => negotiationService.sendOffer(id, payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['negotiation', id] });
      toast('Offer sent', 'success');
    },
    onError: (e: any) => toast(e?.response?.data?.message ?? 'Could not send offer', 'error'),
  });
}

export function useAcceptOffer(id: string) {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: () => negotiationService.accept(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['negotiation', id] });
      qc.invalidateQueries({ queryKey: ['bookings'] });
      qc.invalidateQueries({ queryKey: ['applications'] });
      toast('Offer accepted — booking confirmed', 'success');
    },
    onError: (e: any) => toast(e?.response?.data?.message ?? 'Could not accept offer', 'error'),
  });
}

export function useRejectNegotiation(id: string) {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: () => negotiationService.reject(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['negotiation', id] });
      qc.invalidateQueries({ queryKey: ['applications'] });
      toast('Negotiation declined', 'success');
    },
  });
}
