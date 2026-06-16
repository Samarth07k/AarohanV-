import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { LandingPage } from '@/pages/LandingPage';
import { LoginPage } from '@/features/auth/components/LoginPage';
import { RegisterPage } from '@/features/auth/components/RegisterPage';
import { ForgotPasswordPage } from '@/features/auth/components/ForgotPasswordPage';
import { ResetPasswordPage } from '@/features/auth/components/ResetPasswordPage';
import { AppShell } from '@/shared/components/AppShell';
import { ArtistDashboardPage } from '@/pages/artist/DashboardPage';
import { VenueDashboardPage } from '@/pages/venue/DashboardPage';
import { FeedPage } from '@/pages/FeedPage';
import { ProfilePage } from '@/pages/ProfilePage';
import { OpportunitiesPage } from '@/pages/marketplace/OpportunitiesPage';
import { OpportunityDetailPage } from '@/pages/marketplace/OpportunityDetailPage';
import { ApplicationsPage } from '@/pages/marketplace/ApplicationsPage';
import { NegotiationsPage } from '@/pages/marketplace/NegotiationsPage';
import { NegotiationDetailPage } from '@/pages/marketplace/NegotiationDetailPage';
import { BookingsPage } from '@/pages/marketplace/BookingsPage';
import { MessagingPage } from '@/pages/marketplace/MessagingPage';
import { NotificationsPage } from '@/pages/marketplace/NotificationsPage';

function ProtectedRoute() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
}

function RoleHome() {
  const actor = useAuthStore((s) => s.actor);
  if (!actor) return <Navigate to="/login" replace />;
  return <Navigate to={actor.authorType === 'ARTIST' ? '/artist/dashboard' : '/venue/dashboard'} replace />;
}

const sharedMarketplaceRoutes = [
  { path: 'opportunities', element: <OpportunitiesPage /> },
  { path: 'opportunities/:id', element: <OpportunityDetailPage /> },
  { path: 'applications', element: <ApplicationsPage /> },
  { path: 'negotiations', element: <NegotiationsPage /> },
  { path: 'negotiations/:id', element: <NegotiationDetailPage /> },
  { path: 'bookings', element: <BookingsPage /> },
  { path: 'messaging', element: <MessagingPage /> },
  { path: 'messaging/:conversationId', element: <MessagingPage /> },
  { path: 'notifications', element: <NotificationsPage /> },
  { path: 'profile', element: <ProfilePage /> },
];

const artistRoutes = [
  { path: 'dashboard', element: <ArtistDashboardPage /> },
  ...sharedMarketplaceRoutes,
];

const venueRoutes = [
  { path: 'dashboard', element: <VenueDashboardPage /> },
  ...sharedMarketplaceRoutes,
];

export const router = createBrowserRouter([
  { path: '/', element: <LandingPage /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },
  { path: '/forgot-password', element: <ForgotPasswordPage /> },
  { path: '/reset-password', element: <ResetPasswordPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      { path: '/home', element: <RoleHome /> },
      {
        element: <AppShell />,
        children: [
          { path: '/feed', element: <FeedPage /> },
          { path: '/artist', children: artistRoutes },
          { path: '/venue', children: venueRoutes },
        ],
      },
    ],
  },
  { path: '*', element: <Navigate to="/" replace /> },
]);
