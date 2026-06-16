import { api } from '@/api/client';
import { endpoints } from '@/api/endpoints';
import type { Post } from '@/types/post';
import type { PageResponse, AuthorType } from '@/types';
import type { CreatePostPayload } from '../types';

export const postService = {
  async create(payload: CreatePostPayload): Promise<Post> {
    const { data } = await api.post<Post>(endpoints.posts.create, payload);
    return data;
  },
  async get(id: string): Promise<Post> {
    const { data } = await api.get<Post>(endpoints.posts.detail(id));
    return data;
  },
  async update(id: string, payload: { content?: string; visibility?: string }): Promise<Post> {
    const { data } = await api.put<Post>(endpoints.posts.detail(id), payload);
    return data;
  },
  async remove(id: string): Promise<void> {
    await api.delete(endpoints.posts.detail(id));
  },
  async byAuthor(type: AuthorType, id: string, cursor?: string): Promise<PageResponse<Post>> {
    const { data } = await api.get<PageResponse<Post>>(endpoints.posts.byAuthor(type, id), {
      params: { cursor },
    });
    return data;
  },
};
