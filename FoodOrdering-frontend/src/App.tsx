import { Routes, Route, Navigate } from 'react-router-dom'
import MainLayout from './layouts/MainLayout'
import AdminLayout from './layouts/AdminLayout'

// Customer Pages
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import RestaurantDetail from './pages/RestaurantDetail'
import Profile from './pages/Profile'
import Cart from './pages/Cart'
import Orders from './pages/Orders'
import OrderTracking from './pages/OrderTracking'
import PaymentCallback from './pages/PaymentCallback'

// Admin Pages
import Dashboard from './pages/admin/Dashboard'
import Restaurants from './pages/admin/Restaurants'
import Users from './pages/admin/Users'
import Analytics from './pages/admin/Analytics'
import AuditLogs from './pages/admin/AuditLogs'

export default function App() {
  return (
    <Routes>
      {/* Auth routes (outside layout) */}
      <Route path="/auth/login" element={<Login />} />
      <Route path="/auth/register" element={<Register />} />
      <Route path="/payment/callback" element={<PaymentCallback />} />

      {/* Customer layout */}
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Home />} />
        <Route path="restaurant/:id" element={<RestaurantDetail />} />
        <Route path="cart" element={<Cart />} />
        <Route path="orders" element={<Orders />} />
        <Route path="orders/:id" element={<OrderTracking />} />
        <Route path="profile" element={<Profile />} />
      </Route>

      {/* Admin panel */}
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="analytics" element={<Analytics />} />
        <Route path="restaurants" element={<Restaurants />} />
        <Route path="users" element={<Users />} />
        <Route path="audit" element={<AuditLogs />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
