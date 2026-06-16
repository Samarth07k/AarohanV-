export type AuthorType = 'ARTIST' | 'VENUE';

export interface ActorInfo {
  userId: string;
  authorType: AuthorType;
  authorId: string;
  email: string;
  displayName: string | null;
  avatarUrl: string | null;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  actor: ActorInfo;
}

export interface PageResponse<T> {
  items: T[];
  nextCursor: string | null;
}
