import { create } from 'zustand';

type FeedTab = 'home' | 'following' | 'trending';

interface Toast {
  id: string;
  message: string;
  variant: 'default' | 'success' | 'error';
}

interface UIState {
  feedTab: FeedTab;
  composerOpen: boolean;
  toasts: Toast[];
  setFeedTab: (tab: FeedTab) => void;
  setComposerOpen: (open: boolean) => void;
  pushToast: (message: string, variant?: Toast['variant']) => void;
  dismissToast: (id: string) => void;
}

export const useUIStore = create<UIState>((set) => ({
  feedTab: 'home',
  composerOpen: false,
  toasts: [],
  setFeedTab: (feedTab) => set({ feedTab }),
  setComposerOpen: (composerOpen) => set({ composerOpen }),
  pushToast: (message, variant = 'default') =>
    set((s) => ({
      toasts: [...s.toasts, { id: crypto.randomUUID(), message, variant }],
    })),
  dismissToast: (id) => set((s) => ({ toasts: s.toasts.filter((t) => t.id !== id) })),
}));
