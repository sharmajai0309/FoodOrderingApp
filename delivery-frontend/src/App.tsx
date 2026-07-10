import { Routes, Route, Navigate } from 'react-router-dom'
import PartnerLayout from './layouts/PartnerLayout'
import PartnerDashboard from './pages/partner/PartnerDashboard'
import ActiveDelivery from './pages/partner/ActiveDelivery'
import PartnerLogin from './pages/partner/PartnerLogin'
import PartnerRegister from './pages/partner/PartnerRegister'
import DocumentUpload from './pages/partner/DocumentUpload'
import StatusPending from './pages/partner/StatusPending'
import PartnerEarnings from './pages/partner/PartnerEarnings'
import DeliveryHistory from './pages/partner/DeliveryHistory'
import PartnerProfile from './pages/partner/PartnerProfile'
import { useAuthStore } from './store/useAuthStore'

// Must be logged in as a delivery partner
function AuthRoute({ children }: { children: React.ReactNode }) {
  const user = useAuthStore((state) => state.user)
  if (!user || user.role !== 'DELIVERY_PARTNER') return <Navigate to="/login" replace />
  return <>{children}</>
}

// Must be ACTIVE to access main dashboard
function ActiveRoute({ children }: { children: React.ReactNode }) {
  const user = useAuthStore((state) => state.user)
  if (!user || user.role !== 'DELIVERY_PARTNER') return <Navigate to="/login" replace />

  if (user.status === 'PENDING' || user.status === 'DOCUMENT_UPLOAD_PENDING') {
    return <Navigate to="/upload" replace />
  }
  if (user.status === 'DOCUMENT_VERIFICATION_PENDING' || user.status === 'DOCUMENT_VERIFICATION_REJECTED') {
    return <Navigate to="/status" replace />
  }
  return <>{children}</>
}

export default function App() {
  return (
    <Routes>
      {/* Public auth routes */}
      <Route path="/login" element={<PartnerLogin />} />
      <Route path="/register" element={<PartnerRegister />} />

      {/* Onboarding (require auth, not active status) */}
      <Route path="/upload" element={<AuthRoute><DocumentUpload /></AuthRoute>} />
      <Route path="/status" element={<AuthRoute><StatusPending /></AuthRoute>} />

      {/* Main app (require ACTIVE status) */}
      <Route path="/" element={<ActiveRoute><PartnerLayout /></ActiveRoute>}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<PartnerDashboard />} />
        <Route path="active" element={<ActiveDelivery />} />
        <Route path="earnings" element={<PartnerEarnings />} />
        <Route path="history" element={<DeliveryHistory />} />
        <Route path="profile" element={<PartnerProfile />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
