import { PageHeader, ComingSoon } from '@/shared/components/PageScaffold';
import { GlassCard } from '@/components/ui/card';
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs';
import { useUIStore } from '@/store/uiStore';
import { HomeFeed } from '@/features/feed/components/HomeFeed';

export function FeedPage() {
  const { feedTab, setFeedTab } = useUIStore();
  return (
    <div>
      <PageHeader title="Feed" subtitle="Discover what artists and venues are sharing" />
      <Tabs value={feedTab} onValueChange={(v) => setFeedTab(v as typeof feedTab)}>
        <TabsList className="mb-4">
          <TabsTrigger value="home">Home</TabsTrigger>
          <TabsTrigger value="following">Following</TabsTrigger>
          <TabsTrigger value="trending">Trending</TabsTrigger>
        </TabsList>
        <TabsContent value="home">
          <HomeFeed />
        </TabsContent>
        <TabsContent value="following">
          <GlassCard className="p-5"><ComingSoon phase="Phase B" /></GlassCard>
        </TabsContent>
        <TabsContent value="trending">
          <GlassCard className="p-5"><ComingSoon phase="Phase D (deferred)" /></GlassCard>
        </TabsContent>
      </Tabs>
    </div>
  );
}
