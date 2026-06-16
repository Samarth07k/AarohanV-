import { api } from '@/api/client';
import { endpoints } from '@/api/endpoints';
import type { AuthResponse } from '@/types';

export interface RegisterPayload {
  email: string;
  password: string;
  role: 'ARTIST' | 'VENUE';
  displayName: string;
  location?: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export const authService = {
  async register(payload: RegisterPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>(endpoints.auth.register, payload);
    return data;
  },
  async login(payload: LoginPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>(endpoints.auth.login, payload);
    return data;
  },
  async logout(refreshToken: string): Promise<void> {
    await api.post(endpoints.auth.logout, { refreshToken });
  },
};
