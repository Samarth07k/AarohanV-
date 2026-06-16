import { api } from '@/api/client';
import { endpoints } from '@/api/endpoints';
import type { FeedItem } from '@/types/post';
import type { PageResponse } from '@/types';

export const feedService = {
  async home(cursor?: string): Promise<PageResponse<FeedItem>> {
    const { data } = await api.get<PageResponse<FeedItem>>(endpoints.feed.home, {
      params: { cursor },
    });
    return data;
  },
};
