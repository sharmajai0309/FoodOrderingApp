import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/useAuthStore'
import {
  LayoutDashboard, LogOut, Bike, Map, DollarSign,
  History, User, Bell, Zap, Menu, X, Shield
} from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'
import { useState } from 'react'

const navItems = [
  { name: 'Dashboard',       path: '/dashboard', icon: LayoutDashboard, badge: null },
  { name: 'Active Delivery', path: '/active',    icon: Map,             badge: 'LIVE' },
  { name: 'Earnings',        path: '/earnings',  icon: DollarSign,      badge: null },
  { name: 'History',         path: '/history',   icon: History,         badge: null },
  { name: 'Profile',         path: '/profile',   icon: User,            badge: null },
]

export default function PartnerLayout() {
  const { user, logout } = useAuthStore()
  const location = useLocation()
  const navigate = useNavigate()
  const [mobileOpen, setMobileOpen] = useState(false)

  const handleLogout = () => { logout(); navigate('/login') }

  const SidebarContent = () => (
    <div className="flex flex-col h-full">

      {/* Logo */}
      <div className="px-6 pt-7 pb-6" style={{ borderBottom: '1px solid var(--border)' }}>
        <Link to="/dashboard" className="flex items-center gap-3 group" style={{ textDecoration: 'none' }}>
          <div className="w-10 h-10 rounded-xl flex items-center justify-center glow-blue"
            style={{ background: 'linear-gradient(135deg, #3b82f6, #2563eb)' }}>
            <Bike size={20} className="text-white" />
          </div>
          <div>
            <p style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 18, letterSpacing: '-0.03em', lineHeight: 1 }}>ZapDash</p>
            <p style={{ color: 'var(--text-muted)', fontSize: 10, fontWeight: 700, letterSpacing: '0.15em', textTransform: 'uppercase' }}>Partner Portal</p>
          </div>
        </Link>
      </div>

      {/* Driver chip */}
      <div className="px-4 py-4">
        <div style={{ background: 'var(--blue-soft)', border: '1px solid rgba(59,130,246,0.2)', borderRadius: 12, padding: '12px 14px' }}>
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl flex items-center justify-center font-black text-white text-sm"
              style={{ background: 'linear-gradient(135deg, #3b82f6, #2563eb)', boxShadow: '0 4px 14px rgba(59,130,246,0.3)' }}>
              {user?.username?.charAt(0).toUpperCase() || 'D'}
            </div>
            <div className="flex-1 min-w-0">
              <p style={{ color: 'var(--text-primary)', fontWeight: 700, fontSize: 13 }} className="truncate">{user?.username}</p>
              <div className="flex items-center gap-1.5 mt-0.5">
                <div className="w-1.5 h-1.5 rounded-full online-pulse" style={{ background: 'var(--green)' }} />
                <p style={{ color: 'var(--green)', fontSize: 11, fontWeight: 600 }}>Active</p>
              </div>
            </div>
            <Shield size={13} style={{ color: 'var(--blue-bright)' }} />
          </div>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-4 space-y-0.5">
        <p style={{ color: 'var(--text-muted)', fontSize: 10, fontWeight: 800, letterSpacing: '0.18em', textTransform: 'uppercase', padding: '0 14px 8px' }}>Navigation</p>
        {navItems.map((item) => {
          const isActive = location.pathname === item.path || location.pathname.startsWith(item.path + '/')
          return (
            <Link
              key={item.name}
              to={item.path}
              onClick={() => setMobileOpen(false)}
              className={`nav-item ${isActive ? 'nav-active' : ''}`}
              style={{ position: 'relative' }}
            >
              {isActive && (
                <motion.div
                  layoutId="sidebar-indicator"
                  style={{ position: 'absolute', left: 0, top: '50%', transform: 'translateY(-50%)', width: 3, height: 20, background: 'var(--blue)', borderRadius: '0 4px 4px 0' }}
                />
              )}
              <item.icon size={17} />
              <span className="flex-1">{item.name}</span>
              {item.badge && (
                <span className="badge badge-blue" style={{ fontSize: 9, padding: '2px 7px' }}>{item.badge}</span>
              )}
            </Link>
          )
        })}
      </nav>

      {/* Sign out */}
      <div className="px-4 pb-6 pt-3" style={{ borderTop: '1px solid var(--border)' }}>
        <button
          onClick={handleLogout}
          style={{ width: '100%', display: 'flex', alignItems: 'center', gap: 12, padding: '10px 14px', borderRadius: 10, fontSize: 14, fontWeight: 600, color: 'var(--text-secondary)', background: 'none', border: 'none', cursor: 'pointer', transition: 'all 0.18s' }}
          onMouseEnter={e => { (e.currentTarget as HTMLButtonElement).style.color = 'var(--rose)'; (e.currentTarget as HTMLButtonElement).style.background = 'var(--rose-soft)'; }}
          onMouseLeave={e => { (e.currentTarget as HTMLButtonElement).style.color = 'var(--text-secondary)'; (e.currentTarget as HTMLButtonElement).style.background = 'none'; }}
        >
          <LogOut size={17} />
          <span>Sign Out</span>
        </button>
      </div>
    </div>
  )

  return (
    <div className="min-h-screen flex" style={{ background: 'var(--bg-primary)' }}>

      {/* Desktop Sidebar */}
      <aside className="w-60 hidden lg:flex flex-col shrink-0" style={{ background: 'var(--bg-secondary)', borderRight: '1px solid var(--border)' }}>
        <SidebarContent />
      </aside>

      {/* Mobile Sidebar */}
      <AnimatePresence>
        {mobileOpen && (
          <>
            <motion.div
              initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
              style={{ position: 'fixed', inset: 0, zIndex: 40, background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(4px)' }}
              className="lg:hidden"
              onClick={() => setMobileOpen(false)}
            />
            <motion.aside
              initial={{ x: -260 }} animate={{ x: 0 }} exit={{ x: -260 }}
              transition={{ type: 'spring', damping: 26, stiffness: 300 }}
              style={{ position: 'fixed', left: 0, top: 0, bottom: 0, width: 240, zIndex: 50, background: 'var(--bg-secondary)', borderRight: '1px solid var(--border)' }}
              className="flex flex-col lg:hidden"
            >
              <SidebarContent />
            </motion.aside>
          </>
        )}
      </AnimatePresence>

      {/* Main area */}
      <div className="flex-1 flex flex-col min-h-screen overflow-hidden">

        {/* Header */}
        <header className="h-14 flex items-center justify-between px-5 shrink-0 glass" style={{ borderBottom: '1px solid var(--border)' }}>
          <div className="flex items-center gap-3">
            <button onClick={() => setMobileOpen(true)} className="lg:hidden" style={{ width: 36, height: 36, borderRadius: 8, display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
              {mobileOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
            <p style={{ color: 'var(--text-muted)', fontSize: 12, fontWeight: 700, letterSpacing: '0.1em', textTransform: 'uppercase' }}>
              {navItems.find(i => location.pathname.startsWith(i.path))?.name ?? 'Partner Portal'}
            </p>
          </div>

          <div className="flex items-center gap-2">
            <div className="badge badge-blue hidden sm:flex" style={{ gap: 6 }}>
              <Zap size={11} fill="currentColor" />
              Live
            </div>
            <button style={{ width: 36, height: 36, borderRadius: 8, display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', position: 'relative' }}>
              <Bell size={17} />
              <span style={{ position: 'absolute', top: 6, right: 6, width: 7, height: 7, borderRadius: '50%', background: 'var(--rose)', border: '1.5px solid var(--bg-primary)' }} />
            </button>
            <div className="w-8 h-8 rounded-lg flex items-center justify-center font-black text-white text-sm"
              style={{ background: 'linear-gradient(135deg, #3b82f6, #2563eb)', boxShadow: '0 3px 12px rgba(59,130,246,0.3)' }}>
              {user?.username?.charAt(0).toUpperCase() || 'D'}
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto">
          <div className="p-4 md:p-6 lg:p-8 animate-fade-in">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}
