import { useMutation } from '@tanstack/react-query';
import { authService, type LoginPayload, type RegisterPayload } from '../services/authService';
import { useAuthStore } from '@/store/authStore';
import { useNavigate } from 'react-router-dom';

export function useRegister() {
  const setSession = useAuthStore((s) => s.setSession);
  const navigate = useNavigate();
  return useMutation({
    mutationFn: (payload: RegisterPayload) => authService.register(payload),
    onSuccess: (data) => {
      setSession(data.accessToken, data.refreshToken, data.actor);
      const home = data.actor.authorType === 'ARTIST' ? '/artist/dashboard' : '/venue/dashboard';
      navigate(home);
    },
  });
}

export function useLogin() {
  const setSession = useAuthStore((s) => s.setSession);
  const navigate = useNavigate();
  return useMutation({
    mutationFn: (payload: LoginPayload) => authService.login(payload),
    onSuccess: (data) => {
      setSession(data.accessToken, data.refreshToken, data.actor);
      const home = data.actor.authorType === 'ARTIST' ? '/artist/dashboard' : '/venue/dashboard';
      navigate(home);
    },
  });
}

export function useLogout() {
  const { refreshToken, logout } = useAuthStore.getState();
  const navigate = useNavigate();
  return useMutation({
    mutationFn: async () => {
      if (refreshToken) {
        try { await authService.logout(refreshToken); } catch { /* ignore */ }
      }
    },
    onSettled: () => {
      logout();
      navigate('/login');
    },
  });
}
