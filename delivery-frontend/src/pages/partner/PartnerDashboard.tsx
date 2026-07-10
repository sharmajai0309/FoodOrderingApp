import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { api } from '../../api/axios'
import { motion, AnimatePresence } from 'framer-motion'
import { MapPin, Clock, Zap, Package, ChevronRight, Star, TrendingUp, AlertCircle, Power, Wifi, WifiOff } from 'lucide-react'
import { useAuthStore } from '../../store/useAuthStore'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { useEffect, useRef, useState } from 'react'

interface OpenAssignment {
  id: number; orderId: number; restaurantId: number; restaurantName: string
  customerAddress: string; deliveryFee: number; status: string; assignedAt: string
}
interface EarningsSummary {
  totalEarnings: number; thisWeekEarnings: number; totalDeliveries: number; driverName: string
}
interface Availability { available: boolean }

const SIMULATE_DISTANCE = () => `${(Math.random() * 3 + 0.5).toFixed(1)} km`
const SIMULATE_TIME = () => `${Math.floor(Math.random() * 12 + 3)} min`
const PING_SOUND_URL = 'data:audio/wav;base64,UklGRlYAAABXQVZFZm10IBAAAAABAAEARKwAAIhYAQACABAAZGF0YTIAAAB/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/'
function playPing() {
  try { const a = new Audio(PING_SOUND_URL); a.volume = 0.4; a.play().catch(() => {}) } catch (_) {}
}

export default function PartnerDashboard() {
  const user = useAuthStore((s) => s.user)
  const queryClient = useQueryClient()
  const navigate = useNavigate()
  const prevCountRef = useRef<number | null>(null)
  const [flashId, setFlashId] = useState<number | null>(null)

  const { data: availability } = useQuery<Availability>({
    queryKey: ['availability', user?.id],
    queryFn: async () => (await api.get(`/api/drivers/${user?.id}/availability`)).data?.data,
    enabled: !!user?.id,
  })
  const isOnline = availability?.available ?? false

  const toggleMutation = useMutation({
    mutationFn: async () => (await api.post(`/api/drivers/${user?.id}/availability/toggle`, {})).data?.data,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['availability'] })
      if (data?.available) toast.success('You are now ONLINE 🟢', { description: 'You will receive new delivery requests.' })
      else toast('You are now OFFLINE', { description: "You won't receive new orders until you go online." })
    },
    onError: () => toast.error('Failed to toggle availability'),
  })

  const { data: openOrders = [], isLoading: ordersLoading } = useQuery<OpenAssignment[]>({
    queryKey: ['open-assignments'],
    queryFn: async () => (await api.get('/api/delivery/open')).data?.data ?? [],
    refetchInterval: isOnline ? 8000 : false,
    enabled: isOnline,
  })

  useEffect(() => {
    if (prevCountRef.current !== null && openOrders.length > prevCountRef.current) {
      playPing()
      const newOrder = openOrders[0]
      if (newOrder) { setFlashId(newOrder.id); setTimeout(() => setFlashId(null), 3000) }
      toast('🔔 New Order Available!', { description: `₹${newOrder?.deliveryFee ?? 30} · ${newOrder?.restaurantName}`, duration: 8000 })
    }
    prevCountRef.current = openOrders.length
  }, [openOrders])

  useEffect(() => { if (Notification.permission === 'default') Notification.requestPermission() }, [])

  const { data: earnings } = useQuery<EarningsSummary>({
    queryKey: ['earnings', user?.id],
    queryFn: async () => (await api.get(`/api/deliveryPartners/${user?.id}/earnings`)).data?.data,
    enabled: !!user?.id,
  })

  const acceptMutation = useMutation({
    mutationFn: async (assignmentId: number) => (await api.post(`/api/delivery/${assignmentId}/accept?driverId=${user?.id}`)).data?.data,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['open-assignments'] })
      queryClient.invalidateQueries({ queryKey: ['active-assignment'] })
      toast.success('Order accepted! 🚀 Head to the restaurant.', { duration: 4000 })
      setTimeout(() => navigate('/active'), 500)
    },
    onError: (err: any) => toast.error(err?.response?.data?.message || 'Failed to accept order'),
  })

  const todayEarnings = earnings?.thisWeekEarnings ? earnings.thisWeekEarnings / 7 : 0

  return (
    <div style={{ maxWidth: 900, margin: '0 auto', display: 'flex', flexDirection: 'column', gap: 24, paddingBottom: 32 }}>

      {/* ── Hero: Online/Offline toggle ─────────────────────── */}
      <motion.div
        initial={{ opacity: 0, y: -8 }} animate={{ opacity: 1, y: 0 }}
        style={{
          borderRadius: 20,
          padding: '32px 32px',
          position: 'relative',
          overflow: 'hidden',
          background: isOnline
            ? 'linear-gradient(135deg, #0c1f3a 0%, #0e2445 60%, #0f2952 100%)'
            : 'linear-gradient(135deg, #0c1424 0%, #0a1020 100%)',
          border: isOnline ? '1px solid rgba(59,130,246,0.25)' : '1px solid var(--border)',
          transition: 'all 0.5s ease',
        }}
      >
        {/* Glow orb */}
        <div style={{ position: 'absolute', top: -30, right: -30, width: 220, height: 220, borderRadius: '50%', background: `radial-gradient(circle, ${isOnline ? 'rgba(59,130,246,0.18)' : 'rgba(99,130,160,0.06)'}, transparent)`, pointerEvents: 'none' }} />

        <div style={{ position: 'relative', zIndex: 1, display: 'flex', flexWrap: 'wrap', alignItems: 'center', justifyContent: 'space-between', gap: 24 }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
              {isOnline ? <Wifi size={14} color="var(--blue-bright)" /> : <WifiOff size={14} color="var(--text-muted)" />}
              <span style={{ color: isOnline ? 'var(--blue-bright)' : 'var(--text-muted)', fontSize: 12, fontWeight: 700, letterSpacing: '0.08em' }}>
                {isOnline ? 'Online — Receiving orders' : 'Offline — Orders paused'}
              </span>
            </div>
            <h1 style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 28, letterSpacing: '-0.03em', margin: 0 }}>
              Hey, {user?.username?.split(' ')[0]} {isOnline ? '👋' : '😴'}
            </h1>
            <p style={{ color: isOnline ? 'var(--text-secondary)' : 'var(--text-muted)', fontSize: 14, marginTop: 4 }}>
              {isOnline ? "You're online and ready for deliveries." : 'Toggle online to start accepting orders.'}
            </p>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 20 }}>
            <div style={{ textAlign: 'center' }}>
              <p style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 24, margin: 0 }}>₹{todayEarnings.toFixed(0)}</p>
              <p style={{ color: 'var(--text-muted)', fontSize: 11, fontWeight: 600, marginTop: 2 }}>Today's Est.</p>
            </div>
            <div style={{ width: 1, height: 36, background: 'var(--border)' }} />
            <motion.button
              whileTap={{ scale: 0.95 }}
              onClick={() => toggleMutation.mutate()}
              disabled={toggleMutation.isPending}
              style={{
                display: 'flex', alignItems: 'center', gap: 8, padding: '11px 20px',
                borderRadius: 12, fontWeight: 800, fontSize: 13, cursor: 'pointer', border: 'none',
                background: isOnline ? 'rgba(244,63,94,0.12)' : 'linear-gradient(135deg, #3b82f6, #2563eb)',
                color: isOnline ? 'var(--rose)' : 'white',
                boxShadow: isOnline ? 'none' : '0 4px 20px rgba(59,130,246,0.3)',
                transition: 'all 0.3s',
              }}
            >
              {toggleMutation.isPending
                ? <span className="w-4 h-4 border-2 rounded-full animate-spin" style={{ borderColor: 'rgba(255,255,255,0.3)', borderTopColor: 'currentColor' }} />
                : <Power size={15} />}
              {isOnline ? 'Go Offline' : 'Go Online'}
            </motion.button>
          </div>
        </div>
      </motion.div>

      {/* ── Stats ──────────────────────────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: 14 }}>
        {[
          { label: 'Total Earned',     value: `₹${(earnings?.totalEarnings ?? 0).toFixed(0)}`,   icon: TrendingUp, color: 'var(--blue-bright)',  bg: 'var(--blue-soft)' },
          { label: 'This Week',        value: `₹${(earnings?.thisWeekEarnings ?? 0).toFixed(0)}`, icon: Zap,        color: 'var(--cyan)',         bg: 'var(--cyan-soft)' },
          { label: 'Total Deliveries', value: `${earnings?.totalDeliveries ?? 0}`,                icon: Package,    color: 'var(--purple)',       bg: 'var(--purple-soft)' },
          { label: 'Rating',           value: '4.9 ⭐',                                            icon: Star,       color: 'var(--amber)',        bg: 'var(--amber-soft)' },
        ].map((stat, i) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.07 }}
            className="card card-hover"
            style={{ padding: '20px 22px' }}
          >
            <div style={{ width: 36, height: 36, borderRadius: 10, display: 'flex', alignItems: 'center', justifyContent: 'center', background: stat.bg, marginBottom: 14 }}>
              <stat.icon size={17} color={stat.color} />
            </div>
            <p style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 22, margin: 0 }}>{stat.value}</p>
            <p style={{ color: 'var(--text-muted)', fontSize: 12, fontWeight: 600, marginTop: 4 }}>{stat.label}</p>
          </motion.div>
        ))}
      </div>

      {/* ── Available Orders ────────────────────────────────── */}
      <div>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
          <div>
            <h2 style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 18, margin: 0 }}>Available Orders</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: 13, marginTop: 3 }}>
              {isOnline ? 'Tap to accept and start earning' : 'Go online to see available orders'}
            </p>
          </div>
          {!ordersLoading && openOrders.length > 0 && (
            <div className="badge badge-blue">
              <span style={{ width: 6, height: 6, borderRadius: '50%', background: 'var(--blue)', animation: 'pulse 2s infinite' }} />
              {openOrders.length} available
            </div>
          )}
        </div>

        {/* Offline state */}
        {!isOnline && (
          <div className="card" style={{ padding: 48, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', borderStyle: 'dashed' }}>
            <div style={{ width: 56, height: 56, borderRadius: 16, background: 'rgba(99,130,160,0.06)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 16 }}>
              <WifiOff size={26} color="var(--text-muted)" />
            </div>
            <h3 style={{ color: 'var(--text-primary)', fontWeight: 800, fontSize: 17, margin: 0 }}>You're Offline</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: 13, textAlign: 'center', maxWidth: 280, marginTop: 6 }}>Tap "Go Online" above to start receiving delivery requests.</p>
            <motion.button whileTap={{ scale: 0.97 }} onClick={() => toggleMutation.mutate()}
              className="btn-primary" style={{ marginTop: 20 }}>
              <Wifi size={15} /> Go Online Now
            </motion.button>
          </div>
        )}

        {/* Loading skeletons */}
        {isOnline && ordersLoading && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            {[1, 2].map(i => <div key={i} className="skeleton" style={{ height: 160 }} />)}
          </div>
        )}

        {/* Orders list */}
        {isOnline && !ordersLoading && (
          <AnimatePresence mode="popLayout">
            {openOrders.length === 0 ? (
              <motion.div key="empty" initial={{ opacity: 0 }} animate={{ opacity: 1 }}
                className="card" style={{ padding: 48, display: 'flex', flexDirection: 'column', alignItems: 'center', borderStyle: 'dashed' }}>
                <div style={{ width: 56, height: 56, borderRadius: 16, background: 'rgba(99,130,160,0.06)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 16 }}>
                  <MapPin size={26} color="var(--text-muted)" />
                </div>
                <h3 style={{ color: 'var(--text-primary)', fontWeight: 800, fontSize: 17, margin: 0 }}>All Quiet</h3>
                <p style={{ color: 'var(--text-muted)', fontSize: 13, textAlign: 'center', maxWidth: 260, marginTop: 6 }}>No new orders right now. Stay online — the next one is coming!</p>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginTop: 14, color: 'var(--text-muted)', fontSize: 12 }}>
                  <AlertCircle size={12} /> Auto-refreshing every 8s
                </div>
              </motion.div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                {openOrders.map((order, i) => {
                  const dist = SIMULATE_DISTANCE()
                  const time = SIMULATE_TIME()
                  const isNew = order.id === flashId
                  return (
                    <motion.div
                      key={order.id}
                      initial={{ opacity: 0, y: 16 }}
                      animate={{
                        opacity: 1, y: 0,
                        boxShadow: isNew ? ['0 0 0 0 rgba(59,130,246,0)', '0 0 0 10px rgba(59,130,246,0.2)', '0 0 0 0 rgba(59,130,246,0)'] : 'none'
                      }}
                      exit={{ opacity: 0, scale: 0.97 }}
                      transition={{ delay: i * 0.06 }}
                      className="card"
                      style={{ overflow: 'hidden', border: isNew ? '1px solid rgba(59,130,246,0.4)' : '1px solid var(--border)' }}
                    >
                      {/* Card header */}
                      <div style={{ padding: '14px 20px 12px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderBottom: '1px solid var(--border)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                          {isNew && <span style={{ width: 7, height: 7, borderRadius: '50%', background: 'var(--blue)', animation: 'ping 1s infinite' }} />}
                          <span className={`badge ${isNew ? 'badge-blue' : 'badge-amber'}`}>
                            <span style={{ width: 5, height: 5, borderRadius: '50%', background: 'currentColor' }} />
                            {isNew ? 'New! 🔔' : 'NEW ORDER'}
                          </span>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
                          <span style={{ display: 'flex', alignItems: 'center', gap: 4, color: 'var(--text-muted)', fontSize: 12 }}><Clock size={11} /> {time}</span>
                          <span style={{ display: 'flex', alignItems: 'center', gap: 4, color: 'var(--text-muted)', fontSize: 12 }}><MapPin size={11} /> {dist}</span>
                          <span style={{ color: 'var(--blue-bright)', fontWeight: 900, fontSize: 18 }}>₹{order.deliveryFee ?? 30}</span>
                        </div>
                      </div>

                      {/* Route */}
                      <div style={{ padding: '16px 20px' }}>
                        <div style={{ position: 'relative', paddingLeft: 28 }}>
                          <div style={{ position: 'absolute', left: 9, top: 20, bottom: 20, width: 1.5, background: 'linear-gradient(to bottom, var(--border-strong), var(--blue-soft))' }} />
                          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                            <div style={{ display: 'flex', alignItems: 'flex-start', gap: 10 }}>
                              <div style={{ width: 18, height: 18, borderRadius: '50%', border: '2px solid var(--border-strong)', background: 'var(--surface-2)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginTop: 2, flexShrink: 0 }}>
                                <div style={{ width: 5, height: 5, borderRadius: '50%', background: 'var(--text-secondary)' }} />
                              </div>
                              <div>
                                <p style={{ color: 'var(--text-muted)', fontSize: 10, fontWeight: 800, letterSpacing: '0.14em', textTransform: 'uppercase', marginBottom: 2 }}>Pickup</p>
                                <p style={{ color: 'var(--text-primary)', fontWeight: 700, fontSize: 14, margin: 0 }}>{order.restaurantName || `Restaurant #${order.restaurantId}`}</p>
                              </div>
                            </div>
                            <div style={{ display: 'flex', alignItems: 'flex-start', gap: 10 }}>
                              <div style={{ width: 18, height: 18, borderRadius: '50%', border: '2px solid var(--blue)', background: 'var(--blue-soft)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginTop: 2, flexShrink: 0 }}>
                                <MapPin size={9} color="var(--blue-bright)" />
                              </div>
                              <div>
                                <p style={{ color: 'var(--text-muted)', fontSize: 10, fontWeight: 800, letterSpacing: '0.14em', textTransform: 'uppercase', marginBottom: 2 }}>Dropoff</p>
                                <p style={{ color: 'var(--text-primary)', fontWeight: 600, fontSize: 14, margin: 0 }}>{order.customerAddress}</p>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>

                      {/* CTA */}
                      <div style={{ padding: '0 20px 20px' }}>
                        <motion.button
                          whileTap={{ scale: 0.98 }}
                          onClick={() => acceptMutation.mutate(order.id)}
                          disabled={acceptMutation.isPending}
                          className="btn-success"
                          style={{ width: '100%' }}
                        >
                          {acceptMutation.isPending
                            ? <span className="w-4 h-4 border-2 rounded-full animate-spin" style={{ borderColor: 'rgba(255,255,255,0.3)', borderTopColor: '#fff' }} />
                            : <><Zap size={16} fill="currentColor" /> Accept &amp; Deliver <ChevronRight size={16} /></>}
                        </motion.button>
                      </div>
                    </motion.div>
                  )
                })}
              </div>
            )}
          </AnimatePresence>
        )}
      </div>
    </div>
  )
}
