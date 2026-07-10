import { Outlet, Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/useAuthStore'
import { ShoppingBag, Package, ChevronDown, Flame, LayoutDashboard, User, LogOut, UtensilsCrossed } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api/axios'
import { useState, useRef, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { addAuditLog } from '../pages/admin/AuditLogs'
import LiveOrderBanner from '../components/LiveOrderBanner'

export default function MainLayout() {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()
  const [dropdownOpen, setDropdownOpen] = useState(false)
  const dropdownRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setDropdownOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const { data: cart } = useQuery({
    queryKey: ['cart', user?.id],
    queryFn: async () => {
      if (!user?.id) return null
      const res = await api.get(`/api/Customer/Cart/user/${user.id}`)
      return res.data.data
    },
    enabled: !!user?.id
  })

  const cartCount = cart?.items?.length ?? 0

  const handleLogout = () => {
    addAuditLog({
      userId: user?.id,
      username: user?.username,
      action: 'USER_LOGOUT',
      entity: 'User',
      description: `User ${user?.username} logged out`,
      status: 'SUCCESS',
    })
    logout()
    navigate('/auth/login')
  }

  return (
    <div className="min-h-screen flex flex-col bg-[#f8f5f2] dark:bg-[#0b0f19]">
      <header className="sticky top-0 z-50 bg-white/90 dark:bg-slate-950/90 backdrop-blur-xl border-b border-[#f0ece6] dark:border-border shadow-sm shadow-black/5">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-[68px]">
            
            {/* Brand */}
            <Link to="/" className="flex items-center gap-2.5 group">
              <div className="relative w-9 h-9 bg-primary rounded-xl flex items-center justify-center shadow-lg shadow-primary/30 group-hover:scale-110 transition-transform">
                <Flame size={18} className="text-white" />
              </div>
              <span className="text-xl font-black tracking-tight">
                <span className="text-slate-900 dark:text-white">Crave</span>
                <span className="text-primary">Rush</span>
              </span>
            </Link>

            {/* Center Nav (authenticated) */}
            {user && (
              <nav className="hidden md:flex items-center gap-1 bg-slate-100/80 dark:bg-slate-900/80 rounded-2xl p-1.5 border border-slate-200/50 dark:border-slate-800">
                <NavLink to="/" end className={({ isActive }) => `px-4 py-2 rounded-xl text-sm font-bold transition-all ${isActive ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-sm' : 'text-slate-500 hover:text-slate-900 dark:hover:text-white'}`}>
                  <span className="flex items-center gap-1.5"><UtensilsCrossed size={14} /> Restaurants</span>
                </NavLink>
                {user.role === 'CUSTOMER' && (
                  <NavLink to="/orders" className={({ isActive }) => `px-4 py-2 rounded-xl text-sm font-bold transition-all ${isActive ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-sm' : 'text-slate-500 hover:text-slate-900 dark:hover:text-white'}`}>
                    <span className="flex items-center gap-1.5"><Package size={14} /> Orders</span>
                  </NavLink>
                )}
                {(user.role === 'RESTAURANT_ADMIN' || user.role === 'ADMIN') && (
                  <NavLink to="/admin" className={({ isActive }) => `px-4 py-2 rounded-xl text-sm font-bold transition-all ${isActive ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-sm' : 'text-slate-500 hover:text-slate-900 dark:hover:text-white'}`}>
                    <span className="flex items-center gap-1.5"><LayoutDashboard size={14} /> Admin</span>
                  </NavLink>
                )}
              </nav>
            )}

            {/* Right side */}
            <div className="flex items-center gap-2">
              {user ? (
                <>
                  {/* Cart Button */}
                  {user.role === 'CUSTOMER' && (
                    <Link to="/cart" className="relative flex items-center gap-2 px-4 py-2 bg-slate-100 dark:bg-slate-800 rounded-2xl text-sm font-bold text-slate-700 dark:text-slate-200 hover:bg-primary/10 hover:text-primary transition-all group">
                      <ShoppingBag size={18} className="group-hover:scale-110 transition-transform" />
                      <span className="hidden sm:block">Cart</span>
                      {cartCount > 0 && (
                        <motion.span
                          key={cartCount}
                          initial={{ scale: 0.5 }}
                          animate={{ scale: 1 }}
                          className="inline-flex items-center justify-center h-5 min-w-5 px-1 bg-primary text-white text-[10px] font-black rounded-full"
                        >
                          {cartCount}
                        </motion.span>
                      )}
                    </Link>
                  )}

                  {/* User Dropdown */}
                  <div className="relative" ref={dropdownRef}>
                    <button
                      onClick={() => setDropdownOpen(v => !v)}
                      className="flex items-center gap-2.5 pl-2 pr-3 py-1.5 rounded-2xl hover:bg-slate-100 dark:hover:bg-slate-800 transition-all"
                    >
                      <div className="w-8 h-8 rounded-xl bg-primary text-white font-black flex items-center justify-center text-sm shadow-md shadow-primary/20">
                        {user.username?.charAt(0).toUpperCase()}
                      </div>
                      <span className="hidden sm:block text-sm font-bold text-slate-700 dark:text-slate-200 max-w-[80px] truncate">{user.username}</span>
                      <ChevronDown size={14} className={`text-slate-400 transition-transform ${dropdownOpen ? 'rotate-180' : ''}`} />
                    </button>

                    <AnimatePresence>
                      {dropdownOpen && (
                        <motion.div
                          initial={{ opacity: 0, y: 8, scale: 0.96 }}
                          animate={{ opacity: 1, y: 0, scale: 1 }}
                          exit={{ opacity: 0, y: 8, scale: 0.96 }}
                          transition={{ duration: 0.15 }}
                          className="absolute right-0 top-full mt-2 w-52 bg-white dark:bg-slate-900 border border-border rounded-2xl shadow-2xl shadow-black/10 overflow-hidden"
                        >
                          <div className="px-4 py-3 border-b border-border bg-slate-50 dark:bg-slate-950">
                            <p className="text-xs font-black text-slate-900 dark:text-white truncate">{user.username}</p>
                            <p className="text-[10px] font-bold text-slate-400 truncate">{user.email}</p>
                            <span className="text-[9px] font-black uppercase tracking-widest text-primary bg-primary/10 px-2 py-0.5 rounded-full mt-1 inline-block">{user.role}</span>
                          </div>
                          <div className="p-1.5">
                            <Link onClick={() => setDropdownOpen(false)} to="/profile" className="flex items-center gap-2.5 px-3 py-2.5 text-sm font-bold text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white hover:bg-slate-50 dark:hover:bg-slate-800 rounded-xl transition-all">
                              <User size={15} className="text-slate-400" /> My Profile
                            </Link>
                            {user.role === 'CUSTOMER' && (
                              <Link onClick={() => setDropdownOpen(false)} to="/orders" className="flex items-center gap-2.5 px-3 py-2.5 text-sm font-bold text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white hover:bg-slate-50 dark:hover:bg-slate-800 rounded-xl transition-all">
                                <Package size={15} className="text-slate-400" /> My Orders
                              </Link>
                            )}
                            {(user.role === 'RESTAURANT_ADMIN' || user.role === 'ADMIN') && (
                              <Link onClick={() => setDropdownOpen(false)} to="/admin" className="flex items-center gap-2.5 px-3 py-2.5 text-sm font-bold text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white hover:bg-slate-50 dark:hover:bg-slate-800 rounded-xl transition-all">
                                <LayoutDashboard size={15} className="text-slate-400" /> Admin Panel
                              </Link>
                            )}
                            <div className="h-px bg-border mx-1 my-1" />
                            <button onClick={handleLogout} className="w-full flex items-center gap-2.5 px-3 py-2.5 text-sm font-bold text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/20 rounded-xl transition-all">
                              <LogOut size={15} /> Sign Out
                            </button>
                          </div>
                        </motion.div>
                      )}
                    </AnimatePresence>
                  </div>
                </>
              ) : (
                <div className="flex items-center gap-2">
                  <Link to="/auth/login" className="text-sm font-bold text-slate-600 dark:text-slate-300 hover:text-primary transition-colors px-4 py-2">
                    Log in
                  </Link>
                  <Link to="/auth/register" className="text-sm font-black bg-primary text-white px-5 py-2.5 rounded-xl hover:bg-primary/90 shadow-md shadow-primary/25 transition-all hover:scale-105 active:scale-95">
                    Sign up free
                  </Link>
                </div>
              )}
            </div>
          </div>
        </div>
      </header>

      <main className="flex-1 w-full max-w-7xl mx-auto p-4 sm:p-6 lg:p-8 pt-8">
        <Outlet />
      </main>

      {/* Live Order Floating Banner (Swiggy-style) */}
      {user?.role === 'CUSTOMER' && <LiveOrderBanner />}

      <footer className="bg-white dark:bg-slate-950 border-t border-[#f0ece6] dark:border-border mt-auto">
        <div className="max-w-7xl mx-auto px-4 py-8">
          <div className="flex flex-col md:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-2">
              <div className="w-7 h-7 bg-primary rounded-lg flex items-center justify-center">
                <Flame size={14} className="text-white" />
              </div>
              <span className="font-black">
                <span className="text-slate-900 dark:text-white">Crave</span>
                <span className="text-primary">Rush</span>
              </span>
            </div>
            <p className="text-slate-400 text-sm font-medium">© {new Date().getFullYear()} CraveRush. Delivering happiness, one bite at a time.</p>
            <div className="flex gap-6 text-sm font-bold text-slate-500">
              <a href="#" className="hover:text-primary transition-colors">Privacy</a>
              <a href="#" className="hover:text-primary transition-colors">Terms</a>
              <a href="#" className="hover:text-primary transition-colors">Support</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}
