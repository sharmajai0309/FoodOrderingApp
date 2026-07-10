import { useQuery } from '@tanstack/react-query'
import { api } from '../api/axios'
import { useAuthStore } from '../store/useAuthStore'
import { motion, AnimatePresence } from 'framer-motion'
import { Link } from 'react-router-dom'
import { Package, Clock, CheckCircle2, XCircle, ShoppingBag, ChevronRight, MapPin, CreditCard } from 'lucide-react'

interface ResponseOrder {
  id: number
  totalAmount: number
  orderStatus: string
  createdAt?: string
  items?: Array<{
    id: number
    food: { name: string; images: string[]; price: number }
    quantity: number
    totalPrice: number
  }>
  deliveryAddress?: { city: string; street: string }
}

const statusConfig: Record<string, { color: string; bg: string; icon: typeof CheckCircle2; label: string }> = {
  PENDING:           { color: 'text-amber-500',  bg: 'bg-amber-500/10  border-amber-500/20',    icon: Clock,          label: 'Pending'    },
  PAID:              { color: 'text-emerald-500', bg: 'bg-emerald-500/10 border-emerald-500/20', icon: CheckCircle2,   label: 'Paid'       },
  CONFIRMED:         { color: 'text-blue-500',    bg: 'bg-blue-500/10   border-blue-500/20',     icon: CheckCircle2,   label: 'Confirmed'  },
  OUT_FOR_DELIVERY:  { color: 'text-purple-500',  bg: 'bg-purple-500/10 border-purple-500/20',   icon: Package,        label: 'On the Way' },
  DELIVERED:         { color: 'text-emerald-500', bg: 'bg-emerald-500/10 border-emerald-500/20', icon: CheckCircle2,   label: 'Delivered'  },
  CANCELLED:         { color: 'text-rose-500',    bg: 'bg-rose-500/10   border-rose-500/20',     icon: XCircle,        label: 'Cancelled'  },
  PAYMENT_CANCELLED: { color: 'text-rose-500',    bg: 'bg-rose-500/10   border-rose-500/20',     icon: XCircle,        label: 'Payment Failed' },
}

function StatusBadge({ status }: { status: string }) {
  const cfg = statusConfig[status] || { color: 'text-slate-500', bg: 'bg-slate-100', icon: Clock, label: status }
  const Icon = cfg.icon
  return (
    <span className={`inline-flex items-center gap-1.5 text-[10px] font-black uppercase tracking-widest px-3 py-1.5 rounded-full border ${cfg.bg} ${cfg.color}`}>
      <Icon size={12} /> {cfg.label}
    </span>
  )
}

export default function Orders() {
  const { user } = useAuthStore()

  const { data: orders, isLoading, isError } = useQuery<ResponseOrder[]>({
    queryKey: ['orders', user?.id],
    queryFn: async () => {
      // GET /v1/api/customer/order/user → ApiResponse<ResponseOrder[]>
      const res = await api.get('/v1/api/customer/order/user')
      return res.data.data as ResponseOrder[]
    },
    enabled: !!user,
  })

  if (!user) return (
    <div className="text-center py-20">
      <div className="w-24 h-24 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-6 text-primary"><ShoppingBag size={48} /></div>
      <h2 className="text-3xl font-black mb-2">Login to view orders</h2>
      <Link to="/auth/login" className="mt-4 inline-block bg-primary text-white px-8 py-3 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">Login</Link>
    </div>
  )

  return (
    <div className="max-w-4xl mx-auto space-y-12 pb-20 px-4">
      <motion.div initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} className="flex items-center gap-4">
        <div className="w-12 h-12 bg-primary/10 rounded-2xl flex items-center justify-center text-primary">
          <Package size={24} strokeWidth={2.5} />
        </div>
        <div>
          <h1 className="text-4xl font-black tracking-tight text-slate-900 dark:text-white">My Orders</h1>
          <p className="text-slate-500 font-medium">Track and review your delivery history.</p>
        </div>
      </motion.div>

      {isLoading && (
        <div className="space-y-6">
          {[1,2,3].map(i => (
            <div key={i} className="h-40 bg-slate-100 dark:bg-slate-900 animate-pulse rounded-3xl" />
          ))}
        </div>
      )}

      {isError && (
        <div className="bg-rose-50 dark:bg-rose-950/20 border border-rose-200 dark:border-rose-800 rounded-3xl p-10 text-center">
          <XCircle size={40} className="text-rose-400 mx-auto mb-4" />
          <h3 className="text-xl font-black text-rose-700 dark:text-rose-400">Failed to load orders</h3>
          <p className="text-rose-500 text-sm mt-2">Please try again later.</p>
        </div>
      )}

      {!isLoading && !isError && orders?.length === 0 && (
        <div className="text-center py-20 bg-slate-50 dark:bg-slate-900/50 border border-dashed border-border rounded-4xl">
          <div className="w-20 h-20 bg-slate-100 dark:bg-slate-800 rounded-full flex items-center justify-center mx-auto mb-6 text-slate-400">
            <ShoppingBag size={40} />
          </div>
          <h3 className="text-2xl font-black mb-2">No orders yet</h3>
          <p className="text-slate-500 font-medium mb-8 max-w-xs mx-auto">Looks like you haven't placed any orders. Browse restaurants to get started!</p>
          <Link to="/" className="bg-primary text-white px-8 py-3 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">
            Browse Restaurants
          </Link>
        </div>
      )}

      <AnimatePresence>
        {orders?.map((order, idx) => (
          <motion.div
            key={order.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: idx * 0.06 }}
            className="bg-white dark:bg-slate-950 border border-border rounded-3xl overflow-hidden hover:shadow-2xl hover:-translate-y-1 transition-all duration-500"
          >
            <div className="p-6 flex flex-col md:flex-row md:items-center justify-between gap-6 border-b border-slate-50 dark:border-slate-900">
              <div className="flex items-center gap-4">
                <div className="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center text-primary font-black text-lg border border-primary/10">
                  #{order.id}
                </div>
                <div>
                  <p className="font-black text-slate-900 dark:text-white text-xl tracking-tight">Order #{order.id}</p>
                  {order.deliveryAddress && (
                    <p className="text-xs font-bold text-slate-400 flex items-center gap-1 mt-1">
                      <MapPin size={12} /> {order.deliveryAddress.street}, {order.deliveryAddress.city}
                    </p>
                  )}
                </div>
              </div>
              <div className="flex items-center gap-4">
                <StatusBadge status={order.orderStatus} />
                <div className="flex items-center gap-2 font-black text-slate-900 dark:text-white text-xl">
                  <CreditCard size={18} className="text-primary" />
                  ₹{order.totalAmount}
                </div>
              </div>
            </div>

            {order.items && order.items.length > 0 && (
              <div className="divide-y divide-slate-50 dark:divide-slate-900">
                {order.items.slice(0, 3).map(item => (
                  <div key={item.id} className="px-6 py-4 flex items-center gap-4">
                    <div className="w-12 h-12 rounded-xl bg-slate-100 dark:bg-slate-800 overflow-hidden shrink-0">
                      {item.food.images?.[0] && (
                        <img src={item.food.images[0]} alt="" className="w-full h-full object-cover" />
                      )}
                    </div>
                    <div className="flex-1">
                      <p className="font-bold text-slate-900 dark:text-white">{item.food.name}</p>
                      <p className="text-xs text-slate-400 font-bold">Qty: {item.quantity}</p>
                    </div>
                    <p className="font-black text-slate-900 dark:text-white">₹{item.totalPrice}</p>
                  </div>
                ))}
                {order.items.length > 3 && (
                  <div className="px-6 py-3 flex items-center gap-2 text-xs font-black uppercase tracking-widest text-primary">
                    <ChevronRight size={14} /> +{order.items.length - 3} more items
                  </div>
                )}
              </div>
            )}
            
            <div className="p-6 border-t border-slate-50 dark:border-slate-900 flex justify-end">
               <Link 
                 to={`/orders/${order.id}`}
                 className="flex items-center gap-2 bg-slate-900 hover:bg-slate-800 dark:bg-white dark:hover:bg-slate-100 text-white dark:text-slate-900 px-6 py-2.5 rounded-xl font-black text-sm transition-all shadow-md hover:shadow-lg hover:-translate-y-0.5"
               >
                 Track Order <ChevronRight size={16} />
               </Link>
            </div>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  )
}
