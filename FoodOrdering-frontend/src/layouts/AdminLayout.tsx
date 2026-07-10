import { Outlet, Navigate, Link, useLocation } from 'react-router-dom'
import { useAuthStore } from '../store/useAuthStore'
import { LayoutDashboard, Store, Users, ArrowLeft, LogOut, Bell, ShieldCheck, BarChart3, ScrollText } from 'lucide-react'
import { motion } from 'framer-motion'

export default function AdminLayout() {
  const { user, logout } = useAuthStore()
  const location = useLocation()

  if (!user || (user.role !== 'RESTAURANT_ADMIN' && user.role !== 'ADMIN')) {
    return <Navigate to="/" replace />
  }

  const navItems = [
    { name: 'Dashboard', path: '/admin/dashboard', icon: LayoutDashboard },
    { name: 'Analytics', path: '/admin/analytics', icon: BarChart3 },
    { name: 'Restaurants', path: '/admin/restaurants', icon: Store },
    { name: 'Users', path: '/admin/users', icon: Users },
    { name: 'Audit Logs', path: '/admin/audit', icon: ScrollText },
  ]

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#0b0f19] flex">
      {/* Sidebar */}
      <aside className="w-72 bg-white dark:bg-slate-950 border-r border-border hidden lg:flex flex-col shadow-2xl relative z-20">
        <div className="p-8 pb-10">
          <Link to="/" className="text-2xl font-black tracking-tighter text-primary flex items-center gap-3">
            <span className="bg-primary text-white w-9 h-9 flex items-center justify-center rounded-xl shadow-lg shadow-primary/30 font-black">F</span>
            Admin <span className="text-slate-400 font-medium">Hub</span>
          </Link>
        </div>
        
        <div className="flex-1 px-4 space-y-6">
          <div className="space-y-1">
            <p className="px-4 text-[10px] font-black uppercase tracking-[0.2em] text-slate-400 mb-4">Main Menu</p>
            {navItems.map((item) => {
              const isActive = location.pathname.startsWith(item.path)
              return (
                <Link
                  key={item.name}
                  to={item.path}
                  className={`group relative flex items-center px-4 py-3.5 text-sm font-bold rounded-2xl transition-all ${
                    isActive
                      ? 'text-primary bg-primary/5 shadow-inner'
                      : 'text-slate-500 hover:text-slate-900 dark:hover:text-white hover:bg-slate-50 dark:hover:bg-slate-900'
                  }`}
                >
                  <item.icon className={`mr-3.5 h-5 w-5 transition-transform group-hover:scale-110 ${isActive ? 'text-primary' : 'text-slate-400'}`} />
                  {item.name}
                  {isActive && (
                    <motion.div 
                       layoutId="sidebar-active"
                       className="absolute left-0 w-1.5 h-6 bg-primary rounded-r-full" 
                    />
                  )}
                </Link>
              )
            })}
          </div>

          <div className="pt-4 border-t border-slate-100 dark:border-slate-800 space-y-1">
             <p className="px-4 text-[10px] font-black uppercase tracking-[0.2em] text-slate-400 mb-4">Account</p>
             <Link to="/" className="flex items-center px-4 py-3.5 text-sm font-bold text-slate-500 hover:text-slate-900 dark:hover:text-white hover:bg-slate-50 dark:hover:bg-slate-900 rounded-2xl transition-all group">
               <ArrowLeft className="mr-3.5 h-5 w-5 text-slate-400 group-hover:-translate-x-1 transition-transform" /> Back to Store
             </Link>
             <button onClick={logout} className="w-full flex items-center px-4 py-3.5 text-sm font-bold text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950/20 rounded-2xl transition-all group">
               <LogOut className="mr-3.5 h-5 w-5 group-hover:translate-x-1 transition-transform" /> Secure Logout
             </button>
          </div>
        </div>

        <div className="p-6">
           <div className="bg-slate-50 dark:bg-slate-900 rounded-3xl p-5 border border-border">
              <div className="flex items-center gap-3 mb-3">
                 <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                    <ShieldCheck size={20} />
                 </div>
                 <div>
                    <h4 className="text-xs font-black uppercase tracking-wider text-slate-900 dark:text-white truncate max-w-[120px]">{user?.username}</h4>
                    <p className="text-[10px] font-bold text-slate-400 tracking-widest">{user?.role}</p>
                 </div>
              </div>
              <div className="h-2 w-full bg-slate-200 dark:bg-slate-800 rounded-full overflow-hidden">
                 <div className="w-3/4 h-full bg-primary rounded-full shadow-sm" />
              </div>
           </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col h-screen overflow-hidden">
        {/* Top Header */}
        <header className="h-20 bg-white/80 dark:bg-slate-950/80 backdrop-blur-xl border-b border-border px-8 flex items-center justify-between shrink-0 relative z-10">
           <div>
              <h2 className="text-sm font-black uppercase tracking-widest text-slate-400">Section Overview</h2>
              <p className="text-xl font-black text-slate-900 dark:text-white truncate">
                {navItems.find(i => location.pathname.startsWith(i.path))?.name || 'Administrative Console'}
              </p>
           </div>
           
           <div className="flex items-center gap-4">
              <button className="w-10 h-10 rounded-full bg-slate-100 dark:bg-slate-900 border border-border flex items-center justify-center text-slate-500 hover:text-primary transition-all">
                 <Bell size={18} />
              </button>
              <div className="w-px h-8 bg-border mx-2" />
              <div className="flex items-center gap-3">
                 <div className="w-10 h-10 rounded-2xl bg-primary text-white font-black flex items-center justify-center shadow-lg shadow-primary/20">
                    {user?.username?.charAt(0).toUpperCase()}
                 </div>
              </div>
           </div>
        </header>

        <main className="flex-1 overflow-y-auto p-8 custom-scrollbar">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
