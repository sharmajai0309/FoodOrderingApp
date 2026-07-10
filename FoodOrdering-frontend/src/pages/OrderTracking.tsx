import { useQuery } from '@tanstack/react-query'
import { useParams, Link } from 'react-router-dom'
import { api } from '../api/axios'
import { motion, AnimatePresence } from 'framer-motion'
import {
  ArrowLeft, CheckCircle2, Clock, Package, Receipt,
  ShoppingBag, Truck, Phone, Share2, HelpCircle, AlertCircle
} from 'lucide-react'
import { useEffect, useRef } from 'react'
import { toast } from 'react-toastify'

import { AppCard } from '../components/ui/AppCard'
import { AppButton } from '../components/ui/AppButton'
import { RatingBadge } from '../components/ui/RatingBadge'

interface DeliveryPartner {
  id: number
  name: string
  phone: string
  vehicleType: string
}

interface ResponseOrder {
  id: number
  totalAmount: number
  orderStatus: string
  createdAt?: string
  acceptedAt?: string
  preparingAt?: string
  pickedUpAt?: string
  outForDeliveryAt?: string
  deliveredAt?: string
  deliveryPartnerId?: number
  items?: Array<{
    id: number
    food: { name: string; images: string[]; price: number }
    quantity: number
    totalPrice: number
  }>
  deliveryAddress?: { city?: string; street?: string; zipCode?: string }
  restaurantName?: string
  restaurantImage?: string
  customerName?: string
}

const STATUS_STAGES = [
  { key: 'PENDING',          label: 'Order Placed',       icon: ShoppingBag, color: '#FF7A2F' },
  { key: 'PAID',             label: 'Payment Confirmed',   icon: Receipt,     color: '#FF7A2F' },
  { key: 'ACCEPTED',         label: 'Accepted',            icon: CheckCircle2,color: '#FF7A2F' },
  { key: 'PREPARING',        label: 'Preparing',           icon: Clock,       color: '#FF7A2F' },
  { key: 'PICKED_UP',        label: 'Picked Up',           icon: Package,     color: '#FF7A2F' },
  { key: 'OUT_FOR_DELIVERY', label: 'On the Way',          icon: Truck,       color: '#FF7A2F' },
  { key: 'DELIVERED',        label: 'Delivered',           icon: CheckCircle2,color: '#10b981' },
]

const TIMESTAMP_FIELDS: Record<string, keyof ResponseOrder> = {
  PENDING:          'createdAt',
  PAID:             'createdAt',
  ACCEPTED:         'acceptedAt',
  PREPARING:        'preparingAt',
  PICKED_UP:        'pickedUpAt',
  OUT_FOR_DELIVERY: 'outForDeliveryAt',
  DELIVERED:        'deliveredAt',
}

const VEHICLE_EMOJI: Record<string, string> = {
  BIKE: '🏍️', ELECTRIC_BIKE: '⚡', SCOOTER: '🛵', BICYCLE: '🚲', WALKING_DELIVERY: '🚶'
}

function formatTime(ts?: string) {
  if (!ts) return null
  return new Date(ts).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true })
}

function calcETA(assignedAt?: string): string {
  if (!assignedAt) return '20 min'
  const assigned = new Date(assignedAt).getTime()
  const elapsed = (Date.now() - assigned) / 60000
  const remaining = Math.max(0, Math.round(30 - elapsed))
  return remaining === 0 ? 'Arriving' : `${remaining} min`
}

export default function OrderTracking() {
  const { id } = useParams()
  const prevStatusRef = useRef<string | null>(null)

  const { data: order, isLoading, isError } = useQuery<ResponseOrder>({
    queryKey: ['order', id],
    queryFn: async () => {
      const res = await api.get(`/v1/api/customer/order/${id}`)
      return res.data.data as ResponseOrder
    },
    refetchInterval: (query) => {
      const status = query.state.data?.orderStatus
      if (status === 'DELIVERED' || status === 'CANCELLED' || status === 'PAYMENT_CANCELLED') return false
      return 4000
    },
  })

  // Driver info
  const { data: driver } = useQuery<DeliveryPartner>({
    queryKey: ['driver', order?.deliveryPartnerId],
    queryFn: async () => {
      const res = await api.get(`/api/deliveryPartners/${order!.deliveryPartnerId}`)
      return res.data?.data
    },
    enabled: !!order?.deliveryPartnerId,
  })

  useEffect(() => {
    if (!order) return
    const curr = order.orderStatus
    if (prevStatusRef.current && prevStatusRef.current !== curr) {
      toast.info(`Status Update: ${curr.replace(/_/g, ' ')}`)
    }
    prevStatusRef.current = curr
  }, [order?.orderStatus, order])

  if (isLoading) return (
    <div className="max-w-md mx-auto p-6 space-y-6 animate-pulse">
      <div className="h-64 bg-slate-50 dark:bg-slate-900 rounded-[3rem]" />
      <div className="h-48 bg-slate-50 dark:bg-slate-900 rounded-[3rem]" />
    </div>
  )

  if (isError || !order) return (
    <div className="max-w-md mx-auto py-20 px-6 text-center">
      <div className="w-20 h-20 bg-rose-50 rounded-full flex items-center justify-center mx-auto mb-6 text-rose-500"><AlertCircle size={40} /></div>
      <h2 className="text-2xl font-black mb-2">Order not found</h2>
      <Link to="/orders" className="text-primary font-bold">Back to my orders</Link>
    </div>
  )

  const stageIndex = STATUS_STAGES.findIndex(s => s.key === order.orderStatus)
  const isCancelled = order.orderStatus === 'CANCELLED' || order.orderStatus === 'PAYMENT_CANCELLED'
  const activeIndex = isCancelled ? -1 : (stageIndex === -1 ? 0 : stageIndex)
  const isLive = !isCancelled && order.orderStatus !== 'DELIVERED'

  return (
    <div className="max-w-5xl mx-auto min-h-screen bg-transparent sm:bg-slate-50/50 dark:sm:bg-slate-950 pb-32">
      {/* ── Top Bar ── */}
      <div className="p-6 flex items-center justify-between">
        <Link to="/orders" className="w-10 h-10 bg-white dark:bg-slate-900 rounded-full flex items-center justify-center shadow-sm border border-slate-100 dark:border-slate-800">
           <ArrowLeft size={18} />
        </Link>
        <div className="text-center">
            <h1 className="text-sm font-black uppercase tracking-[0.2em] text-slate-400">Order Tracking</h1>
            <p className="text-xs font-bold text-slate-900 dark:text-white">#{order.id}</p>
        </div>
        <button className="w-10 h-10 bg-white dark:bg-slate-900 rounded-full flex items-center justify-center shadow-sm border border-slate-100 dark:border-slate-800">
           <HelpCircle size={18} />
        </button>
      </div>

      <div className="px-6 space-y-6 md:space-y-0 md:grid md:grid-cols-5 md:gap-8 md:items-start">
        <div className="md:col-span-3 space-y-6">
          {/* ── Status Banner ── */}
          <AppCard className="p-8 relative overflow-hidden" noPadding>
          {isLive && (
             <div className="absolute top-0 right-0 p-6">
               <div className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-primary/10 text-primary border border-primary/20">
                  <span className="w-1.5 h-1.5 rounded-full bg-primary animate-pulse" />
                  <span className="text-[10px] font-black uppercase tracking-widest">Live</span>
               </div>
             </div>
          )}

          <div className="p-8">
            <p className="text-xs font-black text-slate-400 uppercase tracking-[0.2em] mb-2">Estimated Arrival</p>
            <div className="flex items-baseline gap-2 mb-6">
               <span className="text-5xl font-black tracking-tighter text-slate-900 dark:text-white">
                 {isCancelled ? 'Cancelled' : order.orderStatus === 'DELIVERED' ? 'Arrived' : calcETA(order.outForDeliveryAt)}
               </span>
               <span className="text-lg font-black text-primary italic">
                 {isLive ? 'mins' : ''}
               </span>
            </div>

            <div className="flex items-center gap-3 py-4 border-t border-slate-50 dark:border-slate-800/50">
               <div className="w-10 h-10 rounded-xl bg-slate-50 dark:bg-slate-800 flex items-center justify-center text-primary">
                  {(() => {
                    const StepIconActive = STATUS_STAGES[activeIndex]?.icon || Package;
                    return (
                      <motion.div animate={{ scale: [1, 1.1, 1] }} transition={{ repeat: Infinity, duration: 2 }}>
                        <StepIconActive size={20} />
                      </motion.div>
                    );
                  })()}
               </div>
               <div>
                  <p className="text-sm font-black text-slate-900 dark:text-white">
                    {STATUS_STAGES[activeIndex]?.label || 'Processing'}
                  </p>
                  <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                    Order from {order.restaurantName || 'Restaurant'}
                  </p>
               </div>
            </div>
          </div>

          <div className="h-2 bg-slate-100 dark:bg-slate-800 flex">
             {STATUS_STAGES.map((_, i) => (
               <div 
                 key={i} 
                 className={`flex-1 h-full transition-all duration-1000 ${i <= activeIndex ? 'bg-primary' : 'bg-transparent'}`} 
                 style={{ opacity: i <= activeIndex ? 1 : 0.2 }}
               />
             ))}
          </div>
        </AppCard>

        {/* ── Driver Card (Conditional) ── */}
        <AnimatePresence>
          {driver && isLive && (
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}>
              <AppCard className="p-6">
                <div className="flex items-center gap-4 mb-6">
                   <div className="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center text-2xl border border-primary/20">
                      {VEHICLE_EMOJI[driver.vehicleType] || '🛵'}
                   </div>
                   <div className="flex-1">
                      <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-0.5">Delivery Partner</p>
                      <h4 className="font-black text-slate-900 dark:text-white">{driver.name}</h4>
                   </div>
                   <RatingBadge rating={4.9} variant="white" className="border-none shadow-none" />
                </div>
                <div className="flex gap-3">
                   <AppButton variant="secondary" className="flex-1 gap-2 py-3" onClick={() => window.location.href = `tel:${driver.phone}`}>
                      <Phone size={14} /> Call Partner
                   </AppButton>
                   <AppButton variant="ghost" className="w-12 h-12 p-0 rounded-2xl border-slate-100 dark:border-slate-800">
                      <Share2 size={16} />
                   </AppButton>
                </div>
              </AppCard>
            </motion.div>
          )}
        </AnimatePresence>
        </div>

        <div className="md:col-span-2 space-y-8 mt-6 md:mt-0">
        {/* ── Order Timeline ── */}
        <section className="space-y-4">
           <h3 className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] px-2">Order Tracking</h3>
           <div className="space-y-3">
              {STATUS_STAGES.map((stage, idx) => {
                const isCompleted = idx <= activeIndex;
                const ts = formatTime(order[TIMESTAMP_FIELDS[stage.key] as keyof typeof order] as string | undefined);
                
                return (
                  <motion.div 
                    key={stage.key}
                    initial={{ opacity: 0, x: -10 }}
                    animate={{ opacity: isCompleted ? 1 : 0.4, x: 0 }}
                    transition={{ delay: idx * 0.1 }}
                    className="flex items-center gap-4 bg-white dark:bg-slate-900 p-4 rounded-[2rem] border border-slate-100 dark:border-slate-800/50 shadow-sm"
                  >
                    <div className={`w-10 h-10 rounded-xl flex items-center justify-center transition-colors ${isCompleted ? 'bg-primary text-white shadow-lg shadow-primary/20' : 'bg-slate-50 dark:bg-slate-800 text-slate-300'}`}>
                       <stage.icon size={18} />
                    </div>
                    <div className="flex-1">
                       <p className={`text-sm font-black italic ${isCompleted ? 'text-slate-900 dark:text-white' : 'text-slate-400'}`}>
                         {stage.label}
                       </p>
                       {ts && isCompleted && (
                         <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">{ts}</p>
                       )}
                    </div>
                    {isCompleted && (
                      <CheckCircle2 size={16} className="text-emerald-500" />
                    )}
                  </motion.div>
                )
              })}
           </div>
        </section>

        {/* ── Order Items ── */}
        <section className="space-y-4 pb-10">
           <h3 className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] px-2">Items Summary</h3>
           <AppCard className="p-6 space-y-4 divide-y divide-slate-50 dark:divide-slate-800/50">
             {order.items?.map(item => (
                <div key={item.id} className="flex gap-4 pt-4 first:pt-0">
                   <div className="w-12 h-12 rounded-xl bg-slate-50 dark:bg-slate-800 overflow-hidden shrink-0">
                      {item.food.images?.[0] ? <img src={item.food.images[0]} className="w-full h-full object-cover" /> : <Package className="w-full h-full p-3 text-slate-200" />}
                   </div>
                   <div className="flex-1 min-w-0">
                      <p className="text-sm font-black text-slate-900 dark:text-white italic truncate">{item.food.name}</p>
                      <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Qty: {item.quantity}</p>
                   </div>
                   <p className="text-sm font-black text-slate-900 dark:text-white tracking-tighter">₹{item.totalPrice}</p>
                </div>
             ))}
             <div className="pt-4 flex justify-between items-center text-lg">
                <span className="font-black text-slate-400 uppercase text-[10px] tracking-widest">Total Bill</span>
                <span className="font-black text-slate-900 dark:text-white tracking-tighter italic">₹{order.totalAmount}</span>
             </div>
           </AppCard>
        </section>
        </div>
      </div>
    </div>
  )
}
