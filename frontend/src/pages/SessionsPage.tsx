import { useState } from 'react'
import { api } from '../lib/api'
import { useAuth } from '../context/AuthContext'
import { ApiEndpoint } from '../components/ApiEndpoint'

export default function SessionsPage() {
  const { accessToken } = useAuth()
  const [sessionId, setSessionId] = useState('')
  const [deviceId, setDeviceId] = useState('')

  const needAuth = () => {
    if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 })
    return Promise.resolve(null)
  }

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Phase 3 — Sessions</h1>
      <p className="text-slate-500 mb-8">Active sessions, logout everywhere, trusted devices</p>

      <ApiEndpoint method="GET" path="/api/sessions" description="Saari active sessions list karo" protected
        onExecute={async () => (await needAuth()) || api.get('/api/sessions', accessToken)} />

      <ApiEndpoint method="DELETE" path="/api/sessions/{sessionId}" description="Ek session revoke karo" protected
        onExecute={async () => (await needAuth()) || api.delete(`/api/sessions/${sessionId}`, accessToken)}>
        <input className="glass-input" placeholder="Session ID" value={sessionId} onChange={e => setSessionId(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="POST" path="/api/sessions/logout-all" description="Saari devices se logout" protected
        onExecute={async () => (await needAuth()) || api.post('/api/sessions/logout-all', {}, accessToken)} />

      <ApiEndpoint method="GET" path="/api/devices" description="Trusted devices list" protected
        onExecute={async () => (await needAuth()) || api.get('/api/devices', accessToken)} />

      <ApiEndpoint method="POST" path="/api/devices/{deviceId}/trust" description="Device ko trusted mark karo" protected
        onExecute={async () => (await needAuth()) || api.post(`/api/devices/${deviceId}/trust`, {}, accessToken)}>
        <input className="glass-input" placeholder="Device ID" value={deviceId} onChange={e => setDeviceId(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="DELETE" path="/api/devices/{deviceId}" description="Device revoke karo" protected
        onExecute={async () => (await needAuth()) || api.delete(`/api/devices/${deviceId}`, accessToken)}>
        <input className="glass-input" placeholder="Device ID" value={deviceId} onChange={e => setDeviceId(e.target.value)} />
      </ApiEndpoint>
    </div>
  )
}
