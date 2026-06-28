const API_BASE = import.meta.env.VITE_API_URL || ''

export interface ApiResult<T = unknown> {
  ok: boolean
  status: number
  data?: T
  error?: string
  duration: number
}

export interface AuthTokens {
  accessToken: string
  refreshToken: string
  expiresInSeconds?: number
}

export interface UserInfo {
  id: number
  username: string
  email: string
  roles: string[]
}

async function request<T>(
  method: string,
  path: string,
  body?: unknown,
  token?: string | null,
): Promise<ApiResult<T>> {
  const start = performance.now()
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  if (token) headers['Authorization'] = `Bearer ${token}`

  try {
    const res = await fetch(`${API_BASE}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    })
    const duration = Math.round(performance.now() - start)
    const text = await res.text()
    let data: T | undefined
    try {
      data = text ? JSON.parse(text) : undefined
    } catch {
      data = text as unknown as T
    }
    if (!res.ok) {
      const err = (data as { message?: string })?.message || res.statusText
      return { ok: false, status: res.status, error: err, data, duration }
    }
    return { ok: true, status: res.status, data, duration }
  } catch (e) {
    return {
      ok: false,
      status: 0,
      error: e instanceof Error ? e.message : 'Network error — backend chal raha hai?',
      duration: Math.round(performance.now() - start),
    }
  }
}

export const api = {
  get: <T>(path: string, token?: string | null) => request<T>('GET', path, undefined, token),
  post: <T>(path: string, body?: unknown, token?: string | null) => request<T>('POST', path, body, token),
  put: <T>(path: string, body?: unknown, token?: string | null) => request<T>('PUT', path, body, token),
  delete: <T>(path: string, token?: string | null) => request<T>('DELETE', path, undefined, token),
  health: () => request<{ status: string }>('GET', '/actuator/health'),
  register: (username: string, email: string, password: string) =>
    request('POST', '/api/auth/register', { username, email, password }),
  login: (identifier: string, password: string) =>
    request<AuthTokens & { user: UserInfo }>('POST', '/api/auth/login', { identifier, password }),
  refresh: (refreshToken: string) =>
    request<AuthTokens & { user: UserInfo }>('POST', '/api/auth/refresh', { refreshToken }),
  me: (token: string) => request<UserInfo>('GET', '/api/auth/me', undefined, token),
}
