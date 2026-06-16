import { PageHeader, ComingSoon } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/store/authStore';
import { useProfileStats } from '@/features/profile/hooks/useProfileStats';
import { AuthorPostsTab } from '@/features/posts/components/AuthorPostsTab';

export function ProfilePage() {
  const actor = useAuthStore((s) => s.actor);
  const isArtist = actor?.authorType === 'ARTIST';
  const initials = (actor?.displayName || '?').slice(0, 2).toUpperCase();
  const { data: stats } = useProfileStats(actor?.authorType, actor?.authorId);

  return (
    <div>
      <PageHeader title="Profile" />

      {/* Zone 1 — Identity header */}
      <GlassCard className="overflow-hidden mb-4">
        <div className="h-32 bg-gradient-to-br from-primary/30 to-secondary/50" />
        <div className="p-5 flex items-end gap-4 -mt-10">
          <Avatar className="h-20 w-20 ring-4 ring-bg">
            {actor?.avatarUrl && <AvatarImage src={actor.avatarUrl} />}
            <AvatarFallback className="text-xl">{initials}</AvatarFallback>
          </Avatar>
          <div className="flex-1 pb-1">
            <h2 className="font-display text-2xl">{actor?.displayName}</h2>
            <p className="text-sm text-ink/50 capitalize">{actor?.authorType?.toLowerCase()}</p>
          </div>
          <Button variant="outline" size="sm" disabled>Edit profile</Button>
        </div>
      </GlassCard>

      {/* Zone 2 — Stats row (live from profile-stats) */}
      <div className="grid grid-cols-3 gap-3 mb-4">
        {[
          { label: 'Posts', value: stats?.posts ?? 0 },
          { label: 'Followers', value: stats?.followers ?? 0 },
          { label: 'Following', value: stats?.following ?? 0 },
        ].map((s) => (
          <GlassCard key={s.label} className="p-4 text-center">
            <div className="font-display text-2xl text-primary">{s.value}</div>
            <div className="text-xs text-ink/50">{s.label}</div>
          </GlassCard>
        ))}
      </div>

      {/* Zone 3 — Tabs */}
      <Tabs defaultValue="posts">
        <TabsList className="mb-4">
          <TabsTrigger value="posts">Posts</TabsTrigger>
          <TabsTrigger value="portfolio">{isArtist ? 'Portfolio' : 'Gallery'}</TabsTrigger>
          <TabsTrigger value="activity">{isArtist ? 'Marketplace Activity' : 'Opportunities'}</TabsTrigger>
        </TabsList>
        <TabsContent value="posts">
          {actor && <AuthorPostsTab authorType={actor.authorType} authorId={actor.authorId} />}
        </TabsContent>
        <TabsContent value="portfolio">
          <GlassCard className="p-5"><ComingSoon phase="Phase A (media gallery view)" /></GlassCard>
        </TabsContent>
        <TabsContent value="activity">
          <GlassCard className="p-5"><ComingSoon phase="marketplace baseline" /></GlassCard>
        </TabsContent>
      </Tabs>
    </div>
  );
}
