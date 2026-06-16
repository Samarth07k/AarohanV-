import { useState } from 'react';
import { Link } from 'react-router-dom';
import { AuthLayout } from './AuthLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export function ForgotPasswordPage() {
  const [sent, setSent] = useState(false);
  return (
    <AuthLayout
      title="Reset password"
      subtitle="We'll send a reset link to your email"
      footer={<Link to="/login" className="text-primary font-semibold">Back to sign in</Link>}
    >
      {sent ? (
        <p className="text-sm text-ink/70">
          If an account exists for that email, a reset link is on its way.
        </p>
      ) : (
        <form
          onSubmit={(e) => { e.preventDefault(); setSent(true); }}
          className="space-y-4"
        >
          <div className="space-y-1.5">
            <Label htmlFor="email">Email</Label>
            <Input id="email" type="email" required placeholder="you@example.com" />
          </div>
          <Button type="submit" className="w-full">Send reset link</Button>
        </form>
      )}
    </AuthLayout>
  );
}
