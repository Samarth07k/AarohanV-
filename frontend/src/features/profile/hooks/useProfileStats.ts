import { useQuery } from '@tanstack/react-query';
import { profileService } from '../services/profileService';
import type { AuthorType } from '@/types';

export function useProfileStats(type: AuthorType | undefined, id: string | undefined) {
  return useQuery({
    queryKey: ['profile-stats', type, id],
    queryFn: () => profileService.stats(type!, id!),
    enabled: Boolean(type && id),
  });
}
