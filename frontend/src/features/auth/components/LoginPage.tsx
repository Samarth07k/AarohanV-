import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link } from 'react-router-dom';
import { AuthLayout } from '../components/AuthLayout';
import { loginSchema, type LoginValues } from '../schemas';
import { useLogin } from '../hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export function LoginPage() {
  const { register, handleSubmit, formState: { errors } } = useForm<LoginValues>({
    resolver: zodResolver(loginSchema),
  });
  const login = useLogin();

  return (
    <AuthLayout
      title="Welcome back"
      subtitle="Sign in to your Aarohan account"
      footer={<>New here? <Link to="/register" className="text-primary font-semibold">Create an account</Link></>}
    >
      <form onSubmit={handleSubmit((v) => login.mutate(v))} className="space-y-4">
        <div className="space-y-1.5">
          <Label htmlFor="email">Email</Label>
          <Input id="email" type="email" placeholder="you@example.com" {...register('email')} />
          {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
        </div>
        <div className="space-y-1.5">
          <Label htmlFor="password">Password</Label>
          <Input id="password" type="password" placeholder="••••••••" {...register('password')} />
          {errors.password && <p className="text-xs text-destructive">{errors.password.message}</p>}
        </div>
        {login.isError && (
          <p className="text-sm text-destructive">Invalid email or password.</p>
        )}
        <Button type="submit" className="w-full" disabled={login.isPending}>
          {login.isPending ? 'Signing in…' : 'Sign in'}
        </Button>
        <div className="text-center">
          <Link to="/forgot-password" className="text-xs text-ink/50 hover:text-primary">Forgot password?</Link>
        </div>
      </form>
    </AuthLayout>
  );
}
