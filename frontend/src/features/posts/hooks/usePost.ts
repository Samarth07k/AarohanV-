import { useQuery } from '@tanstack/react-query';
import { postService } from '../services/postService';

export function usePost(id: string | undefined) {
  return useQuery({
    queryKey: ['post', id],
    queryFn: () => postService.get(id!),
    enabled: Boolean(id),
  });
}
