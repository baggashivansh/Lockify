import { api } from '../lib/api'
import { useAuth } from '../context/AuthContext'
import { ApiEndpoint } from '../components/ApiEndpoint'
import { GlassCard } from '../components/GlassCard'

export default function OAuthPage() {
  const { accessToken } = useAuth()

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Phase 5 — OAuth</h1>
      <p className="text-slate-500 mb-8">Linked social accounts</p>

      <GlassCard className="mb-6">
        <p className="text-sm text-slate-600">
          Google/GitHub OAuth ke liye <code className="bg-white/60 px-1 rounded">GOOGLE_CLIENT_ID</code> env set karo.
          Browser se: <code className="bg-white/60 px-1 rounded">http://localhost:8080/oauth2/authorization/google</code>
        </p>
      </GlassCard>

      <ApiEndpoint method="GET" path="/api/oauth2/linked-accounts" description="Linked OAuth accounts list" protected
        onExecute={() => {
          if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 })
          return api.get('/api/oauth2/linked-accounts', accessToken)
        }} />
    </div>
  )
}
