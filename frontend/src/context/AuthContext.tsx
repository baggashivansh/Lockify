import { createContext, useContext, useState, useCallback, ReactNode } from 'react'
import { api, AuthTokens, UserInfo } from '../lib/api'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  user: UserInfo | null
  isAuthenticated: boolean
  setTokens: (tokens: AuthTokens & { user?: UserInfo }) => void
  logout: () => void
  refreshAccessToken: () => Promise<boolean>
}

const AuthContext = createContext<AuthState | null>(null)
const STORAGE_KEY = 'lockify_auth'

function loadStored(): Partial<AuthState> {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : {}
  } catch {
    return {}
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const stored = loadStored()
  const [accessToken, setAccessToken] = useState<string | null>(stored.accessToken ?? null)
  const [refreshToken, setRefreshToken] = useState<string | null>(stored.refreshToken ?? null)
  const [user, setUser] = useState<UserInfo | null>(stored.user ?? null)

  const persist = useCallback((at: string | null, rt: string | null, u: UserInfo | null) => {
    if (at) localStorage.setItem(STORAGE_KEY, JSON.stringify({ accessToken: at, refreshToken: rt, user: u }))
    else localStorage.removeItem(STORAGE_KEY)
  }, [])

  const setTokens = useCallback((tokens: AuthTokens & { user?: UserInfo }) => {
    setAccessToken(tokens.accessToken)
    setRefreshToken(tokens.refreshToken)
    const u = tokens.user ?? user
    if (tokens.user) setUser(tokens.user)
    persist(tokens.accessToken, tokens.refreshToken, u)
  }, [persist, user])

  const logout = useCallback(() => {
    setAccessToken(null)
    setRefreshToken(null)
    setUser(null)
    localStorage.removeItem(STORAGE_KEY)
  }, [])

  const refreshAccessToken = useCallback(async () => {
    if (!refreshToken) return false
    const res = await api.refresh(refreshToken)
    if (res.ok && res.data) {
      setTokens(res.data)
      return true
    }
    logout()
    return false
  }, [refreshToken, setTokens, logout])

  return (
    <AuthContext.Provider value={{
      accessToken, refreshToken, user,
      isAuthenticated: !!accessToken,
      setTokens, logout, refreshAccessToken,
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
