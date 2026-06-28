import { useState, ReactNode } from 'react'
import { ApiResult } from '../lib/api'
import { ResponseViewer } from './ResponseViewer'
import { Play, Loader2 } from 'lucide-react'

interface ApiEndpointProps {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE'
  path: string
  description: string
  protected?: boolean
  onExecute: () => Promise<ApiResult>
  children?: ReactNode
}

const methodBadge: Record<string, string> = {
  GET: 'badge-get', POST: 'badge-post', PUT: 'badge-put', DELETE: 'badge-delete',
}

export function ApiEndpoint({ method, path, description, protected: isProtected, onExecute, children }: ApiEndpointProps) {
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState<ApiResult | null>(null)

  const run = async () => {
    setLoading(true)
    setResult(await onExecute())
    setLoading(false)
  }

  return (
    <div className="glass p-5 mb-4">
      <div className="flex flex-wrap items-center gap-2 mb-2">
        <span className={`badge ${methodBadge[method]}`}>{method}</span>
        <code className="text-sm text-slate-700 font-mono">{path}</code>
        <span className={`badge ${isProtected ? 'badge-protected' : 'badge-public'}`}>
          {isProtected ? '🔒 Auth' : '🌐 Public'}
        </span>
      </div>
      <p className="text-sm text-slate-500 mb-4">{description}</p>
      {children && <div className="space-y-3 mb-4">{children}</div>}
      <button onClick={run} disabled={loading} className="btn-primary flex items-center gap-2 text-sm">
        {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Play className="w-4 h-4" />}
        Send Request
      </button>
      <ResponseViewer result={result} />
    </div>
  )
}
