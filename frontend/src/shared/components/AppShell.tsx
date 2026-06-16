import { type ReactNode } from 'react';
import { Link, useLocation, Outlet } from 'react-router-dom';
import {
  Coffee, LayoutDashboard, Rss, Briefcase, FileText, Handshake,
  CalendarCheck, MessageCircle, Bell, User, LogOut,
} from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { useLogout } from '@/features/auth/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { cn } from '@/lib/utils';

interface NavItem { to: string; label: string; icon: ReactNode; }

export function AppShell() {
  const actor = useAuthStore((s) => s.actor);
  const logout = useLogout();
  const location = useLocation();
  const base = actor?.authorType === 'ARTIST' ? '/artist' : '/venue';

  const items: NavItem[] = [
    { to: `${base}/dashboard`, label: 'Dashboard', icon: <LayoutDashboard className="h-5 w-5" /> },
    { to: `/feed`, label: 'Feed', icon: <Rss className="h-5 w-5" /> },
    { to: `${base}/opportunities`, label: 'Opportunities', icon: <Briefcase className="h-5 w-5" /> },
    { to: `${base}/applications`, label: 'Applications', icon: <FileText className="h-5 w-5" /> },
    { to: `${base}/negotiations`, label: 'Negotiations', icon: <Handshake className="h-5 w-5" /> },
    { to: `${base}/bookings`, label: 'Bookings', icon: <CalendarCheck className="h-5 w-5" /> },
    { to: `${base}/messaging`, label: 'Messaging', icon: <MessageCircle className="h-5 w-5" /> },
    { to: `${base}/notifications`, label: 'Notifications', icon: <Bell className="h-5 w-5" /> },
    { to: `${base}/profile`, label: 'Profile', icon: <User className="h-5 w-5" /> },
  ];

  const initials = (actor?.displayName || actor?.email || '?').slice(0, 2).toUpperCase();

  return (
    <div className="min-h-screen flex flex-col md:flex-row">
      {/* Sidebar (desktop) */}
      <aside className="hidden md:flex md:w-64 flex-col border-r border-border bg-white/40 backdrop-blur-glass p-4">
        <Link to="/" className="flex items-center gap-2 font-display text-xl text-primary px-2 py-3">
          <Coffee className="h-6 w-6" /> Aarohan
        </Link>
        <nav className="flex-1 space-y-1 mt-4">
          {items.map((item) => {
            const active = location.pathname === item.to;
            return (
              <Link key={item.to} to={item.to}
                className={cn(
                  'flex items-center gap-3 rounded-control px-3 py-2.5 text-sm font-medium transition-colors',
                  active ? 'bg-primary text-bg' : 'text-ink/70 hover:bg-ink/5'
                )}>
                {item.icon} {item.label}
              </Link>
            );
          })}
        </nav>
        <div className="border-t border-border pt-3 mt-3">
          <div className="flex items-center gap-3 px-2 mb-2">
            <Avatar className="h-9 w-9">
              {actor?.avatarUrl && <AvatarImage src={actor.avatarUrl} />}
              <AvatarFallback>{initials}</AvatarFallback>
            </Avatar>
            <div className="min-w-0">
              <div className="text-sm font-semibold truncate">{actor?.displayName || 'You'}</div>
              <div className="text-xs text-ink/50 capitalize">{actor?.authorType?.toLowerCase()}</div>
            </div>
          </div>
          <Button variant="ghost" size="sm" className="w-full justify-start" onClick={() => logout.mutate()}>
            <LogOut className="h-4 w-4" /> Sign out
          </Button>
        </div>
      </aside>

      {/* Top bar (mobile) */}
      <header className="md:hidden flex items-center justify-between border-b border-border bg-white/60 backdrop-blur-glass px-4 py-3 sticky top-0 z-20">
        <Link to="/" className="flex items-center gap-2 font-display text-lg text-primary">
          <Coffee className="h-5 w-5" /> Aarohan
        </Link>
        <Button variant="ghost" size="icon" onClick={() => logout.mutate()}><LogOut className="h-5 w-5" /></Button>
      </header>

      {/* Main content */}
      <main className="flex-1 min-w-0 al-botanical-bg">
        <div className="container py-6 md:py-8 max-w-4xl">
          <Outlet />
        </div>
      </main>

      {/* Bottom tab bar (mobile) */}
      <nav className="md:hidden fixed bottom-0 inset-x-0 z-20 flex justify-around border-t border-border bg-white/80 backdrop-blur-glass py-2">
        {items.slice(0, 5).map((item) => {
          const active = location.pathname === item.to;
          return (
            <Link key={item.to} to={item.to}
              className={cn('flex flex-col items-center gap-0.5 px-2 py-1 text-[10px]',
                active ? 'text-primary' : 'text-ink/50')}>
              {item.icon}
            </Link>
          );
        })}
      </nav>
    </div>
  );
}
