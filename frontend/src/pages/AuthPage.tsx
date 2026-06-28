import { useState } from 'react'
import { api } from '../lib/api'
import { useAuth } from '../context/AuthContext'
import { ApiEndpoint } from '../components/ApiEndpoint'
import { GlassCard } from '../components/GlassCard'

export default function AuthPage() {
  const { setTokens, accessToken, refreshToken } = useAuth()
  const [reg, setReg] = useState({ username: '', email: '', password: 'Password@123' })
  const [login, setLogin] = useState({ identifier: '', password: 'Password@123' })

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Phase 1 — Authentication</h1>
      <p className="text-slate-500 mb-8">Register, Login, JWT, Refresh, Profile</p>

      <GlassCard title="Tip" className="mb-6">
        <p className="text-sm text-slate-600">
          Pehle <strong>Register</strong> karo, phir <strong>Login</strong>. Tokens automatically save ho jayenge — baaki pages pe auth auto-attach hoga.
        </p>
      </GlassCard>

      <ApiEndpoint
        method="POST" path="/api/auth/register" description="Naya user banao — default USER role milega"
        onExecute={async () => api.register(reg.username, reg.email, reg.password)}
      >
        <input className="glass-input" placeholder="Username" value={reg.username} onChange={e => setReg({ ...reg, username: e.target.value })} />
        <input className="glass-input" placeholder="Email" type="email" value={reg.email} onChange={e => setReg({ ...reg, email: e.target.value })} />
        <input className="glass-input" placeholder="Password" type="password" value={reg.password} onChange={e => setReg({ ...reg, password: e.target.value })} />
      </ApiEndpoint>

      <ApiEndpoint
        method="POST" path="/api/auth/login" description="Email ya username se login — access + refresh token milta hai"
        onExecute={async () => {
          const res = await api.login(login.identifier, login.password)
          if (res.ok && res.data) setTokens(res.data)
          return res
        }}
      >
        <input className="glass-input" placeholder="Email or Username" value={login.identifier} onChange={e => setLogin({ ...login, identifier: e.target.value })} />
        <input className="glass-input" placeholder="Password" type="password" value={login.password} onChange={e => setLogin({ ...login, password: e.target.value })} />
      </ApiEndpoint>

      <ApiEndpoint
        method="POST" path="/api/auth/refresh" description="Access token expire hone pe refresh karo"
        onExecute={async () => {
          if (!refreshToken) return { ok: false, status: 0, error: 'No refresh token — pehle login karo', duration: 0 }
          const res = await api.refresh(refreshToken)
          if (res.ok && res.data) setTokens(res.data)
          return res
        }}
      />

      <ApiEndpoint
        method="GET" path="/api/auth/me" description="Current logged-in user ki profile" protected
        onExecute={async () => {
          if (!accessToken) return { ok: false, status: 0, error: 'Login required', duration: 0 }
          return api.me(accessToken)
        }}
      />
    </div>
  )
}
