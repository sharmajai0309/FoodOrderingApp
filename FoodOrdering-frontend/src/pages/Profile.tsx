import { useAuthStore } from '../store/useAuthStore'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api/axios'
import { motion } from 'framer-motion'
import { Link, useNavigate } from 'react-router-dom'
import { Mail, Shield, MapPin, Package, Settings, Bell, ChevronRight, LogOut } from 'lucide-react'

export default function Profile() {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  const { data: orders } = useQuery({
    queryKey: ['orders', user?.id],
    queryFn: async () => {
      const res = await api.get('/v1/api/customer/order/user')
      return res.data.data || []
    },
    enabled: !!user,
  })

  const handleLogout = () => {
    logout()
    navigate('/auth/login')
  }

  const navItems = [
    { icon: Package, label: 'Order History', color: 'text-blue-500', action: () => navigate('/orders') },
    { icon: MapPin, label: 'Delivery Addresses', color: 'text-emerald-500', action: () => {} },
    { icon: Bell, label: 'Notifications', color: 'text-amber-500', action: () => {} },
    { icon: Settings, label: 'System Settings', color: 'text-slate-500', action: () => {} },
  ]

  return (
    <div className="max-w-6xl mx-auto space-y-12 pb-20">
      <header className="flex flex-col md:flex-row items-center gap-8 bg-white dark:bg-slate-950 p-10 rounded-[3rem] border border-border shadow-sm">
        <div className="relative">
           <div className="w-32 h-32 rounded-[2.5rem] bg-linear-to-br from-primary to-orange-600 flex items-center justify-center text-white text-5xl font-black shadow-2xl shadow-primary/30">
              {user?.username?.charAt(0).toUpperCase()}
           </div>
           <div className="absolute -bottom-2 -right-2 w-10 h-10 bg-white dark:bg-slate-900 border-4 border-white dark:border-slate-950 rounded-2xl flex items-center justify-center text-primary shadow-lg">
              <Shield size={18} />
           </div>
        </div>

        <div className="text-center md:text-left flex-1">
           <h1 className="text-4xl font-black text-slate-900 dark:text-white tracking-tight mb-2">{user?.username}</h1>
           <div className="flex flex-wrap justify-center md:justify-start gap-4">
              <div className="flex items-center gap-2 text-slate-500 font-bold bg-slate-50 dark:bg-slate-900 px-4 py-2 rounded-2xl border border-border">
                 <Mail size={16} className="text-primary" /> {user?.email}
              </div>
              <div className="flex items-center gap-2 text-slate-500 font-bold bg-slate-100 dark:bg-slate-900 px-4 py-2 rounded-2xl border border-border">
                 <Shield size={16} className="text-primary" /> {user?.role} Access
              </div>
           </div>
        </div>

        <button
          onClick={handleLogout}
          className="flex items-center gap-2 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 px-6 py-3 rounded-2xl font-black uppercase tracking-widest text-xs hover:bg-rose-50 hover:text-rose-600 dark:hover:bg-rose-950/20 dark:hover:text-rose-400 transition-all"
        >
          <LogOut size={16} /> Sign Out
        </button>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
         <div className="space-y-4">
            <h2 className="text-xs font-black uppercase tracking-[0.2em] text-slate-400 px-4">Preference Center</h2>
            <nav className="space-y-2">
               {navItems.map((item) => (
                 <button key={item.label} onClick={item.action} className="w-full flex items-center justify-between p-5 bg-white dark:bg-slate-950 border border-border rounded-4xl hover:shadow-xl hover:-translate-y-1 transition-all group">
                    <div className="flex items-center gap-4">
                       <div className={`p-3 rounded-2xl bg-slate-50 dark:bg-slate-900 group-hover:bg-primary/5 transition-colors`}>
                          <item.icon size={20} className={item.color} />
                       </div>
                       <span className="font-black text-slate-900 dark:text-white">{item.label}</span>
                    </div>
                    <ChevronRight size={18} className="text-slate-300 group-hover:text-primary transition-colors" />
                 </button>
               ))}
            </nav>
         </div>

         <div className="lg:col-span-2 space-y-8">
            {/* Recent Orders Preview */}
            <div className="bg-white dark:bg-slate-950 border border-border rounded-[3rem] p-8">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-black">Recent Orders</h3>
                <Link to="/orders" className="text-primary text-xs font-black uppercase tracking-widest hover:underline">
                  View All
                </Link>
              </div>
              {!orders || orders.length === 0 ? (
                <div className="bg-slate-50 dark:bg-slate-900/50 border-2 border-dashed border-border rounded-[2rem] p-10 text-center">
                   <div className="w-16 h-16 bg-white dark:bg-slate-900 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300 shadow-sm">
                      <Package size={32} />
                   </div>
                   <h3 className="text-xl font-black text-slate-900 dark:text-white mb-2">No active orders</h3>
                   <p className="text-slate-500 font-medium max-w-sm mx-auto mb-6">You haven't placed any orders yet!</p>
                   <Link to="/" className="bg-primary text-white px-8 py-3 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all inline-block">
                     Browse Restaurants
                   </Link>
                </div>
              ) : (
                <div className="space-y-3">
                  {orders.slice(0, 3).map((order: any) => (
                    <div key={order.id} className="flex items-center justify-between p-4 bg-slate-50 dark:bg-slate-900 rounded-2xl">
                      <div>
                        <p className="font-black text-slate-900 dark:text-white">Order #{order.id}</p>
                        <p className="text-xs text-slate-400 font-bold">{order.orderStatus}</p>
                      </div>
                      <p className="font-black text-primary">₹{order.totalAmount}</p>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Loyalty Status */}
            <div className="bg-white dark:bg-slate-950 border border-border rounded-[3rem] p-10 overflow-hidden relative">
               <div className="absolute top-0 right-0 p-10 opacity-[0.05] pointer-events-none">
                  <StarIcon size={120} className="text-primary fill-current" />
               </div>
               <h3 className="text-xl font-black uppercase tracking-tight mb-6 flex items-center gap-3">
                  <StarIcon className="text-primary" size={24} /> Elite Loyalty Status
               </h3>
               <div className="space-y-6">
                  <div className="flex justify-between items-end mb-2">
                     <span className="text-sm font-black text-slate-400 tracking-widest uppercase">Progress to Silver</span>
                     <span className="text-2xl font-black text-primary italic">{(orders?.length || 0) * 75} / 1000 pts</span>
                  </div>
                  <div className="h-4 w-full bg-slate-100 dark:bg-slate-900 rounded-full overflow-hidden p-1 shadow-inner border border-border/50">
                     <motion.div 
                        initial={{ width: 0 }}
                        animate={{ width: `${Math.min(((orders?.length || 0) * 75) / 10, 100)}%` }}
                        className="h-full bg-linear-to-r from-primary to-orange-500 rounded-full shadow-lg" 
                     />
                  </div>
                  <p className="text-xs font-bold text-slate-500 leading-relaxed italic">
                    Place more orders to earn points and unlock free deliveries and exclusive rewards!
                  </p>
               </div>
            </div>
         </div>
      </div>
    </div>
  )
}

function StarIcon({ size, className }: any) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
    </svg>
  )
}
