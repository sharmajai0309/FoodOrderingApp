import { useQuery } from '@tanstack/react-query'
import { api } from '../../api/axios'
import { Users as UsersIcon, Building2, Utensils, TrendingUp, ArrowUpRight, Activity, Clock, XCircle, CheckCircle2 } from 'lucide-react'
import { motion } from 'framer-motion'
import type { LucideIcon } from 'lucide-react'

interface StatCardProps {
  title: string
  value: string | number
  icon: LucideIcon
  trend?: string
  color: string
  loading?: boolean
}

const StatCard = ({ title, value, icon: Icon, trend, color, loading }: StatCardProps) => (
  <motion.div 
    whileHover={{ y: -5 }}
    className="relative overflow-hidden p-6 bg-white dark:bg-slate-950 rounded-4xl border border-border shadow-sm group transition-all hover:shadow-2xl hover:shadow-primary/5"
  >
    <div className={`absolute top-0 right-0 w-32 h-32 -mr-8 -mt-8 rounded-full opacity-[0.03] group-hover:opacity-[0.08] transition-opacity ${color}`} />
    
    <div className="flex justify-between items-start mb-4">
      <div className={`p-3 rounded-2xl ${color} bg-opacity-10 text-opacity-100 flex items-center justify-center`}>
        <Icon size={24} className={color.replace('bg-', 'text-')} />
      </div>
      {trend && (
        <span className="flex items-center gap-1 text-[10px] font-black uppercase tracking-widest text-emerald-500 bg-emerald-500/10 px-2.5 py-1 rounded-full border border-emerald-500/20">
          <TrendingUp size={10} /> {trend}
        </span>
      )}
    </div>
    
    <div className="space-y-1">
      <p className="text-xs font-black uppercase tracking-widest text-slate-400">{title}</p>
      <div className="flex items-baseline gap-2">
         <h3 className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter">
           {loading ? <span className="animate-pulse text-slate-300">···</span> : value}
         </h3>
      </div>
    </div>
  </motion.div>
)

const statusConfig: Record<string, { color: string; bg: string; icon: typeof CheckCircle2 }> = {
  PENDING:           { color: 'text-amber-500',  bg: 'bg-amber-500/10',    icon: Clock },
  PAID:              { color: 'text-emerald-500', bg: 'bg-emerald-500/10',  icon: CheckCircle2 },
  CONFIRMED:         { color: 'text-blue-500',    bg: 'bg-blue-500/10',     icon: CheckCircle2 },
  OUT_FOR_DELIVERY:  { color: 'text-purple-500',  bg: 'bg-purple-500/10',   icon: Activity },
  DELIVERED:         { color: 'text-emerald-500', bg: 'bg-emerald-500/10',  icon: CheckCircle2 },
  CANCELLED:         { color: 'text-rose-500',    bg: 'bg-rose-500/10',     icon: XCircle },
}

export default function Dashboard() {
  const { data: restaurants, isLoading: restaurantsLoading } = useQuery({
    queryKey: ['admin-restaurants'],
    queryFn: async () => {
      const res = await api.get('/api/admin/restaurants/restaurant')
      return res.data
    },
  })

  const { data: users, isLoading: usersLoading } = useQuery({
    queryKey: ['admin-users'],
    queryFn: async () => {
      const res = await api.get('/v1/user/GetAll')
      return res.data
    },
  })

  const totalUsers = users?.length || 0
  const totalRestaurants = restaurants?.length || 0

  return (
    <div className="space-y-10">
      <div className="flex items-center justify-between">
         <div>
            <h1 className="text-4xl font-black text-slate-900 dark:text-white tracking-tight">Ecosystem Stats</h1>
            <p className="text-slate-500 font-medium">Real-time performance and user metrics.</p>
         </div>
         <button className="flex items-center gap-2 bg-white dark:bg-slate-900 border border-border px-6 py-3 rounded-2xl text-sm font-bold shadow-sm hover:shadow-md transition-all">
            <Activity size={18} className="text-primary" /> Refresh
         </button>
      </div>
      
      <div className="grid gap-8 md:grid-cols-2 xl:grid-cols-4">
        <StatCard 
          title="Total Users" 
          value={totalUsers}
          icon={UsersIcon} 
          color="bg-blue-500" 
          loading={usersLoading}
        />
        <StatCard 
          title="Active Restaurants" 
          value={totalRestaurants}
          icon={Building2} 
          color="bg-emerald-500" 
          loading={restaurantsLoading}
        />
        <StatCard 
          title="Customers" 
          value={users?.filter((u: any) => u.role === 'CUSTOMER').length || 0}
          icon={UsersIcon} 
          color="bg-primary" 
          loading={usersLoading}
        />
        <StatCard 
          title="Restaurant Admins" 
          value={users?.filter((u: any) => u.role === 'RESTAURANT_ADMIN').length || 0}
          icon={Utensils}
          color="bg-purple-500" 
          loading={usersLoading}
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Restaurants List */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-950 border border-border rounded-4xl p-8 shadow-sm">
          <div className="flex items-center justify-between mb-8">
             <h2 className="text-xl font-black uppercase tracking-tight">Active Restaurants</h2>
             <button className="text-primary text-xs font-black uppercase tracking-[0.2em] flex items-center gap-1 hover:gap-2 transition-all">
               View All <ArrowUpRight size={14} />
             </button>
          </div>
          {restaurantsLoading ? (
            <div className="space-y-4">
              {[1,2,3].map(i => (
                <div key={i} className="h-16 bg-slate-100 dark:bg-slate-900 rounded-2xl animate-pulse" />
              ))}
            </div>
          ) : !restaurants || restaurants.length === 0 ? (
            <div className="h-40 flex items-center justify-center text-slate-400 font-bold border-2 border-dashed border-slate-100 dark:border-slate-800 rounded-3xl">
              No restaurants onboarded yet.
            </div>
          ) : (
            <div className="space-y-3">
              {restaurants.slice(0, 6).map((r: any) => (
                <div key={r.id} className="flex items-center gap-4 p-4 rounded-2xl hover:bg-slate-50 dark:hover:bg-slate-900 transition-colors">
                  <div className="w-10 h-10 rounded-xl bg-slate-100 dark:bg-slate-800 overflow-hidden shrink-0 flex items-center justify-center text-xl">
                    {r.images?.[0] ? (
                      <img src={r.images[0]} alt="" className="w-full h-full object-cover" />
                    ) : '🍽️'}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-black text-slate-900 dark:text-white truncate">{r.title || r.name}</p>
                    <p className="text-xs text-slate-400 font-bold truncate">{r.description || 'No description'}</p>
                  </div>
                  <span className="text-[10px] font-black uppercase tracking-widest text-emerald-500 bg-emerald-500/10 px-2.5 py-1 rounded-full border border-emerald-500/20 shrink-0">
                    Active
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* User Breakdown */}
        <div className="bg-white dark:bg-slate-950 border border-border rounded-4xl p-8 shadow-sm">
           <h2 className="text-xl font-black uppercase tracking-tight mb-8">User Breakdown</h2>
           {usersLoading ? (
             <div className="space-y-4">
               {[1,2,3].map(i => <div key={i} className="h-14 bg-slate-100 dark:bg-slate-900 rounded-2xl animate-pulse" />)}
             </div>
           ) : (
             <div className="space-y-4">
               {[
                 { label: 'Customers', role: 'CUSTOMER', color: 'bg-blue-500' },
                 { label: 'Restaurant Admins', role: 'RESTAURANT_ADMIN', color: 'bg-primary' },
                 { label: 'Platform Admins', role: 'ADMIN', color: 'bg-rose-500' },
               ].map(({ label, role, color }) => {
                 const count = users?.filter((u: any) => u.role === role).length || 0
                 const pct = totalUsers ? Math.round((count / totalUsers) * 100) : 0
                 return (
                   <div key={role} className="space-y-2">
                     <div className="flex justify-between items-center">
                       <span className="text-xs font-black text-slate-500 uppercase tracking-widest">{label}</span>
                       <span className="text-sm font-black text-slate-900 dark:text-white">{count}</span>
                     </div>
                     <div className="h-2 bg-slate-100 dark:bg-slate-900 rounded-full overflow-hidden">
                       <motion.div
                         initial={{ width: 0 }}
                         animate={{ width: `${pct}%` }}
                         className={`h-full ${color} rounded-full`}
                       />
                     </div>
                   </div>
                 )
               })}
             </div>
           )}
        </div>
      </div>
    </div>
  )
}
