import { useQuery } from '@tanstack/react-query'
import { useAuthStore } from '../../store/useAuthStore'
import { api } from '../../api/axios'
import { motion } from 'framer-motion'
import { CheckCircle2, AlertCircle, Clock, Package, Loader2, Navigation, Store } from 'lucide-react'

interface HistoryItem {
  id: number
  orderId: number
  restaurantName: string
  customerAddress: string
  deliveryFee: number
  status: string
  assignedAt: string
  pickedAt?: string
  deliveredAt?: string
}

const STATUS_CONFIG: Record<string, { label: string; color: string; bg: string; icon: typeof CheckCircle2 }> = {
  DELIVERED: { label: 'Delivered', color: '#10b981', bg: 'rgba(16,185,129,0.1)', icon: CheckCircle2 },
  CANCELLED: { label: 'Cancelled', color: '#f43f5e', bg: 'rgba(244,63,94,0.1)', icon: AlertCircle },
  ACCEPTED: { label: 'Active', color: '#f59e0b', bg: 'rgba(245,158,11,0.1)', icon: Clock },
  PICKED_UP: { label: 'Picked Up', color: '#3b82f6', bg: 'rgba(59,130,246,0.1)', icon: Navigation },
  PENDING: { label: 'Pending', color: '#94a3b8', bg: 'rgba(148,163,184,0.1)', icon: Clock },
}

export default function DeliveryHistory() {
  const user = useAuthStore((s) => s.user)

  const { data: history = [], isLoading } = useQuery<HistoryItem[]>({
    queryKey: ['history', user?.id],
    queryFn: async () => {
      const res = await api.get(`/api/delivery/driver/${user?.id}/history`)
      return res.data?.data ?? []
    },
    enabled: !!user?.id,
  })

  const formatTime = (ts?: string) => {
    if (!ts) return '—'
    return new Date(ts).toLocaleString('en-IN', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' })
  }

  const formatDuration = (start?: string, end?: string) => {
    if (!start || !end) return null
    const diff = Math.round((new Date(end).getTime() - new Date(start).getTime()) / 60000)
    return `${diff} min`
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-72">
        <Loader2 size={32} className="text-emerald-400 animate-spin" />
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto pb-8 space-y-6">
      <div>
        <h1 className="text-2xl font-black text-white tracking-tight">Delivery History</h1>
        <p className="text-slate-500 text-sm mt-1">{history.length} total deliveries</p>
      </div>

      {history.length === 0 ? (
        <div className="py-24 flex flex-col items-center justify-center rounded-3xl" style={{ background: 'var(--surface)', border: '1px dashed rgba(148,163,184,0.1)' }}>
          <Package size={40} className="text-slate-700 mb-4" />
          <p className="text-slate-400 font-bold">No delivery history yet</p>
          <p className="text-slate-600 text-sm mt-1">Accept your first order from the dashboard!</p>
        </div>
      ) : (
        <div className="space-y-3">
          {history.map((item, i) => {
            const cfg = STATUS_CONFIG[item.status] ?? STATUS_CONFIG.PENDING
            const duration = formatDuration(item.assignedAt, item.deliveredAt)
            return (
              <motion.div
                key={item.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.03 }}
                className="p-5 rounded-2xl"
                style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.07)' }}
              >
                <div className="flex items-start gap-4">
                  <div className="w-11 h-11 rounded-xl flex items-center justify-center shrink-0" style={{ background: cfg.bg }}>
                    <cfg.icon size={20} style={{ color: cfg.color }} />
                  </div>

                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-4">
                      <div className="min-w-0">
                        <p className="font-black text-white text-sm">Order #{item.orderId}</p>
                        <div className="flex items-center gap-1.5 mt-1 text-xs text-slate-500">
                          <Store size={11} />
                          <span className="truncate">{item.restaurantName || `Assignment #${item.id}`}</span>
                        </div>
                        <div className="flex items-center gap-1.5 mt-0.5 text-xs text-slate-600">
                          <Navigation size={11} />
                          <span className="truncate">{item.customerAddress}</span>
                        </div>
                      </div>
                      <div className="text-right shrink-0">
                        <p className="font-black" style={{ color: cfg.color }}>
                          {item.status === 'DELIVERED' ? `+₹${Number(item.deliveryFee ?? 30).toFixed(0)}` : cfg.label}
                        </p>
                        <span className="inline-block mt-1 text-[10px] font-black uppercase tracking-widest px-2 py-0.5 rounded-full" style={{ background: cfg.bg, color: cfg.color }}>
                          {cfg.label}
                        </span>
                      </div>
                    </div>

                    <div className="flex items-center gap-4 mt-3 pt-3" style={{ borderTop: '1px solid rgba(148,163,184,0.06)' }}>
                      <span className="text-xs text-slate-600 flex items-center gap-1">
                        <Clock size={11} /> {formatTime(item.assignedAt)}
                      </span>
                      {duration && (
                        <span className="text-xs text-slate-600 flex items-center gap-1">
                          🕐 {duration}
                        </span>
                      )}
                      {item.deliveredAt && (
                        <span className="text-xs text-emerald-600 flex items-center gap-1">
                          <CheckCircle2 size={11} /> {formatTime(item.deliveredAt)}
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </motion.div>
            )
          })}
        </div>
      )}
    </div>
  )
}
