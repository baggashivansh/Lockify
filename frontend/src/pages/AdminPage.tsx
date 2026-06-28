import { api } from '../lib/api'
import { useAuth } from '../context/AuthContext'
import { ApiEndpoint } from '../components/ApiEndpoint'
import { GlassCard } from '../components/GlassCard'

export default function AdminPage() {
  const { accessToken, user } = useAuth()

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Admin & RBAC</h1>
      <p className="text-slate-500 mb-8">Role-based access control test</p>

      <GlassCard className="mb-6">
        <p className="text-sm text-slate-600">
          Tumhare roles: <strong>{user?.roles.join(', ') || 'none'}</strong>.
          USER role se admin endpoints pe <strong>403 Forbidden</strong> aana chahiye — yeh sahi behavior hai.
        </p>
      </GlassCard>

      <ApiEndpoint method="GET" path="/api/admin/dashboard" description="ADMIN ya SUPER_ADMIN only" protected
        onExecute={() => {
          if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 })
          return api.get('/api/admin/dashboard', accessToken)
        }} />

      <ApiEndpoint method="GET" path="/api/admin/users/create-permission-test" description="CREATE permission required" protected
        onExecute={() => {
          if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 })
          return api.get('/api/admin/users/create-permission-test', accessToken)
        }} />

      <ApiEndpoint method="GET" path="/api/admin/super" description="SUPER_ADMIN only" protected
        onExecute={() => {
          if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 })
          return api.get('/api/admin/super', accessToken)
        }} />

      <ApiEndpoint method="GET" path="/api/user/profile" description="ROLE_USER required" protected
        onExecute={() => {
          if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 })
          return api.get('/api/user/profile', accessToken)
        }} />

      <ApiEndpoint method="GET" path="/api/user/read-test" description="READ permission required" protected
        onExecute={() => {
          if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 })
          return api.get('/api/user/read-test', accessToken)
        }} />
    </div>
  )
}
