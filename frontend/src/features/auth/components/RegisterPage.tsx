import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Mic2, Building2 } from 'lucide-react';
import { AuthLayout } from '../components/AuthLayout';
import { registerSchema, type RegisterValues } from '../schemas';
import { useRegister } from '../hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/lib/utils';

export function RegisterPage() {
  const { register, handleSubmit, watch, setValue, formState: { errors } } =
    useForm<RegisterValues>({
      resolver: zodResolver(registerSchema),
      defaultValues: { role: 'ARTIST' },
    });
  const role = watch('role');
  const signup = useRegister();

  return (
    <AuthLayout
      title="Join Aarohan"
      subtitle="Create your account to start connecting"
      footer={<>Already have an account? <Link to="/login" className="text-primary font-semibold">Sign in</Link></>}
    >
      <form onSubmit={handleSubmit((v) => signup.mutate(v))} className="space-y-4">
        <div className="grid grid-cols-2 gap-3">
          {([
            { value: 'ARTIST', label: 'Artist', icon: Mic2, desc: 'Perform & get booked' },
            { value: 'VENUE', label: 'Venue', icon: Building2, desc: 'Host & hire talent' },
          ] as const).map((opt) => {
            const active = role === opt.value;
            const Icon = opt.icon;
            return (
              <motion.button
                type="button"
                key={opt.value}
                whileTap={{ scale: 0.97 }}
                onClick={() => setValue('role', opt.value)}
                className={cn(
                  'rounded-card border p-4 text-left transition-all',
                  active ? 'border-primary bg-primary/5 shadow-soft' : 'border-border bg-white/50'
                )}
              >
                <Icon className={cn('h-5 w-5 mb-2', active ? 'text-primary' : 'text-ink/40')} />
                <div className="font-semibold text-sm">{opt.label}</div>
                <div className="text-xs text-ink/50">{opt.desc}</div>
              </motion.button>
            );
          })}
        </div>

        <div className="space-y-1.5">
          <Label htmlFor="displayName">{role === 'ARTIST' ? 'Artist / stage name' : 'Venue name'}</Label>
          <Input id="displayName" {...register('displayName')} />
          {errors.displayName && <p className="text-xs text-destructive">{errors.displayName.message}</p>}
        </div>
        <div className="space-y-1.5">
          <Label htmlFor="location">Location <span className="text-ink/40">(optional)</span></Label>
          <Input id="location" placeholder="City, Country" {...register('location')} />
        </div>
        <div className="space-y-1.5">
          <Label htmlFor="email">Email</Label>
          <Input id="email" type="email" {...register('email')} />
          {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
        </div>
        <div className="space-y-1.5">
          <Label htmlFor="password">Password</Label>
          <Input id="password" type="password" {...register('password')} />
          {errors.password && <p className="text-xs text-destructive">{errors.password.message}</p>}
        </div>
        {signup.isError && <p className="text-sm text-destructive">Could not register. Email may be in use.</p>}
        <Button type="submit" className="w-full" disabled={signup.isPending}>
          {signup.isPending ? 'Creating account…' : 'Create account'}
        </Button>
      </form>
    </AuthLayout>
  );
}
