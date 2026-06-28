import { useAuth } from '../context/AuthContext'
import { GlassCard } from '../components/GlassCard'
import { User, Key, Clock, Shield } from 'lucide-react'

export default function DashboardPage() {
  const { isAuthenticated, user, accessToken, refreshToken } = useAuth()

  const truncate = (s: string | null, n = 24) =>
    s ? s.slice(0, n) + '...' : '—'

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Dashboard</h1>
      <p className="text-slate-500 mb-8">Current session aur token status</p>

      <div className="grid gap-4 sm:grid-cols-2">
        <GlassCard title="Auth Status">
          <div className={`flex items-center gap-2 text-lg font-medium ${isAuthenticated ? 'text-emerald-600' : 'text-slate-400'}`}>
            <Shield className="w-5 h-5" />
            {isAuthenticated ? 'Authenticated' : 'Not logged in'}
          </div>
          {!isAuthenticated && (
            <p className="text-sm text-slate-500 mt-2">Phase 1 → Auth se login karo</p>
          )}
        </GlassCard>

        {isAuthenticated && user && (
          <GlassCard title="Current User">
            <div className="space-y-2 text-sm">
              <div className="flex items-center gap-2"><User className="w-4 h-4 text-apple-blue" /> {user.username}</div>
              <div className="text-slate-500">{user.email}</div>
              <div className="flex gap-1 flex-wrap">
                {user.roles.map(r => (
                  <span key={r} className="badge badge-protected">{r}</span>
                ))}
              </div>
            </div>
          </GlassCard>
        )}

        <GlassCard title="Access Token">
          <div className="flex items-start gap-2">
            <Key className="w-4 h-4 text-apple-blue mt-0.5 shrink-0" />
            <code className="text-xs text-slate-600 break-all font-mono">{truncate(accessToken, 48)}</code>
          </div>
        </GlassCard>

        <GlassCard title="Refresh Token">
          <div className="flex items-start gap-2">
            <Clock className="w-4 h-4 text-apple-blue mt-0.5 shrink-0" />
            <code className="text-xs text-slate-600 break-all font-mono">{truncate(refreshToken, 48)}</code>
          </div>
        </GlassCard>
      </div>
    </div>
  )
}
