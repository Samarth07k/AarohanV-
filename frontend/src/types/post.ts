import type { AuthorType } from './index';
import type { MediaAttachment } from './mediaAttachment';

export type PostType =
  | 'ACHIEVEMENT' | 'PERFORMANCE_CLIP' | 'EVENT_PHOTO' | 'ANNOUNCEMENT'
  | 'UPCOMING_EVENT' | 'VENUE_UPDATE' | 'ARTIST_SPOTLIGHT' | 'VENUE_ACHIEVEMENT'
  | 'GENERAL';

export type PostVisibility = 'PUBLIC' | 'FOLLOWERS_ONLY';
export type PostStatus = 'PUBLISHED' | 'HIDDEN' | 'DELETED';
export type LinkedEntityType = 'OPPORTUNITY' | 'BOOKING' | 'ARTIST' | 'VENUE' | null;

export interface Post {
  id: string;
  authorType: AuthorType;
  authorId: string;
  postType: PostType;
  content: string;
  linkedEntityType: LinkedEntityType;
  linkedEntityId: string | null;
  likeCount: number;
  commentCount: number;
  visibility: PostVisibility;
  status: PostStatus;
  createdAt: string;
  updatedAt: string;
}

export interface FeedItem {
  post: Post;
  author: {
    type: AuthorType;
    id: string;
    displayName: string;
    avatarUrl: string | null;
  };
  mediaAttachments: MediaAttachment[];
  isLikedByCurrentUser: boolean;
  isFollowingAuthor: boolean;
}
