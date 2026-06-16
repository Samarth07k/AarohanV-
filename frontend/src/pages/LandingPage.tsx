import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Coffee, Mic2, Building2, Heart, MessageCircle, Users,
  Sparkles, ArrowRight, CalendarCheck, FileText, Handshake,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { GlassCard } from '@/components/ui/card';
import { BotanicalCursorGlow } from '@/shared/components/BotanicalCursorGlow';

const fadeUp = {
  initial: { opacity: 0, y: 24 },
  whileInView: { opacity: 1, y: 0 },
  viewport: { once: true, margin: '-80px' },
  transition: { duration: 0.5, ease: 'easeOut' },
};

function Nav() {
  return (
    <header className="relative z-10">
      <div className="container flex items-center justify-between py-5">
        <Link to="/" className="flex items-center gap-2 font-display text-xl text-primary">
          <Coffee className="h-6 w-6" /> Aarohan
        </Link>
        <nav className="flex items-center gap-2">
          <Button asChild variant="ghost" size="sm"><Link to="/login">Sign in</Link></Button>
          <Button asChild size="sm"><Link to="/register">Get started</Link></Button>
        </nav>
      </div>
    </header>
  );
}

function Hero() {
  return (
    <section className="al-botanical-bg relative overflow-hidden">
      <BotanicalCursorGlow />
      <div className="container relative z-10 grid lg:grid-cols-2 gap-12 items-center py-20 md:py-28">
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, ease: 'easeOut' }}
        >
          <span className="inline-flex items-center gap-2 rounded-full bg-primary/10 px-4 py-1.5 text-sm font-medium text-primary mb-6">
            <Sparkles className="h-4 w-4" /> Where artists and cafés meet
          </span>
          <h1 className="font-display text-5xl md:text-6xl leading-[1.05] mb-6">
            Discover talent.<br />Book the room.<br />
            <span className="text-primary">Grow your craft.</span>
          </h1>
          <p className="text-lg text-ink/70 max-w-md mb-8">
            A warm, welcoming home for artists and cafés — a social discovery
            layer for visibility and reputation, and a marketplace for real bookings.
          </p>
          <div className="flex flex-wrap gap-3">
            <Button asChild size="lg"><Link to="/register">Join as Artist <Mic2 className="h-4 w-4" /></Link></Button>
            <Button asChild size="lg" variant="outline"><Link to="/register">List your Café <Building2 className="h-4 w-4" /></Link></Button>
          </div>
        </motion.div>

        <div className="relative h-[460px] hidden lg:block">
          <motion.div
            className="absolute top-0 right-8 w-72"
            initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, duration: 0.6 }}
          >
            <GlassCard className="p-5 animate-float">
              <div className="flex items-center gap-3 mb-3">
                <div className="h-10 w-10 rounded-full bg-secondary/60" />
                <div>
                  <div className="font-semibold text-sm">Luna Vega</div>
                  <div className="text-xs text-ink/50">Jazz vocalist · Lisbon</div>
                </div>
              </div>
              <p className="text-sm text-ink/70 mb-3">New live clip from Saturday's set 🎙️</p>
              <div className="h-28 rounded-xl bg-gradient-to-br from-primary/20 to-secondary/40" />
              <div className="flex gap-4 mt-3 text-ink/50 text-sm">
                <span className="flex items-center gap-1"><Heart className="h-4 w-4" /> 248</span>
                <span className="flex items-center gap-1"><MessageCircle className="h-4 w-4" /> 31</span>
              </div>
            </GlassCard>
          </motion.div>

          <motion.div
            className="absolute bottom-4 left-0 w-64"
            initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4, duration: 0.6 }}
          >
            <GlassCard className="p-5" style={{ animationDelay: '1.5s' }}>
              <span className="inline-block rounded-full bg-accent/15 px-3 py-1 text-xs font-medium text-accent mb-2">
                Open opportunity
              </span>
              <div className="font-semibold mb-1">Friday Night Live</div>
              <div className="text-xs text-ink/50 mb-3">The Greenhouse · 250 cap</div>
              <div className="flex items-center justify-between">
                <span className="text-sm font-semibold text-primary">€400–600</span>
                <Button size="sm" variant="secondary">Apply</Button>
              </div>
            </GlassCard>
          </motion.div>
        </div>
      </div>
    </section>
  );
}

function ShowcaseSection() {
  const artists = [
    { name: 'Luna Vega', tag: 'Jazz · Lisbon' },
    { name: 'The Hollow Pines', tag: 'Folk · Dublin' },
    { name: 'Adaeze', tag: 'Afrobeat · London' },
  ];
  const venues = [
    { name: 'The Greenhouse', tag: '250 cap · Berlin' },
    { name: 'Cellar 88', tag: '120 cap · Porto' },
    { name: 'Open Sky Roof', tag: '400 cap · Athens' },
  ];
  return (
    <section className="al-section container">
      <motion.div {...fadeUp} className="grid md:grid-cols-2 gap-10">
        <div>
          <h2 className="font-display text-3xl mb-6 flex items-center gap-2"><Mic2 className="h-6 w-6 text-primary" /> Artists</h2>
          <div className="space-y-3">
            {artists.map((a) => (
              <GlassCard key={a.name} className="p-4 flex items-center gap-3">
                <div className="h-12 w-12 rounded-full bg-secondary/60 shrink-0" />
                <div><div className="font-semibold">{a.name}</div><div className="text-sm text-ink/50">{a.tag}</div></div>
              </GlassCard>
            ))}
          </div>
        </div>
        <div>
          <h2 className="font-display text-3xl mb-6 flex items-center gap-2"><Building2 className="h-6 w-6 text-primary" /> Venues</h2>
          <div className="space-y-3">
            {venues.map((v) => (
              <GlassCard key={v.name} className="p-4 flex items-center gap-3">
                <div className="h-12 w-12 rounded-xl bg-primary/20 shrink-0" />
                <div><div className="font-semibold">{v.name}</div><div className="text-sm text-ink/50">{v.tag}</div></div>
              </GlassCard>
            ))}
          </div>
        </div>
      </motion.div>
    </section>
  );
}

function WorkflowSection() {
  const steps = [
    { icon: Building2, label: 'Opportunity', desc: 'Venue posts a slot' },
    { icon: FileText, label: 'Application', desc: 'Artist applies' },
    { icon: Handshake, label: 'Negotiation', desc: 'Agree on terms' },
    { icon: CalendarCheck, label: 'Booking', desc: 'Confirmed gig' },
    { icon: MessageCircle, label: 'Messaging', desc: 'Artist ↔ Venue' },
  ];
  return (
    <section className="al-section al-botanical-bg">
      <div className="container">
        <motion.div {...fadeUp} className="text-center mb-12">
          <h2 className="font-display text-4xl mb-3">The marketplace workflow</h2>
          <p className="text-ink/60 max-w-xl mx-auto">From the first post to a confirmed booking — auditable end to end.</p>
        </motion.div>
        <div className="flex flex-wrap justify-center items-stretch gap-3">
          {steps.map((s, i) => (
            <motion.div key={s.label} {...fadeUp} transition={{ delay: i * 0.08, duration: 0.5 }}
              className="flex items-center gap-3">
              <GlassCard className="p-5 w-40 text-center">
                <s.icon className="h-6 w-6 text-primary mx-auto mb-2" />
                <div className="font-semibold text-sm">{s.label}</div>
                <div className="text-xs text-ink/50">{s.desc}</div>
              </GlassCard>
              {i < steps.length - 1 && <ArrowRight className="h-5 w-5 text-ink/30 hidden md:block" />}
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}

function SocialSection() {
  const features = [
    { icon: FileText, label: 'Posts & Media' },
    { icon: Heart, label: 'Likes' },
    { icon: MessageCircle, label: 'Comments' },
    { icon: Users, label: 'Follows' },
  ];
  return (
    <section className="al-section container">
      <motion.div {...fadeUp} className="grid lg:grid-cols-2 gap-10 items-center">
        <div>
          <h2 className="font-display text-4xl mb-4">A social layer built for reputation</h2>
          <p className="text-ink/70 mb-6">
            Share performance clips, event photos, and announcements. Build a following.
            Let your work be discovered — fast, visible, and entirely separate from the
            transactional marketplace.
          </p>
          <div className="grid grid-cols-2 gap-3">
            {features.map((f) => (
              <GlassCard key={f.label} className="p-4 flex items-center gap-3">
                <f.icon className="h-5 w-5 text-primary" />
                <span className="font-medium text-sm">{f.label}</span>
              </GlassCard>
            ))}
          </div>
        </div>
        <GlassCard className="p-6 space-y-4">
          {[1, 2].map((i) => (
            <div key={i} className="border-b border-border last:border-0 pb-4 last:pb-0">
              <div className="flex items-center gap-3 mb-2">
                <div className="h-9 w-9 rounded-full bg-secondary/60" />
                <div className="text-sm"><span className="font-semibold">Artist {i}</span> <span className="text-ink/40">· 2h</span></div>
              </div>
              <div className="h-24 rounded-xl bg-gradient-to-br from-primary/15 to-accent/15 mb-2" />
              <div className="flex gap-4 text-ink/50 text-sm">
                <Heart className="h-4 w-4" /> <MessageCircle className="h-4 w-4" />
              </div>
            </div>
          ))}
        </GlassCard>
      </motion.div>
    </section>
  );
}

function Testimonials() {
  const quotes = [
    { q: 'I booked three festivals in a month. The whole flow just works.', a: 'Adaeze, Afrobeat artist' },
    { q: 'Finding the right acts used to take weeks. Now it takes an afternoon.', a: 'The Greenhouse, Berlin' },
    { q: 'My following grew faster here than anywhere else.', a: 'Luna Vega, vocalist' },
  ];
  return (
    <section className="al-section al-botanical-bg">
      <div className="container">
        <motion.h2 {...fadeUp} className="font-display text-4xl text-center mb-12">Loved by the community</motion.h2>
        <div className="grid md:grid-cols-3 gap-6">
          {quotes.map((t, i) => (
            <motion.div key={i} {...fadeUp} transition={{ delay: i * 0.1, duration: 0.5 }}>
              <GlassCard className="p-6 h-full">
                <p className="font-display text-lg mb-4 leading-snug">"{t.q}"</p>
                <p className="text-sm text-ink/50">{t.a}</p>
              </GlassCard>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}

function CTASection() {
  return (
    <section className="al-section container">
      <motion.div {...fadeUp}>
        <GlassCard className="p-10 md:p-16 text-center al-botanical-bg">
          <h2 className="font-display text-4xl md:text-5xl mb-4">Ready to be discovered?</h2>
          <p className="text-ink/70 max-w-md mx-auto mb-8">Join Aarohan and start building your presence today.</p>
          <div className="flex flex-wrap justify-center gap-3">
            <Button asChild size="lg"><Link to="/register">Create your account</Link></Button>
            <Button asChild size="lg" variant="outline"><Link to="/login">Sign in</Link></Button>
          </div>
        </GlassCard>
      </motion.div>
    </section>
  );
}

function Footer() {
  return (
    <footer className="border-t border-border">
      <div className="container py-10 flex flex-col md:flex-row items-center justify-between gap-4 text-sm text-ink/50">
        <span className="flex items-center gap-2 font-display text-base text-primary"><Coffee className="h-5 w-5" /> Aarohan</span>
        <span>© {new Date().getFullYear()} Aarohan. Crafted for artists and cafés.</span>
      </div>
    </footer>
  );
}

export function LandingPage() {
  return (
    <div className="min-h-screen">
      <Nav />
      <Hero />
      <ShowcaseSection />
      <WorkflowSection />
      <SocialSection />
      <Testimonials />
      <CTASection />
      <Footer />
    </div>
  );
}
