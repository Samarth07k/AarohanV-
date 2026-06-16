import { useInfiniteQuery, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { opportunityService } from '../services/marketplaceService';
import { useUIStore } from '@/store/uiStore';

export function useDiscoverOpportunities() {
  return useInfiniteQuery({
    queryKey: ['opportunities', 'discover'],
    queryFn: ({ pageParam }) => opportunityService.discover(pageParam as string | undefined),
    initialPageParam: undefined as string | undefined,
    getNextPageParam: (last) => last.nextCursor ?? undefined,
  });
}

export function useMyOpportunities() {
  return useQuery({ queryKey: ['opportunities', 'mine'], queryFn: () => opportunityService.mine() });
}

export function useOpportunity(id: string | undefined) {
  return useQuery({
    queryKey: ['opportunity', id],
    queryFn: () => opportunityService.get(id!),
    enabled: Boolean(id),
  });
}

export function useOpportunityApplications(id: string | undefined) {
  return useQuery({
    queryKey: ['opportunity-applications', id],
    queryFn: () => opportunityService.applications(id!),
    enabled: Boolean(id),
  });
}

export function useCreateOpportunity() {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: opportunityService.create,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['opportunities'] });
      toast('Opportunity published', 'success');
    },
    onError: () => toast('Could not create opportunity', 'error'),
  });
}

export function useCloseOpportunity() {
  const qc = useQueryClient();
  const toast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: (id: string) => opportunityService.close(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['opportunities'] });
      toast('Opportunity closed', 'success');
    },
  });
}
