import { useState } from 'react'
import { api } from '../lib/api'
import { useAuth } from '../context/AuthContext'
import { ApiEndpoint } from '../components/ApiEndpoint'

export default function ResourcesPage() {
  const { accessToken } = useAuth()
  const [title, setTitle] = useState('My Document')
  const [content, setContent] = useState('Sample content')
  const [resourceId, setResourceId] = useState('1')

  const auth = () => {
    if (!accessToken) return Promise.resolve({ ok: false, status: 0, error: 'Login required', duration: 0 } as const)
    return null
  }

  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-800 mb-2">Phase 6 — Resources</h1>
      <p className="text-slate-500 mb-8">Resource ownership + ABAC demo</p>

      <ApiEndpoint method="POST" path="/api/resources" description="Naya resource banao (owner = you)" protected
        onExecute={async () => (await auth()) || api.post('/api/resources', { title, content }, accessToken)}>
        <input className="glass-input" placeholder="Title" value={title} onChange={e => setTitle(e.target.value)} />
        <input className="glass-input" placeholder="Content" value={content} onChange={e => setContent(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="GET" path="/api/resources" description="Apne resources list karo" protected
        onExecute={async () => (await auth()) || api.get('/api/resources', accessToken)} />

      <ApiEndpoint method="GET" path="/api/resources/{id}" description="Ek resource get karo (ownership check)" protected
        onExecute={async () => (await auth()) || api.get(`/api/resources/${resourceId}`, accessToken)}>
        <input className="glass-input" placeholder="Resource ID" value={resourceId} onChange={e => setResourceId(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="PUT" path="/api/resources/{id}" description="Resource update (owner only)" protected
        onExecute={async () => (await auth()) || api.put(`/api/resources/${resourceId}`, { title, content }, accessToken)}>
        <input className="glass-input" placeholder="Resource ID" value={resourceId} onChange={e => setResourceId(e.target.value)} />
        <input className="glass-input" placeholder="Title" value={title} onChange={e => setTitle(e.target.value)} />
      </ApiEndpoint>

      <ApiEndpoint method="DELETE" path="/api/resources/{id}" description="Resource delete (owner only)" protected
        onExecute={async () => (await auth()) || api.delete(`/api/resources/${resourceId}`, accessToken)}>
        <input className="glass-input" placeholder="Resource ID" value={resourceId} onChange={e => setResourceId(e.target.value)} />
      </ApiEndpoint>
    </div>
  )
}
