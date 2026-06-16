import { api } from '@/api/client';
import { endpoints } from '@/api/endpoints';
import type { AuthorType } from '@/types';

export interface ProfileStats {
  followers: number;
  following: number;
  posts: number;
  opportunities: number;
  applications: number;
  negotiations: number;
  bookings: number;
}

export const profileService = {
  async stats(type: AuthorType, id: string): Promise<ProfileStats> {
    const { data } = await api.get<ProfileStats>(endpoints.profile.stats(type, id));
    return data;
  },
};
