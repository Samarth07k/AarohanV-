import { api } from '@/api/client';
import { marketplaceEndpoints as ep } from '@/api/endpoints';
import type {
  Opportunity, Application, Negotiation, Booking, Conversation, Message, AppNotification,
} from '@/types/marketplace';
import type { PageResponse } from '@/types';

export const opportunityService = {
  async create(payload: { title: string; description: string; eventDate?: string; budgetMin?: number; budgetMax?: number }) {
    const { data } = await api.post<Opportunity>(ep.opportunities.create, payload);
    return data;
  },
  async discover(cursor?: string): Promise<PageResponse<Opportunity>> {
    const { data } = await api.get<PageResponse<Opportunity>>(ep.opportunities.discover, { params: { cursor } });
    return data;
  },
  async mine(): Promise<Opportunity[]> {
    const { data } = await api.get<Opportunity[]>(ep.opportunities.mine);
    return data;
  },
  async get(id: string): Promise<Opportunity> {
    const { data } = await api.get<Opportunity>(ep.opportunities.detail(id));
    return data;
  },
  async update(id: string, payload: Partial<{ title: string; description: string; eventDate: string; budgetMin: number; budgetMax: number; status: string }>) {
    const { data } = await api.put<Opportunity>(ep.opportunities.update(id), payload);
    return data;
  },
  async close(id: string) {
    const { data } = await api.post<Opportunity>(ep.opportunities.close(id));
    return data;
  },
  async applications(id: string): Promise<Application[]> {
    const { data } = await api.get<Application[]>(ep.opportunities.applications(id));
    return data;
  },
};

export const applicationService = {
  async submit(payload: { opportunityId: string; coverMessage: string }) {
    const { data } = await api.post<Application>(ep.applications.submit, payload);
    return data;
  },
  async get(id: string) {
    const { data } = await api.get<Application>(ep.applications.detail(id));
    return data;
  },
  async withdraw(id: string) {
    const { data } = await api.post<Application>(ep.applications.withdraw(id));
    return data;
  },
  async setStatus(id: string, status: string) {
    const { data } = await api.patch<Application>(ep.applications.status(id), { status });
    return data;
  },
  async mine(): Promise<Application[]> {
    const { data } = await api.get<Application[]>(ep.applications.mine);
    return data;
  },
  async received(): Promise<Application[]> {
    const { data } = await api.get<Application[]>(ep.applications.received);
    return data;
  },
};

export const negotiationService = {
  async get(id: string) {
    const { data } = await api.get<Negotiation>(ep.negotiations.detail(id));
    return data;
  },
  async byApplication(applicationId: string) {
    const { data } = await api.get<Negotiation>(ep.negotiations.byApplication(applicationId));
    return data;
  },
  async sendOffer(id: string, payload: { amount: number; terms: string }) {
    const { data } = await api.post<Negotiation>(ep.negotiations.offers(id), payload);
    return data;
  },
  async accept(id: string) {
    const { data } = await api.post<Negotiation>(ep.negotiations.accept(id));
    return data;
  },
  async reject(id: string) {
    const { data } = await api.post<Negotiation>(ep.negotiations.reject(id));
    return data;
  },
};

export const bookingService = {
  async mine(): Promise<Booking[]> {
    const { data } = await api.get<Booking[]>(ep.bookings.mine);
    return data;
  },
  async get(id: string) {
    const { data } = await api.get<Booking>(ep.bookings.detail(id));
    return data;
  },
  async setStatus(id: string, status: string) {
    const { data } = await api.patch<Booking>(ep.bookings.status(id), { status });
    return data;
  },
};

export const messagingService = {
  async conversations(): Promise<Conversation[]> {
    const { data } = await api.get<Conversation[]>(ep.conversations.mine);
    return data;
  },
  async conversation(id: string) {
    const { data } = await api.get<Conversation>(ep.conversations.detail(id));
    return data;
  },
  async messages(id: string): Promise<Message[]> {
    const { data } = await api.get<Message[]>(ep.conversations.messages(id));
    return data;
  },
  async send(id: string, content: string) {
    const { data } = await api.post<Message>(ep.conversations.messages(id), { content });
    return data;
  },
  async markRead(id: string) {
    await api.post(ep.conversations.read(id));
  },
};

export const notificationService = {
  async list(cursor?: string): Promise<PageResponse<AppNotification>> {
    const { data } = await api.get<PageResponse<AppNotification>>(ep.notifications.list, { params: { cursor } });
    return data;
  },
  async unreadCount(): Promise<number> {
    const { data } = await api.get<{ count: number }>(ep.notifications.unreadCount);
    return data.count;
  },
  async markRead(id: string) {
    await api.post(ep.notifications.read(id));
  },
  async markAllRead() {
    await api.post(ep.notifications.readAll);
  },
};
