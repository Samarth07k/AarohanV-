import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { bookingService } from '../services/marketplaceService';
import { useUIStore } from '@/store/uiStore';

export function useMyBookings() {
  return useQuery({ queryKey: ['bookings', 'mine'], queryFn: () => bookingService.mine() });
}

export function useBooking(id: string | undefined) {
  return useQuery({
    queryKey: ['booking', id],
    queryFn: () => bookingService.get(id!),
    enabled: Boolean(id),
  });
}

export function useUpdateBookingStatus() {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: ({ id, status }: { id: string; status: string }) => bookingService.setStatus(id, status),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['bookings'] });
      qc.invalidateQueries({ queryKey: ['booking'] });
      toast('Booking updated', 'success');
    },
  });
}
