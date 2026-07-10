import { useQuery } from '@tanstack/react-query'
import { useAuthStore } from '../../store/useAuthStore'
import { api } from '../../api/axios'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { User, Phone, Bike, Shield, LogOut, Star, Package, DollarSign, ChevronRight, Loader2 } from 'lucide-react'
import { toast } from 'sonner'

const VEHICLE_LABELS: Record<string, string> = {
  BIKE: '🏍️ Bike',
  ELECTRIC_BIKE: '⚡ Electric Bike',
  SCOOTER: '🛵 Scooter',
  BICYCLE: '🚲 Bicycle',
  WALKING_DELIVERY: '🚶 Walking',
}

export default function PartnerProfile() {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  const { data: profile, isLoading } = useQuery({
    queryKey: ['profile', user?.id],
    queryFn: async () => {
      const res = await api.get(`/api/deliveryPartners/${user?.id}`)
      return res.data?.data
    },
    enabled: !!user?.id,
  })

  const { data: earnings } = useQuery({
    queryKey: ['earnings', user?.id],
    queryFn: async () => {
      const res = await api.get(`/api/deliveryPartners/${user?.id}/earnings`)
      return res.data?.data
    },
    enabled: !!user?.id,
  })

  const handleLogout = () => {
    logout()
    toast.success('Signed out successfully.')
    navigate('/login')
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-72">
        <Loader2 size={32} className="text-emerald-400 animate-spin" />
      </div>
    )
  }

  const statusColor = profile?.status === 'ACTIVE' ? '#10b981' : '#f59e0b'
  const statusBg = profile?.status === 'ACTIVE' ? 'rgba(16,185,129,0.1)' : 'rgba(245,158,11,0.1)'

  return (
    <div className="max-w-2xl mx-auto pb-8 space-y-6">

      {/* Profile Card */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="relative overflow-hidden rounded-3xl p-8 text-center"
        style={{ background: 'linear-gradient(135deg, #0f2a1e, #0c1a2e)', border: '1px solid rgba(16,185,129,0.2)' }}
      >
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-64 h-32 opacity-20 rounded-full" style={{ background: 'radial-gradient(circle, #10b981, transparent)' }} />
        <div className="relative z-10">
          <div className="w-24 h-24 rounded-3xl bg-emerald-500 text-white font-black text-4xl flex items-center justify-center mx-auto mb-5 shadow-2xl shadow-emerald-500/30">
            {profile?.name?.charAt(0)?.toUpperCase() || 'D'}
          </div>
          <h1 className="text-2xl font-black text-white">{profile?.name}</h1>
          <p className="text-slate-400 text-sm mt-1">Driver ID #{user?.id}</p>
          <div className="flex items-center justify-center gap-2 mt-3">
            <span className="flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold" style={{ background: statusBg, color: statusColor }}>
              <span className="w-1.5 h-1.5 rounded-full inline-block" style={{ background: statusColor }} />
              {profile?.status?.replace(/_/g, ' ')}
            </span>
          </div>
        </div>
      </motion.div>

      {/* Quick Stats */}
      <div className="grid grid-cols-3 gap-4">
        {[
          { label: 'Earnings', value: `₹${Math.round(earnings?.totalEarnings ?? 0)}`, icon: DollarSign, color: '#10b981', bg: 'rgba(16,185,129,0.1)' },
          { label: 'Deliveries', value: `${earnings?.totalDeliveries ?? 0}`, icon: Package, color: '#3b82f6', bg: 'rgba(59,130,246,0.1)' },
          { label: 'Rating', value: '4.9', icon: Star, color: '#f59e0b', bg: 'rgba(245,158,11,0.1)' },
        ].map((s) => (
          <div key={s.label} className="p-4 rounded-2xl text-center" style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.08)' }}>
            <div className="w-9 h-9 rounded-xl flex items-center justify-center mx-auto mb-3" style={{ background: s.bg }}>
              <s.icon size={18} style={{ color: s.color }} />
            </div>
            <p className="font-black text-white text-lg">{s.value}</p>
            <p className="text-slate-500 text-xs font-semibold">{s.label}</p>
          </div>
        ))}
      </div>

      {/* Info */}
      <div className="rounded-2xl overflow-hidden" style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.08)' }}>
        <div className="p-5" style={{ borderBottom: '1px solid rgba(148,163,184,0.06)' }}>
          <p className="text-xs font-black text-slate-600 uppercase tracking-widest mb-4">Account Information</p>
          <div className="space-y-4">
            {[
              { icon: User, label: 'Full Name', value: profile?.name },
              { icon: Phone, label: 'Phone Number', value: profile?.phone },
              { icon: Bike, label: 'Vehicle Type', value: VEHICLE_LABELS[profile?.vehicleType] ?? profile?.vehicleType },
              { icon: Shield, label: 'Status', value: profile?.status?.replace(/_/g, ' ') },
            ].map((row) => (
              <div key={row.label} className="flex items-center gap-4">
                <div className="w-9 h-9 rounded-xl flex items-center justify-center shrink-0" style={{ background: 'rgba(148,163,184,0.06)' }}>
                  <row.icon size={16} className="text-slate-400" />
                </div>
                <div className="flex-1">
                  <p className="text-xs text-slate-500 font-semibold">{row.label}</p>
                  <p className="text-white font-bold text-sm mt-0.5">{row.value ?? '—'}</p>
                </div>
                <ChevronRight size={14} className="text-slate-700" />
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Sign Out */}
      <motion.button
        whileTap={{ scale: 0.97 }}
        onClick={handleLogout}
        className="w-full py-4 rounded-2xl font-black text-sm flex items-center justify-center gap-3 transition-all"
        style={{ background: 'rgba(244,63,94,0.08)', border: '1px solid rgba(244,63,94,0.15)', color: '#f43f5e' }}
      >
        <LogOut size={18} />
        Sign Out
      </motion.button>
    </div>
  )
}
