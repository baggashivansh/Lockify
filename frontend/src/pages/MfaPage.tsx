import { useState } from 'react'
import { api } from '../lib/api'
import { useAuth } from '../context/AuthContext'
import { ApiEndpoint } from '../components/ApiEndpoint'
import { GlassCard } from '../components/GlassCard'

export default function MfaPage() {
  const { accessToken } = useAuth()
  const [code, setCode] = useState('')

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Phase 4 — MFA</h1>
      <p className="text-slate-500 mb-8">TOTP setup, verify, enable, disable</p>

      <GlassCard className="mb-6">
        <p className="text-sm text-slate-600">Flow: <strong>Setup</strong> → scan QR (secret response me) → <strong>Verify</strong> → <strong>Enable</strong></p>
      </GlassCard>

      <ApiEndpoint method="POST" path="/api/mfa/setup" description="TOTP secret generate karo" protected
        onExecute={() => api.post('/api/mfa/setup', {}, accessToken)} />

      <ApiEndpoint method="POST" path="/api/mfa/verify" description="6-digit TOTP code verify karo" protected
        onExecute={() => api.post('/api/mfa/verify', { code, type: 'TOTP' }, accessToken)}>
        <input className="glass-input" placeholder="6-digit code" value={code} onChange={e => setCode(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="POST" path="/api/mfa/enable" description="MFA enable karo (verified code chahiye)" protected
        onExecute={() => api.post('/api/mfa/enable', { code, type: 'TOTP' }, accessToken)}>
        <input className="glass-input" placeholder="6-digit code" value={code} onChange={e => setCode(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="POST" path="/api/mfa/disable" description="MFA disable karo" protected
        onExecute={() => api.post('/api/mfa/disable', { code, type: 'TOTP' }, accessToken)}>
        <input className="glass-input" placeholder="6-digit code" value={code} onChange={e => setCode(e.target.value)} />
      </ApiEndpoint>
    </div>
  )
}
