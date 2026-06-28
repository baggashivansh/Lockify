import { ReactNode } from 'react'

interface GlassCardProps {
  title?: string
  subtitle?: string
  children: ReactNode
  className?: string
}

export function GlassCard({ title, subtitle, children, className = '' }: GlassCardProps) {
  return (
    <div className={`glass-strong p-6 ${className}`}>
      {title && (
        <div className="mb-4">
          <h3 className="text-lg font-semibold text-slate-800">{title}</h3>
          {subtitle && <p className="text-sm text-slate-500 mt-0.5">{subtitle}</p>}
        </div>
      )}
      {children}
    </div>
  )
}
