import { api } from '@/api/client';
import { endpoints } from '@/api/endpoints';

export const engagementService = {
  async like(postId: string): Promise<void> {
    await api.post(endpoints.likes.like(postId));
  },
  async unlike(postId: string): Promise<void> {
    await api.delete(endpoints.likes.like(postId));
  },
};
