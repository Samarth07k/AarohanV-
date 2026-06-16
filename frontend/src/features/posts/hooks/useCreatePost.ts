import { useMutation, useQueryClient } from '@tanstack/react-query';
import { postService } from '../services/postService';
import type { CreatePostPayload } from '../types';
import { useUIStore } from '@/store/uiStore';

export function useCreatePost() {
  const qc = useQueryClient();
  const pushToast = useUIStore((s) => s.pushToast);
  return useMutation({
    mutationFn: (payload: CreatePostPayload) => postService.create(payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['feed', 'home'] });
      qc.invalidateQueries({ queryKey: ['author-posts'] });
      pushToast('Post published', 'success');
    },
    onError: () => pushToast('Could not publish post', 'error'),
  });
}
