import { create } from 'zustand';
import type { ActorInfo } from '@/types';

const STORAGE_KEY = 'aarohan.auth';

interface PersistedAuth {
  accessToken: string | null;
  refreshToken: string | null;
  actor: ActorInfo | null;
}

function load(): PersistedAuth {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) return JSON.parse(raw);
  } catch {
    /* ignore */
  }
  return { accessToken: null, refreshToken: null, actor: null };
}

function persist(state: PersistedAuth) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  } catch {
    /* ignore */
  }
}

interface AuthState extends PersistedAuth {
  isAuthenticated: boolean;
  setSession: (accessToken: string, refreshToken: string, actor: ActorInfo) => void;
  setAccessToken: (accessToken: string) => void;
  logout: () => void;
}

const initial = load();

export const useAuthStore = create<AuthState>((set, get) => ({
  ...initial,
  isAuthenticated: Boolean(initial.accessToken),
  setSession: (accessToken, refreshToken, actor) => {
    const next = { accessToken, refreshToken, actor };
    persist(next);
    set({ ...next, isAuthenticated: true });
  },
  setAccessToken: (accessToken) => {
    const { refreshToken, actor } = get();
    const next = { accessToken, refreshToken, actor };
    persist(next);
    set({ accessToken });
  },
  logout: () => {
    persist({ accessToken: null, refreshToken: null, actor: null });
    set({ accessToken: null, refreshToken: null, actor: null, isAuthenticated: false });
  },
}));
