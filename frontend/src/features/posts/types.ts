import type { PostType, PostVisibility, LinkedEntityType } from '@/types/post';

export interface CreatePostPayload {
  postType: PostType;
  content: string;
  mediaIds?: string[];
  linkedEntityType?: Exclude<LinkedEntityType, null>;
  linkedEntityId?: string;
  visibility?: PostVisibility;
}

export interface UploadUrlResponse {
  uploadUrl: string;
  fileUrl: string;
  key: string;
}

export interface AttachMediaPayload {
  mediaType: 'IMAGE' | 'VIDEO';
  url: string;
  thumbnailUrl?: string;
  width?: number;
  height?: number;
  durationSeconds?: number;
  displayOrder?: number;
}

// Phase A surfaces only these post types in the composer
export const PHASE_A_POST_TYPES: { value: PostType; label: string; roles: ('ARTIST' | 'VENUE')[] }[] = [
  { value: 'GENERAL', label: 'General', roles: ['ARTIST', 'VENUE'] },
  { value: 'ANNOUNCEMENT', label: 'Announcement', roles: ['ARTIST', 'VENUE'] },
  { value: 'EVENT_PHOTO', label: 'Event Photo', roles: ['ARTIST', 'VENUE'] },
  { value: 'UPCOMING_EVENT', label: 'Upcoming Event', roles: ['VENUE'] },
];
