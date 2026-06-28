import { useState } from 'react'
import { api } from '../lib/api'
import { useAuth } from '../context/AuthContext'
import { ApiEndpoint } from '../components/ApiEndpoint'
import { GlassCard } from '../components/GlassCard'

export default function AccountPage() {
  const { accessToken } = useAuth()
  const [verifyToken, setVerifyToken] = useState('')
  const [email, setEmail] = useState('')
  const [reset, setReset] = useState({ token: '', newPassword: 'NewPassword@123' })

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Phase 2 — Account Security</h1>
      <p className="text-slate-500 mb-8">Email verification, forgot/reset password</p>

      <GlassCard className="mb-6">
        <p className="text-sm text-slate-600">
          Register ke baad <strong>backend console logs</strong> me email verification token dikhega.
          Copy karke yahan paste karo.
        </p>
      </GlassCard>

      <ApiEndpoint method="POST" path="/api/account/verify-email" description="Email verification token se account verify karo"
        onExecute={() => api.post('/api/account/verify-email', { token: verifyToken })}>
        <input className="glass-input" placeholder="Verification token (from server logs)" value={verifyToken} onChange={e => setVerifyToken(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="POST" path="/api/account/resend-verification" description="Verification email dubara bhejo" protected
        onExecute={() => api.post('/api/account/resend-verification', {}, accessToken)} />

      <ApiEndpoint method="POST" path="/api/account/forgot-password" description="Reset link request — hamesha same response"
        onExecute={() => api.post('/api/account/forgot-password', { email })}>
        <input className="glass-input" placeholder="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="POST" path="/api/account/reset-password" description="Reset token + naya password (token server logs me)"
        onExecute={() => api.post('/api/account/reset-password', reset)}>
        <input className="glass-input" placeholder="Reset token" value={reset.token} onChange={e => setReset({ ...reset, token: e.target.value })} />
        <input className="glass-input" placeholder="New password" type="password" value={reset.newPassword} onChange={e => setReset({ ...reset, newPassword: e.target.value })} />
      </ApiEndpoint>
    </div>
  )
}
