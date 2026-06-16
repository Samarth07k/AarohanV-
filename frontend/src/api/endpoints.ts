import type { AuthorType } from '@/types';

const authorPath = (type: AuthorType) => (type === 'ARTIST' ? 'artists' : 'venues');

export const endpoints = {
  auth: {
    register: '/auth/register',
    login: '/auth/login',
    refresh: '/auth/refresh',
    logout: '/auth/logout',
    me: '/auth/me',
  },
  posts: {
    create: '/posts',
    detail: (id: string) => `/posts/${id}`,
    byAuthor: (type: AuthorType, id: string) => `/${authorPath(type)}/${id}/posts`,
  },
  media: {
    uploadUrl: '/media/upload-url',
    attach: (postId: string) => `/posts/${postId}/media`,
    remove: (postId: string, mediaId: string) => `/posts/${postId}/media/${mediaId}`,
  },
  comments: {
    list: (postId: string) => `/posts/${postId}/comments`,
    add: (postId: string) => `/posts/${postId}/comments`,
    remove: (id: string) => `/comments/${id}`,
  },
  likes: {
    like: (postId: string) => `/posts/${postId}/likes`,
    list: (postId: string) => `/posts/${postId}/likes`,
  },
  follows: {
    follow: '/follows',
    unfollow: (type: AuthorType, id: string) => `/follows/${type}/${id}`,
    status: '/follows/status',
    followers: (type: AuthorType, id: string) => `/${authorPath(type)}/${id}/followers`,
    following: (type: AuthorType, id: string) => `/${authorPath(type)}/${id}/following`,
  },
  feed: {
    home: '/feed/home',
    following: '/feed/following',
    trending: '/feed/trending',
    activity: (type: AuthorType, id: string) => `/${authorPath(type)}/${id}/activity`,
  },
  profile: {
    detail: (type: AuthorType, id: string) => `/${authorPath(type)}/${id}`,
    stats: (type: AuthorType, id: string) => `/${authorPath(type)}/${id}/profile-stats`,
    contentStats: (type: AuthorType, id: string) => `/${authorPath(type)}/${id}/content-stats`,
  },
} as const;

// ---- Marketplace endpoints ----
export const marketplaceEndpoints = {
  opportunities: {
    create: '/opportunities',
    discover: '/opportunities',
    mine: '/opportunities/mine',
    detail: (id: string) => `/opportunities/${id}`,
    update: (id: string) => `/opportunities/${id}`,
    close: (id: string) => `/opportunities/${id}/close`,
    applications: (id: string) => `/opportunities/${id}/applications`,
  },
  applications: {
    submit: '/applications',
    detail: (id: string) => `/applications/${id}`,
    withdraw: (id: string) => `/applications/${id}/withdraw`,
    status: (id: string) => `/applications/${id}/status`,
    mine: '/applications/mine',
    received: '/applications/received',
  },
  negotiations: {
    detail: (id: string) => `/negotiations/${id}`,
    byApplication: (applicationId: string) => `/negotiations/by-application/${applicationId}`,
    offers: (id: string) => `/negotiations/${id}/offers`,
    accept: (id: string) => `/negotiations/${id}/accept`,
    reject: (id: string) => `/negotiations/${id}/reject`,
  },
  bookings: {
    mine: '/bookings/mine',
    detail: (id: string) => `/bookings/${id}`,
    status: (id: string) => `/bookings/${id}/status`,
  },
  conversations: {
    mine: '/conversations',
    detail: (id: string) => `/conversations/${id}`,
    messages: (id: string) => `/conversations/${id}/messages`,
    read: (id: string) => `/conversations/${id}/read`,
  },
  notifications: {
    list: '/notifications',
    unreadCount: '/notifications/unread-count',
    read: (id: string) => `/notifications/${id}/read`,
    readAll: '/notifications/read-all',
  },
} as const;
