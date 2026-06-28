import { ApiResult } from '../lib/api'
import { CheckCircle2, XCircle, Clock } from 'lucide-react'

export function ResponseViewer({ result }: { result: ApiResult | null }) {
  if (!result) return null

  return (
    <div className={`mt-4 rounded-xl border p-4 text-sm font-mono overflow-auto max-h-80 ${
      result.ok ? 'bg-emerald-50/60 border-emerald-200/60' : 'bg-red-50/60 border-red-200/60'
    }`}>
      <div className="flex items-center gap-3 mb-2 font-sans">
        {result.ok ? (
          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
        ) : (
          <XCircle className="w-4 h-4 text-red-600" />
        )}
        <span className={result.ok ? 'text-emerald-700' : 'text-red-700'}>
          {result.status || 'ERR'} — {result.ok ? 'Success' : result.error}
        </span>
        <span className="flex items-center gap-1 text-slate-400 ml-auto">
          <Clock className="w-3.5 h-3.5" /> {result.duration}ms
        </span>
      </div>
      <pre className="text-xs text-slate-700 whitespace-pre-wrap break-all">
        {JSON.stringify(result.data ?? result.error, null, 2)}
      </pre>
    </div>
  )
}
