import type { AuthorType } from './index';

export interface Comment {
  id: string;
  postId: string;
  authorType: AuthorType;
  authorId: string;
  authorDisplayName: string;
  authorAvatarUrl: string | null;
  content: string;
  createdAt: string;
}
