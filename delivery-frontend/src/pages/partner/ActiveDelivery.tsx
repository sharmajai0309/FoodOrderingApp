import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { api } from '../../api/axios'
import { motion, AnimatePresence } from 'framer-motion'
import {
  Navigation, Store, CheckCircle2, ChevronRight, Phone, MessageSquare,
  MapPin, Clock, PackageCheck, AlertCircle, Bike, Star, Loader2, X, AlertTriangle
} from 'lucide-react'
import { useAuthStore } from '../../store/useAuthStore'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { useState } from 'react'

interface Assignment {
  id: number
  orderId: number
  restaurantId: number
  restaurantName: string
  customerAddress: string
  deliveryFee: number
  status: 'ACCEPTED' | 'PICKED_UP' | 'DELIVERED' | 'CANCELLED' | 'PENDING'
  assignedAt: string
  pickedAt?: string
  deliveredAt?: string
  driverName?: string
}

const TIMELINE_STEPS = [
  { key: 'ACCEPTED', label: 'Order Accepted', sub: 'Head to the restaurant', icon: Store, color: '#10b981' },
  { key: 'PICKED_UP', label: 'Picked Up', sub: 'Food collected from restaurant', icon: PackageCheck, color: '#3b82f6' },
  { key: 'DELIVERED', label: 'Delivered', sub: 'Order handed to customer', icon: CheckCircle2, color: '#a855f7' },
]

function getStepIndex(status: string) {
  if (status === 'ACCEPTED') return 0
  if (status === 'PICKED_UP') return 1
  if (status === 'DELIVERED') return 2
  return -1
}

export default function ActiveDelivery() {
  const user = useAuthStore((s) => s.user)
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [showCancelModal, setShowCancelModal] = useState(false)
  const [cancelReason, setCancelReason] = useState('')
  const [customReason, setCustomReason] = useState('')

  const CANCEL_REASONS = [
    'Traffic jam / Road blocked',
    'Vehicle breakdown',
    'Customer unreachable',
    'Restaurant not ready',
    'Personal emergency',
    'Other',
  ]

  const { data: assignment, isLoading } = useQuery<Assignment | null>({
    queryKey: ['active-assignment', user?.id],
    queryFn: async () => {
      const res = await api.get(`/api/delivery/driver/${user?.id}/active`)
      return res.data?.data ?? null
    },
    enabled: !!user?.id,
    refetchInterval: 10000,
  })

  const pickupMutation = useMutation({
    mutationFn: async (assignmentId: number) => {
      const res = await api.post(`/api/delivery/${assignmentId}/pickup?driverId=${user?.id}`)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['active-assignment'] })
      toast.success('Order picked up! 📦 Head to the customer.', { duration: 4000 })
    },
    onError: (e: any) => toast.error(e?.response?.data?.message || 'Failed to update status'),
  })

  const deliverMutation = useMutation({
    mutationFn: async (assignmentId: number) => {
      const res = await api.post(`/api/delivery/${assignmentId}/deliver?driverId=${user?.id}`)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['active-assignment'] })
      queryClient.invalidateQueries({ queryKey: ['earnings'] })
      toast.success('🎉 Delivery Complete! ₹30 credited to your wallet.', { duration: 6000 })
      setTimeout(() => navigate('/earnings'), 2000)
    },
    onError: (e: any) => toast.error(e?.response?.data?.message || 'Failed to mark delivered'),
  })

  const cancelMutation = useMutation({
    mutationFn: async ({ assignmentId, reason }: { assignmentId: number; reason: string }) => {
      const res = await api.post(`/api/delivery/${assignmentId}/cancel?reason=${encodeURIComponent(reason)}`)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['active-assignment'] })
      queryClient.invalidateQueries({ queryKey: ['open-assignments'] })
      setShowCancelModal(false)
      toast('Order cancelled', { description: 'The order has been returned to the pool.', duration: 4000 })
      setTimeout(() => navigate('/dashboard'), 1500)
    },
    onError: (e: any) => toast.error(e?.response?.data?.message || 'Failed to cancel order'),
  })

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-72">
        <div className="flex flex-col items-center gap-4">
          <Loader2 size={32} className="text-emerald-400 animate-spin" />
          <p className="text-slate-500 font-semibold">Loading active delivery...</p>
        </div>
      </div>
    )
  }

  if (!assignment) {
    return (
      <div className="max-w-2xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="flex flex-col items-center justify-center py-24 rounded-3xl text-center"
          style={{ background: 'var(--surface)', border: '1px dashed rgba(148,163,184,0.12)' }}
        >
          <div className="w-24 h-24 rounded-3xl flex items-center justify-center mb-6" style={{ background: 'rgba(16,185,129,0.06)' }}>
            <Bike size={40} className="text-emerald-600" />
          </div>
          <h2 className="text-2xl font-black text-white mb-3">No Active Delivery</h2>
          <p className="text-slate-500 max-w-xs">Head to your Dashboard to accept an available order and start earning.</p>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => navigate('/dashboard')}
            className="mt-8 flex items-center gap-2 px-6 py-3.5 rounded-xl font-bold text-sm"
            style={{ background: 'rgba(16,185,129,0.1)', border: '1px solid rgba(16,185,129,0.2)', color: '#10b981' }}
          >
            Browse Orders <ChevronRight size={16} />
          </motion.button>
        </motion.div>
      </div>
    )
  }

  const stepIndex = getStepIndex(assignment.status)
  const isPending = pickupMutation.isPending || deliverMutation.isPending

  return (
    <div className="max-w-3xl mx-auto space-y-5 pb-8">

      {/* Header Card */}
      <motion.div
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        className="relative overflow-hidden rounded-3xl p-6"
        style={{ background: 'linear-gradient(135deg, #0c1a2e 0%, #0f2a1e 100%)', border: '1px solid rgba(16,185,129,0.2)' }}
      >
        <div className="absolute top-0 right-0 w-48 h-48 opacity-10 rounded-full" style={{ background: 'radial-gradient(circle, #10b981, transparent)', transform: 'translate(20%, -20%)' }} />
        <div className="relative z-10">
          <div className="flex items-center gap-2 mb-3">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping-slow" />
            <span className="text-xs font-black text-emerald-400 uppercase tracking-widest">Live Delivery</span>
          </div>
          <div className="flex items-start justify-between">
            <div>
              <h1 className="text-2xl font-black text-white">Order #{assignment.orderId}</h1>
              <p className="text-slate-400 text-sm mt-1">Assignment #{assignment.id}</p>
            </div>
            <div className="text-right">
              <p className="text-3xl font-black text-emerald-400">₹{Number(assignment.deliveryFee ?? 30).toFixed(0)}</p>
              <p className="text-slate-500 text-xs font-semibold mt-1">Delivery Fee</p>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Progress Bar */}
      <div className="rounded-2xl p-5" style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.07)' }}>
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm font-bold text-slate-300">Delivery Progress</p>
          <p className="text-xs text-slate-500">{stepIndex + 1} / {TIMELINE_STEPS.length}</p>
        </div>
        <div className="w-full h-2 rounded-full" style={{ background: 'rgba(148,163,184,0.1)' }}>
          <motion.div
            className="h-full rounded-full"
            style={{ background: 'linear-gradient(90deg, #10b981, #059669)' }}
            initial={{ width: '0%' }}
            animate={{ width: `${((stepIndex + 1) / TIMELINE_STEPS.length) * 100}%` }}
            transition={{ duration: 0.8, ease: 'easeOut' }}
          />
        </div>
      </div>

      {/* Timeline */}
      <div className="rounded-2xl p-6 space-y-0" style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.07)' }}>
        <p className="text-sm font-black text-slate-300 mb-6">Order Timeline</p>
        {TIMELINE_STEPS.map((step, index) => {
          const isCompleted = index < stepIndex
          const isCurrent = index === stepIndex
          const isUpcoming = index > stepIndex
          const isLast = index === TIMELINE_STEPS.length - 1

          return (
            <div key={step.key} className="relative flex items-start gap-4 pb-6">
              {/* Connector */}
              {!isLast && (
                <div className={isCompleted || isCurrent ? 'timeline-connector' : 'timeline-connector-inactive'} />
              )}

              {/* Icon */}
              <div className="relative z-10 shrink-0">
                <AnimatePresence mode="wait">
                  {isCompleted ? (
                    <motion.div key="done" initial={{ scale: 0 }} animate={{ scale: 1 }} className="w-8 h-8 rounded-full flex items-center justify-center" style={{ background: step.color + '20', border: `2px solid ${step.color}` }}>
                      <CheckCircle2 size={16} style={{ color: step.color }} />
                    </motion.div>
                  ) : isCurrent ? (
                    <motion.div key="current" animate={{ boxShadow: [`0 0 0 0 ${step.color}60`, `0 0 0 8px ${step.color}00`] }} transition={{ duration: 1.5, repeat: Infinity }} className="w-8 h-8 rounded-full flex items-center justify-center" style={{ background: step.color, border: `2px solid ${step.color}` }}>
                      <step.icon size={16} className="text-white" />
                    </motion.div>
                  ) : (
                    <div key="upcoming" className="w-8 h-8 rounded-full flex items-center justify-center" style={{ background: 'rgba(148,163,184,0.06)', border: '2px solid rgba(148,163,184,0.15)' }}>
                      <step.icon size={16} className="text-slate-600" />
                    </div>
                  )}
                </AnimatePresence>
              </div>

              {/* Content */}
              <div className="flex-1 pt-1">
                <p className={`font-bold text-sm ${isCompleted ? 'text-slate-400 line-through' : isCurrent ? 'text-white' : 'text-slate-600'}`}>
                  {step.label}
                </p>
                <p className={`text-xs mt-0.5 ${isCurrent ? 'text-slate-400' : 'text-slate-600'}`}>{step.sub}</p>
                {isCurrent && (
                  <motion.span
                    initial={{ opacity: 0, scale: 0.8 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="inline-block mt-2 text-[10px] font-black uppercase tracking-widest px-2.5 py-1 rounded-full"
                    style={{ background: step.color + '20', color: step.color }}
                  >
                    Current
                  </motion.span>
                )}
              </div>
            </div>
          )
        })}
      </div>

      {/* Pickup & Dropoff Cards */}
      <div className="grid sm:grid-cols-2 gap-4">
        <div className="rounded-2xl p-5" style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.07)' }}>
          <div className="flex items-center gap-2 mb-3">
            <div className="w-8 h-8 rounded-xl flex items-center justify-center" style={{ background: 'rgba(251,191,36,0.1)' }}>
              <Store size={16} className="text-amber-400" />
            </div>
            <p className="text-xs font-black text-slate-500 uppercase tracking-widest">Restaurant Pickup</p>
          </div>
          <p className="font-black text-white text-base">{assignment.restaurantName || `Restaurant #${assignment.restaurantId}`}</p>
          <button className="mt-4 w-full py-2.5 rounded-xl text-xs font-bold flex items-center justify-center gap-2 text-amber-400 transition-all" style={{ background: 'rgba(251,191,36,0.06)', border: '1px solid rgba(251,191,36,0.15)' }}>
            <Phone size={12} /> Call Restaurant
          </button>
        </div>

        <div className="rounded-2xl p-5" style={{ background: 'var(--surface)', border: '1px solid rgba(148,163,184,0.07)' }}>
          <div className="flex items-center gap-2 mb-3">
            <div className="w-8 h-8 rounded-xl flex items-center justify-center" style={{ background: 'rgba(16,185,129,0.1)' }}>
              <MapPin size={16} className="text-emerald-400" />
            </div>
            <p className="text-xs font-black text-slate-500 uppercase tracking-widest">Customer Dropoff</p>
          </div>
          <p className="font-bold text-white text-sm">{assignment.customerAddress}</p>
          <button className="mt-4 w-full py-2.5 rounded-xl text-xs font-bold flex items-center justify-center gap-2 text-emerald-400 transition-all" style={{ background: 'rgba(16,185,129,0.06)', border: '1px solid rgba(16,185,129,0.15)' }}>
            <MessageSquare size={12} /> Message Customer
          </button>
        </div>
      </div>

      {/* ETA */}
      <div className="flex items-center gap-3 px-5 py-4 rounded-2xl" style={{ background: 'rgba(59,130,246,0.06)', border: '1px solid rgba(59,130,246,0.15)' }}>
        <Clock size={18} className="text-blue-400 shrink-0" />
        <div>
          <p className="text-sm font-bold text-white">Estimated Arrival</p>
          <p className="text-blue-400 text-xs font-semibold">~{Math.floor(Math.random() * 15 + 5)} mins away from customer</p>
        </div>
        <div className="ml-auto flex items-center gap-1 text-amber-400">
          {[...Array(5)].map((_, i) => <Star key={i} size={12} className="fill-amber-400" />)}
          <span className="text-xs font-bold ml-1">4.9</span>
        </div>
      </div>

      {/* CTA Button */}
      <AnimatePresence mode="wait">
        {assignment.status === 'ACCEPTED' && (
          <motion.button
            key="pickup-btn"
            initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => pickupMutation.mutate(assignment.id)}
            disabled={isPending}
            className="w-full py-5 rounded-2xl font-black text-white text-lg flex items-center justify-center gap-3 transition-all"
            style={{ background: 'linear-gradient(135deg, #f59e0b, #d97706)', boxShadow: '0 8px 32px rgba(245,158,11,0.25)' }}
          >
            {isPending ? <Loader2 size={22} className="animate-spin" /> : <><PackageCheck size={22} /> Confirm Pickup</>}
          </motion.button>
        )}
        {assignment.status === 'PICKED_UP' && (
          <motion.button
            key="deliver-btn"
            initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => deliverMutation.mutate(assignment.id)}
            disabled={isPending}
            className="w-full py-5 rounded-2xl font-black text-white text-lg flex items-center justify-center gap-3 transition-all"
            style={{ background: 'linear-gradient(135deg, #10b981, #059669)', boxShadow: '0 8px 32px rgba(16,185,129,0.3)' }}
          >
            {isPending ? <Loader2 size={22} className="animate-spin" /> : <><Navigation size={22} /> Mark Delivered — ₹{Number(assignment.deliveryFee ?? 30).toFixed(0)} Earned!</>}
          </motion.button>
        )}
        {assignment.status === 'DELIVERED' && (
          <motion.div
            key="complete"
            initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }}
            className="w-full py-6 rounded-2xl flex flex-col items-center justify-center gap-2"
            style={{ background: 'rgba(16,185,129,0.1)', border: '1px solid rgba(16,185,129,0.2)' }}
          >
            <CheckCircle2 size={36} className="text-emerald-400" />
            <p className="font-black text-white text-lg">Delivery Complete!</p>
            <p className="text-emerald-400 text-sm font-semibold">Great job! ₹{Number(assignment.deliveryFee ?? 30).toFixed(0)} has been credited.</p>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Cancel Order Button (shown only for ACCEPTED/PICKED_UP) */}
      {(assignment.status === 'ACCEPTED' || assignment.status === 'PICKED_UP') && (
        <button
          onClick={() => setShowCancelModal(true)}
          className="w-full py-3 rounded-xl text-sm font-bold text-rose-400 transition-all hover:bg-rose-500/10"
          style={{ border: '1px solid rgba(244,63,94,0.2)' }}
        >
          Cancel Order
        </button>
      )}

      {/* Cancel Reason Modal */}
      <AnimatePresence>
        {showCancelModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-4"
            style={{ background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(4px)' }}
            onClick={(e) => e.target === e.currentTarget && setShowCancelModal(false)}
          >
            <motion.div
              initial={{ y: 60, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              exit={{ y: 60, opacity: 0 }}
              transition={{ type: 'spring', damping: 25, stiffness: 300 }}
              className="w-full max-w-md rounded-3xl overflow-hidden"
              style={{ background: 'var(--surface)', border: '1px solid rgba(244,63,94,0.2)' }}
            >
              {/* Modal Header */}
              <div className="p-6 flex items-start justify-between" style={{ borderBottom: '1px solid rgba(148,163,184,0.08)' }}>
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl flex items-center justify-center" style={{ background: 'rgba(244,63,94,0.1)' }}>
                    <AlertTriangle size={18} className="text-rose-400" />
                  </div>
                  <div>
                    <p className="font-black text-white">Cancel Order?</p>
                    <p className="text-slate-500 text-xs mt-0.5">Please select a reason below</p>
                  </div>
                </div>
                <button onClick={() => setShowCancelModal(false)} className="w-8 h-8 rounded-lg flex items-center justify-center text-slate-500 hover:text-white hover:bg-white/5 transition-all">
                  <X size={16} />
                </button>
              </div>

              {/* Reasons */}
              <div className="p-5 space-y-2">
                {CANCEL_REASONS.map((reason) => (
                  <button
                    key={reason}
                    onClick={() => setCancelReason(reason)}
                    className="w-full text-left px-4 py-3 rounded-xl text-sm font-semibold transition-all"
                    style={{
                      background: cancelReason === reason ? 'rgba(244,63,94,0.12)' : 'rgba(148,163,184,0.04)',
                      border: cancelReason === reason ? '1px solid rgba(244,63,94,0.4)' : '1px solid rgba(148,163,184,0.1)',
                      color: cancelReason === reason ? '#f87171' : '#94a3b8',
                    }}
                  >
                    {reason}
                  </button>
                ))}

                {cancelReason === 'Other' && (
                  <motion.textarea
                    initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }}
                    value={customReason}
                    onChange={e => setCustomReason(e.target.value)}
                    placeholder="Please describe the issue..."
                    rows={3}
                    className="w-full px-4 py-3 rounded-xl text-sm font-medium resize-none outline-none mt-2"
                    style={{ background: 'rgba(148,163,184,0.06)', border: '1px solid rgba(148,163,184,0.15)', color: 'white' }}
                  />
                )}
              </div>

              {/* Actions */}
              <div className="px-5 pb-6 flex gap-3">
                <button
                  onClick={() => setShowCancelModal(false)}
                  className="flex-1 py-3 rounded-xl text-sm font-bold text-slate-400 transition-all hover:text-white hover:bg-white/5"
                  style={{ border: '1px solid rgba(148,163,184,0.1)' }}
                >
                  Keep Order
                </button>
                <motion.button
                  whileTap={{ scale: 0.97 }}
                  disabled={!cancelReason || cancelMutation.isPending}
                  onClick={() => {
                    const reason = cancelReason === 'Other' ? customReason || 'Other' : cancelReason
                    if (reason && assignment) cancelMutation.mutate({ assignmentId: assignment.id, reason })
                  }}
                  className="flex-1 py-3 rounded-xl text-sm font-black text-white flex items-center justify-center gap-2 transition-all disabled:opacity-50"
                  style={{ background: cancelReason ? 'linear-gradient(135deg, #e11d48, #be123c)' : 'rgba(244,63,94,0.3)' }}
                >
                  {cancelMutation.isPending ? <Loader2 size={16} className="animate-spin" /> : 'Confirm Cancel'}
                </motion.button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
