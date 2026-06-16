import type { AuthorType } from './index';

export interface LikeActor {
  actorType: AuthorType;
  actorId: string;
  displayName: string;
  avatarUrl: string | null;
}
