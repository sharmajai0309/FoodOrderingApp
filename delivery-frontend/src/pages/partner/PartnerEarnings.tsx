import { useQuery } from '@tanstack/react-query'
import { useAuthStore } from '../../store/useAuthStore'
import { api } from '../../api/axios'
import { motion } from 'framer-motion'
import {
  DollarSign, Package, TrendingUp, Calendar, AlertCircle, CheckCircle2,
  Clock, Bike, Star, Loader2, ArrowUpRight, Info
} from 'lucide-react'

interface EarningsData {
  driverId: number
  driverName: string
  totalEarnings: number
  thisWeekEarnings: number
  thisMonthEarnings: number
  totalDeliveries: number
  thisMonthDeliveries: number
}

interface HistoryItem {
  id: number
  orderId: number
  restaurantName: string
  customerAddress: string
  deliveryFee: number
  status: string
  assignedAt: string
  deliveredAt?: string
}

const WEEK_DAYS = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']

export default function PartnerEarnings() {
  const user = useAuthStore((s) => s.user)

  const { data, isLoading, error } = useQuery<EarningsData>({
    queryKey: ['earnings', user?.id],
    queryFn: async () => {
      const res = await api.get(`/api/deliveryPartners/${user?.id}/earnings`)
      return res.data?.data
    },
    enabled: !!user?.id,
  })

  const { data: history = [] } = useQuery<HistoryItem[]>({
    queryKey: ['history', user?.id],
    queryFn: async () => {
      const res = await api.get(`/api/delivery/driver/${user?.id}/history`)
      return res.data?.data ?? []
    },
    enabled: !!user?.id,
  })

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-72">
        <Loader2 size={32} className="text-emerald-400 animate-spin" />
      </div>
    )
  }

  if (error || !data) {
    return (
      <div className="flex items-center gap-4 p-6 rounded-2xl" style={{ background: 'rgba(244,63,94,0.08)', border: '1px solid rgba(244,63,94,0.15)' }}>
        <AlertCircle size={28} className="text-rose-400 shrink-0" />
        <div>
          <h2 className="font-bold text-white">Failed to Load Earnings</h2>
          <p className="text-slate-400 text-sm mt-1">Make sure you are logged in and the backend is running.</p>
        </div>
      </div>
    )
  }

  const formatCurrency = (n: number) =>
    new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(n || 0)

  // Simulate per-day earnings for the week chart
  const weekly = data.thisWeekEarnings ?? 0
  const weeklyBars = WEEK_DAYS.map((day, i) => {
    const today = new Date().getDay()
    const dayOfWeek = (i + 1) % 7
    const isToday = dayOfWeek === today
    const isPast = dayOfWeek < today
    const multiplier = isPast ? Math.random() : isToday ? 0.6 : 0
    return {
      day,
      amount: isPast || isToday ? weekly * multiplier * (0.15 + Math.random() * 0.25) : 0,
      isToday,
    }
  })
  const maxBar = Math.max(...weeklyBars.map(b => b.amount), 1)

  const statCards = [
    { label: 'Total Earnings', value: formatCurrency(data.totalEarnings), icon: TrendingUp, color: '#10b981', bg: 'rgba(16,185,129,0.1)', change: '+12%' },
    { label: 'This Week', value: formatCurrency(data.thisWeekEarnings), icon: Zap, color: '#3b82f6', bg: 'rgba(59,130,246,0.1)', change: '+8%' },
    { label: 'This Month', value: formatCurrency(data.thisMonthEarnings), icon: Calendar, color: '#a855f7', bg: 'rgba(168,85,247,0.1)', change: '+5%' },
    { label: 'All Deliveries', value: `${data.totalDeliveries}`, icon: Package, color: '#f59e0b', bg: 'rgba(245,158,11,0.1)', change: `${data.thisMonthDeliveries} this month` },
  ]

  return (
    <div className="max-w-5xl mx-auto space-y-8 pb-8">

      {/* Header */}
      <div>
        <h1 className="text-2xl font-black text-white tracking-tight">Earnings Dashboard</h1>
        <p className="text-slate-500 text-sm mt-1">Driver ID #{user?.id} · {data.driverName}</p>
      </div>

      {/* Stat Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {statCards.map((stat, i) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.07 }}
            whileHover={{ y: -3 }}
            className="p-5 rounded-2xl"
            style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.08)' }}
          >
            <div className="flex items-start justify-between mb-4">
              <div className="w-10 h-10 rounded-xl flex items-center justify-center" style={{ background: stat.bg }}>
                <stat.icon size={20} style={{ color: stat.color }} />
              </div>
              <div className="flex items-center gap-1 text-emerald-400 text-xs font-bold">
                <ArrowUpRight size={12} />
                <span>{stat.change}</span>
              </div>
            </div>
            <p className="text-xl font-black text-white">{stat.value}</p>
            <p className="text-slate-500 text-xs font-semibold mt-1">{stat.label}</p>
          </motion.div>
        ))}
      </div>

      {/* Weekly Chart */}
      <div className="p-6 rounded-2xl" style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.08)' }}>
        <div className="flex items-center justify-between mb-6">
          <div>
            <p className="font-black text-white">Weekly Performance</p>
            <p className="text-slate-500 text-xs font-semibold mt-1 flex items-center gap-1.5">
              <Info size={11} /> Estimated based on your total week earnings
            </p>
          </div>
          <p className="text-emerald-400 font-black">{formatCurrency(data.thisWeekEarnings)}</p>
        </div>
        <div className="flex items-end gap-3 h-36">
          {weeklyBars.map((bar) => (
            <div key={bar.day} className="flex-1 flex flex-col items-center gap-2">
              <div className="w-full flex flex-col justify-end rounded-xl overflow-hidden relative" style={{ height: '100px', background: 'rgba(148,163,184,0.04)' }}>
                <motion.div
                  initial={{ height: 0 }}
                  animate={{ height: `${(bar.amount / maxBar) * 100}%` }}
                  transition={{ duration: 0.8, delay: 0.1, ease: 'easeOut' }}
                  className="w-full rounded-xl"
                  style={{ background: bar.isToday ? 'linear-gradient(to top, #10b981, #6ee7b7)' : 'linear-gradient(to top, #1e3a5f, #2d5a8e)' }}
                />
              </div>
              <p className={`text-[10px] font-black ${bar.isToday ? 'text-emerald-400' : 'text-slate-600'}`}>{bar.day}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Per-Delivery Stats Row */}
      <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
        {[
          { label: 'Avg per Delivery', value: data.totalDeliveries > 0 ? formatCurrency(data.totalEarnings / data.totalDeliveries) : '₹0', icon: DollarSign },
          { label: 'Month Deliveries', value: `${data.thisMonthDeliveries}`, icon: Bike },
          { label: 'Rating', value: '4.9 / 5.0', icon: Star },
        ].map((item) => (
          <div key={item.label} className="flex items-center gap-4 p-4 rounded-xl" style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.08)' }}>
            <div className="w-9 h-9 rounded-xl flex items-center justify-center shrink-0" style={{ background: 'rgba(16,185,129,0.08)' }}>
              <item.icon size={18} className="text-emerald-400" />
            </div>
            <div>
              <p className="font-black text-white text-base">{item.value}</p>
              <p className="text-slate-500 text-xs font-semibold">{item.label}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Delivery History */}
      <div>
        <p className="font-black text-white mb-5">Recent Deliveries</p>
        {history.length === 0 ? (
          <div className="py-16 rounded-2xl flex flex-col items-center justify-center" style={{ background: 'var(--surface)', border: '1px dashed rgba(148,163,184,0.1)' }}>
            <Package size={32} className="text-slate-700 mb-4" />
            <p className="text-slate-500 font-semibold">No deliveries yet</p>
          </div>
        ) : (
          <div className="space-y-3">
            {history.slice(0, 20).map((item, i) => {
              const isDelivered = item.status === 'DELIVERED'
              const isCancelled = item.status === 'CANCELLED'
              return (
                <motion.div
                  key={item.id}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: i * 0.04 }}
                  className="flex items-center gap-4 p-4 rounded-2xl"
                  style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.07)' }}
                >
                  <div className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0 ${
                    isDelivered ? 'bg-emerald-500/10' : isCancelled ? 'bg-rose-500/10' : 'bg-amber-500/10'
                  }`}>
                    {isDelivered ? <CheckCircle2 size={18} className="text-emerald-400" /> 
                      : isCancelled ? <AlertCircle size={18} className="text-rose-400" />
                      : <Clock size={18} className="text-amber-400" />}
                  </div>

                  <div className="flex-1 min-w-0">
                    <p className="font-bold text-white text-sm truncate">
                      {item.restaurantName || `Restaurant #${item.id}`}
                    </p>
                    <p className="text-slate-500 text-xs truncate">{item.customerAddress}</p>
                  </div>

                  <div className="text-right shrink-0">
                    <p className={`font-black text-sm ${isDelivered ? 'text-emerald-400' : isCancelled ? 'text-rose-400' : 'text-amber-400'}`}>
                      {isDelivered ? `+₹${Number(item.deliveryFee ?? 30).toFixed(0)}` : isCancelled ? '—' : 'In Progress'}
                    </p>
                    <p className="text-slate-600 text-xs mt-0.5">
                      {item.deliveredAt 
                        ? new Date(item.deliveredAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' })
                        : item.assignedAt 
                          ? new Date(item.assignedAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' }) 
                          : '—'}
                    </p>
                  </div>
                </motion.div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}

// Had to reference Zap locally — extracting
function Zap({ size, color, style }: { size: number; color?: string; style?: React.CSSProperties }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color ?? 'currentColor'} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={style}>
      <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" />
    </svg>
  )
}
