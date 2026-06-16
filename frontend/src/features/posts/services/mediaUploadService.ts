import { api } from '@/api/client';
import { endpoints } from '@/api/endpoints';
import type { UploadUrlResponse, AttachMediaPayload } from '../types';
import type { MediaAttachment } from '@/types/mediaAttachment';

export const mediaUploadService = {
  async requestUploadUrl(mediaType: 'IMAGE' | 'VIDEO', fileName: string, contentType: string): Promise<UploadUrlResponse> {
    const { data } = await api.post<UploadUrlResponse>(endpoints.media.uploadUrl, {
      mediaType, fileName, contentType,
    });
    return data;
  },
  async attach(postId: string, payload: AttachMediaPayload): Promise<MediaAttachment> {
    const { data } = await api.post<MediaAttachment>(endpoints.media.attach(postId), payload);
    return data;
  },
  async remove(postId: string, mediaId: string): Promise<void> {
    await api.delete(endpoints.media.remove(postId, mediaId));
  },
};
