import type { AuthorType } from './index';

export interface FollowTarget {
  type: AuthorType;
  id: string;
  displayName: string;
  avatarUrl: string | null;
}

export interface FollowStatus {
  isFollowing: boolean;
}
