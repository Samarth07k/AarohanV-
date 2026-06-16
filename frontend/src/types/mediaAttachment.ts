export type MediaType = 'IMAGE' | 'VIDEO';

export interface MediaAttachment {
  id: string;
  postId: string;
  mediaType: MediaType;
  url: string;
  thumbnailUrl: string | null;
  width: number | null;
  height: number | null;
  durationSeconds: number | null;
  displayOrder: number;
  createdAt: string;
}
