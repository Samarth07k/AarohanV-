import { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { AuthLayout } from './AuthLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export function ResetPasswordPage() {
  const [params] = useSearchParams();
  const token = params.get('token');
  const [done, setDone] = useState(false);
  return (
    <AuthLayout
      title="Choose a new password"
      footer={<Link to="/login" className="text-primary font-semibold">Back to sign in</Link>}
    >
      {done ? (
        <p className="text-sm text-ink/70">Your password has been reset. You can now sign in.</p>
      ) : (
        <form onSubmit={(e) => { e.preventDefault(); setDone(true); }} className="space-y-4">
          {!token && <p className="text-xs text-destructive">Missing or invalid reset token.</p>}
          <div className="space-y-1.5">
            <Label htmlFor="password">New password</Label>
            <Input id="password" type="password" required minLength={8} />
          </div>
          <Button type="submit" className="w-full" disabled={!token}>Reset password</Button>
        </form>
      )}
    </AuthLayout>
  );
}
