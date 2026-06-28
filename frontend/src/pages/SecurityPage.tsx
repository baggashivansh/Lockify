import { api } from '../lib/api'
import { useAuth } from '../context/AuthContext'
import { ApiEndpoint } from '../components/ApiEndpoint'
import { GlassCard } from '../components/GlassCard'

export default function SecurityPage() {
  const { accessToken } = useAuth()

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Phase 7 — Security Events</h1>
      <p className="text-slate-500 mb-8">Suspicious logins, token reuse, fingerprinting</p>

      <GlassCard className="mb-6">
        <p className="text-sm text-slate-600">Admin role chahiye security events dekhne ke liye. Normal USER ko 403 milega — yeh expected hai.</p>
      </GlassCard>

      <ApiEndpoint method="GET" path="/api/security/events" description="Security events list (ADMIN)" protected
        onExecute={() => {
          if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 })
          return api.get('/api/security/events', accessToken)
        }} />
    </div>
  )
}
