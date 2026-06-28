import { Routes, Route } from 'react-router-dom'
import { Layout } from './components/Layout'
import GuidePage from './pages/GuidePage'
import DashboardPage from './pages/DashboardPage'
import AuthPage from './pages/AuthPage'
import AccountPage from './pages/AccountPage'
import SessionsPage from './pages/SessionsPage'
import MfaPage from './pages/MfaPage'
import OAuthPage from './pages/OAuthPage'
import ResourcesPage from './pages/ResourcesPage'
import SecurityPage from './pages/SecurityPage'
import AdminPage from './pages/AdminPage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<GuidePage />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="auth" element={<AuthPage />} />
        <Route path="account" element={<AccountPage />} />
        <Route path="sessions" element={<SessionsPage />} />
        <Route path="mfa" element={<MfaPage />} />
        <Route path="oauth" element={<OAuthPage />} />
        <Route path="resources" element={<ResourcesPage />} />
        <Route path="security" element={<SecurityPage />} />
        <Route path="admin" element={<AdminPage />} />
      </Route>
    </Routes>
  )
}
