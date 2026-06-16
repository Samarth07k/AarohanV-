import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { applicationService } from '../services/marketplaceService';
import { useUIStore } from '@/store/uiStore';

export function useMyApplications() {
  return useQuery({ queryKey: ['applications', 'mine'], queryFn: () => applicationService.mine() });
}

export function useReceivedApplications() {
  return useQuery({ queryKey: ['applications', 'received'], queryFn: () => applicationService.received() });
}

export function useSubmitApplication() {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: applicationService.submit,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['applications'] });
      qc.invalidateQueries({ queryKey: ['opportunities'] });
      qc.invalidateQueries({ queryKey: ['opportunity'] });
      toast('Application submitted', 'success');
    },
    onError: (e: any) => {
      const msg = e?.response?.data?.message ?? 'Could not submit application';
      toast(msg, 'error');
    },
  });
}

export function useReviewApplication() {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: ({ id, status }: { id: string; status: string }) => applicationService.setStatus(id, status),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['applications'] });
      qc.invalidateQueries({ queryKey: ['opportunity-applications'] });
      toast('Application updated', 'success');
    },
  });
}

export function useWithdrawApplication() {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: (id: string) => applicationService.withdraw(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['applications'] });
      toast('Application withdrawn', 'success');
    },
  });
}
