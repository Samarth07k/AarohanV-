export type OpportunityStatus = 'OPEN' | 'CLOSED' | 'CANCELLED';
export type ApplicationStatus = 'PENDING' | 'REVIEWING' | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN';
export type NegotiationStatus = 'OPEN' | 'AGREED' | 'DECLINED';
export type OfferParty = 'ARTIST' | 'VENUE';
export type BookingStatus = 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';

export interface Opportunity {
  id: string;
  venueId: string;
  venueName: string;
  venueLocation: string | null;
  title: string;
  description: string;
  eventDate: string | null;
  budgetMin: number | null;
  budgetMax: number | null;
  status: OpportunityStatus;
  applicationCount: number;
  hasApplied: boolean;
  myApplicationId: string | null;
  createdAt: string;
}

export interface Application {
  id: string;
  opportunityId: string;
  opportunityTitle: string;
  venueId: string;
  venueName: string;
  artistId: string;
  artistName: string;
  artistAvatarUrl: string | null;
  coverMessage: string;
  status: ApplicationStatus;
  negotiationId: string | null;
  createdAt: string;
}

export interface Offer {
  id: string;
  negotiationId: string;
  offeredBy: OfferParty;
  amount: number;
  terms: string;
  createdAt: string;
}

export interface Negotiation {
  id: string;
  applicationId: string;
  opportunityId: string;
  opportunityTitle: string;
  artistId: string;
  artistName: string;
  artistAvatarUrl: string | null;
  venueId: string;
  venueName: string;
  status: NegotiationStatus;
  offers: Offer[];
  latestOffer: Offer | null;
  bookingId: string | null;
  createdAt: string;
}

export interface Booking {
  id: string;
  negotiationId: string;
  artistId: string;
  artistName: string;
  artistAvatarUrl: string | null;
  venueId: string;
  venueName: string;
  venueLocation: string | null;
  agreedAmount: number;
  eventDate: string | null;
  status: BookingStatus;
  conversationId: string | null;
  createdAt: string;
}

export interface Conversation {
  id: string;
  bookingId: string;
  artistId: string;
  artistName: string;
  artistAvatarUrl: string | null;
  venueId: string;
  venueName: string;
  venueAvatarUrl: string | null;
  lastMessage: string | null;
  updatedAt: string;
}

export interface Message {
  id: string;
  conversationId: string;
  senderType: OfferParty;
  senderId: string;
  content: string;
  read: boolean;
  createdAt: string;
}

export interface AppNotification {
  id: string;
  type: string;
  relatedEntityType: string | null;
  relatedEntityId: string | null;
  title: string;
  body: string;
  count: number;
  read: boolean;
  createdAt: string;
}
