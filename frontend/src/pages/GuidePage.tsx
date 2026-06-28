import { GlassCard } from '../components/GlassCard'
import { CheckCircle2, Terminal, Database, Shield } from 'lucide-react'

const steps = [
  {
    step: 1,
    title: 'Start Backend',
    code: 'docker compose up -d\nmvn spring-boot:run',
    desc: 'PostgreSQL + Redis start karo, phir Spring Boot on :8080',
  },
  {
    step: 2,
    title: 'Start Frontend',
    code: 'cd frontend && npm install && npm run dev',
    desc: 'Console open hoga http://localhost:5173 pe',
  },
  {
    step: 3,
    title: 'Register User',
    desc: 'Phase 1 → Auth → Register. Password: min 8 chars, upper+lower+digit+special',
    path: '/auth',
  },
  {
    step: 4,
    title: 'Login',
    desc: 'Email ya username se login karo. Tokens auto-save honge.',
    path: '/auth',
  },
  {
    step: 5,
    title: 'Test Protected APIs',
    desc: 'Sessions, MFA, Resources — sab sections me "Send Request" dabao. Token auto-attach hota hai.',
  },
  {
    step: 6,
    title: 'Check Server Logs',
    desc: 'Email verification + password reset tokens console me log hote hain (dev mode).',
  },
]

export default function GuidePage() {
  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-slate-800 mb-2">Getting Started</h1>
        <p className="text-slate-500">Postman ki zaroorat nahi — saari Lockify APIs yahan se test karo</p>
      </div>

      <GlassCard title="Quick Status" className="mb-6">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          {[
            { icon: Terminal, label: 'Frontend', value: 'localhost:5173' },
            { icon: Database, label: 'Backend', value: 'localhost:8080' },
            { icon: Shield, label: 'Auth', value: 'JWT + Refresh' },
          ].map(({ icon: Icon, label, value }) => (
            <div key={label} className="flex items-center gap-3 p-3 rounded-xl bg-white/40">
              <Icon className="w-5 h-5 text-apple-blue" />
              <div>
                <p className="text-xs text-slate-500">{label}</p>
                <p className="text-sm font-medium text-slate-700">{value}</p>
              </div>
            </div>
          ))}
        </div>
      </GlassCard>

      <div className="space-y-4">
        {steps.map(s => (
          <GlassCard key={s.step}>
            <div className="flex gap-4">
              <div className="w-8 h-8 rounded-full bg-apple-blue/10 text-apple-blue flex items-center justify-center font-bold text-sm shrink-0">
                {s.step}
              </div>
              <div className="flex-1">
                <h3 className="font-semibold text-slate-800 flex items-center gap-2">
                  {s.title}
                  <CheckCircle2 className="w-4 h-4 text-apple-blue/50" />
                </h3>
                <p className="text-sm text-slate-500 mt-1">{s.desc}</p>
                {s.code && (
                  <pre className="mt-2 p-3 rounded-xl bg-slate-800/90 text-emerald-300 text-xs font-mono">{s.code}</pre>
                )}
              </div>
            </div>
          </GlassCard>
        ))}
      </div>

      <GlassCard title="Password Rules" className="mt-6">
        <ul className="text-sm text-slate-600 space-y-1 list-disc list-inside">
          <li>Minimum 8 characters</li>
          <li>At least 1 uppercase, 1 lowercase, 1 number, 1 special char (@$!%*?&)</li>
          <li>Example: <code className="bg-white/60 px-1.5 py-0.5 rounded">Password@123</code></li>
        </ul>
      </GlassCard>
    </div>
  )
}
