import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Footer } from './Footer'
import {
  Shield, BookOpen, LogIn, User, Key, Monitor, Fingerprint,
  Link2, FileText, AlertTriangle, LayoutDashboard, LogOut,
} from 'lucide-react'
import { useEffect, useState } from 'react'
import { api } from '../lib/api'

const nav = [
  { to: '/', icon: BookOpen, label: 'Guide' },
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/auth', icon: LogIn, label: 'Auth' },
  { to: '/account', icon: Key, label: 'Account' },
  { to: '/sessions', icon: Monitor, label: 'Sessions' },
  { to: '/mfa', icon: Fingerprint, label: 'MFA' },
  { to: '/oauth', icon: Link2, label: 'OAuth' },
  { to: '/resources', icon: FileText, label: 'Resources' },
  { to: '/security', icon: AlertTriangle, label: 'Security' },
  { to: '/admin', icon: Shield, label: 'Admin' },
]

export function Layout() {
  const { isAuthenticated, user, logout } = useAuth()
  const [backendUp, setBackendUp] = useState<boolean | null>(null)

  useEffect(() => {
    api.health().then(r => setBackendUp(r.ok))
    const id = setInterval(() => api.health().then(r => setBackendUp(r.ok)), 30000)
    return () => clearInterval(id)
  }, [])

  return (
    <div className="relative min-h-screen pb-20">
      <div className="bg-orb w-96 h-96 bg-apple-blue/20 -top-20 -left-20" />
      <div className="bg-orb w-80 h-80 bg-blue-300/20 top-1/3 -right-20" />
      <div className="bg-orb w-64 h-64 bg-apple-blue/10 bottom-20 left-1/3" />

      <div className="relative z-10 flex min-h-screen flex-col lg:flex-row">
        {/* Mobile header */}
        <div className="lg:hidden p-4 glass-strong mx-4 mt-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Shield className="w-5 h-5 text-apple-blue" />
            <span className="font-bold text-slate-800">Lockify</span>
          </div>
          <span className={`text-xs px-2 py-1 rounded-full ${backendUp ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'}`}>
            {backendUp ? 'Online' : 'Offline'}
          </span>
        </div>
        <div className="lg:hidden overflow-x-auto px-4 py-2 flex gap-2 scrollbar-hide">
          {nav.map(({ to, label }) => (
            <NavLink key={to} to={to} end={to === '/'}
              className={({ isActive }) => `shrink-0 px-3 py-1.5 rounded-full text-xs font-medium transition ${
                isActive ? 'bg-apple-blue text-white' : 'bg-white/50 text-slate-600'
              }`}>{label}</NavLink>
          ))}
        </div>

        {/* Sidebar */}
        <aside className="w-64 shrink-0 p-4 hidden lg:block">
          <div className="glass-strong p-5 sticky top-4">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-apple-blue to-apple-blue-light flex items-center justify-center shadow-lg shadow-apple-blue/30">
                <Shield className="w-5 h-5 text-white" />
              </div>
              <div>
                <h1 className="font-bold text-slate-800">Lockify</h1>
                <p className="text-xs text-slate-500">API Console</p>
              </div>
            </div>

            <div className={`flex items-center gap-2 text-xs mb-4 px-2 py-1.5 rounded-lg ${
              backendUp === null ? 'bg-slate-100' : backendUp ? 'bg-emerald-50 text-emerald-700' : 'bg-red-50 text-red-700'
            }`}>
              <span className={`w-2 h-2 rounded-full ${
                backendUp === null ? 'bg-slate-400' : backendUp ? 'bg-emerald-500 animate-pulse' : 'bg-red-500'
              }`} />
              {backendUp === null ? 'Checking...' : backendUp ? 'Backend Online' : 'Backend Offline'}
            </div>

            <nav className="space-y-0.5">
              {nav.map(({ to, icon: Icon, label }) => (
                <NavLink
                  key={to}
                  to={to}
                  end={to === '/'}
                  className={({ isActive }) =>
                    `flex items-center gap-2.5 px-3 py-2 rounded-xl text-sm transition-all ${
                      isActive
                        ? 'bg-apple-blue/10 text-apple-blue font-medium'
                        : 'text-slate-600 hover:bg-white/50 hover:text-slate-800'
                    }`
                  }
                >
                  <Icon className="w-4 h-4 shrink-0" />
                  <span className="truncate">{label}</span>
                </NavLink>
              ))}
            </nav>

            {isAuthenticated && (
              <div className="mt-4 pt-4 border-t border-white/50">
                <div className="flex items-center gap-2 px-2 mb-2">
                  <User className="w-4 h-4 text-apple-blue" />
                  <span className="text-xs text-slate-600 truncate">{user?.username}</span>
                </div>
                <button onClick={logout} className="flex items-center gap-2 w-full px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-xl transition">
                  <LogOut className="w-4 h-4" /> Logout
                </button>
              </div>
            )}
          </div>
        </aside>

        {/* Main */}
        <main className="flex-1 p-4 lg:p-8 max-w-4xl">
          <Outlet />
        </main>
      </div>
      <Footer />
    </div>
  )
}
