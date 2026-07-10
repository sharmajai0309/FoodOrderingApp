import { Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { useActiveOrder } from '../hooks/useActiveOrder'
import { Bike, ChevronRight, X } from 'lucide-react'
import { useState } from 'react'

const STATUS_LABEL: Record<string, { text: string; emoji: string; color: string }> = {
  PAID:             { text: 'Finding a delivery partner',   emoji: '🔍', color: '#f59e0b' },
  ACCEPTED:         { text: 'Restaurant accepted your order', emoji: '✅', color: '#10b981' },
  PREPARING:        { text: 'Preparing your food',           emoji: '👨‍🍳', color: '#f97316' },
  PICKED_UP:        { text: 'Picked up from restaurant',     emoji: '📦', color: '#8b5cf6' },
  OUT_FOR_DELIVERY: { text: 'Out for delivery',              emoji: '🛵', color: '#10b981' },
}

/**
 * Swiggy/Zomato-style floating bottom banner.
 * Shows whenever the logged-in customer has an active order.
 * Disappears when dismissed or order is delivered.
 */
export default function LiveOrderBanner() {
  const { activeOrder } = useActiveOrder()
  const [dismissed, setDismissed] = useState(false)

  if (!activeOrder || dismissed) return null

  const cfg = STATUS_LABEL[activeOrder.orderStatus] ?? {
    text: activeOrder.orderStatus,
    emoji: '🍽️',
    color: '#6b7280',
  }

  return (
    <AnimatePresence>
      <motion.div
        initial={{ y: 100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        exit={{ y: 100, opacity: 0 }}
        transition={{ type: 'spring', damping: 20, stiffness: 300 }}
        className="fixed bottom-6 left-1/2 z-50 w-[calc(100%-2rem)] max-w-lg"
        style={{ transform: 'translateX(-50%)' }}
      >
        <div
          className="relative flex items-center gap-4 px-5 py-4 rounded-2xl shadow-2xl overflow-hidden"
          style={{
            background: 'linear-gradient(135deg, #0f172a, #1e293b)',
            border: `1px solid ${cfg.color}40`,
            boxShadow: `0 8px 32px rgba(0,0,0,0.4), 0 0 0 1px ${cfg.color}20`,
          }}
        >
          {/* Colored left accent */}
          <div className="absolute left-0 top-0 bottom-0 w-1 rounded-l-2xl" style={{ background: cfg.color }} />

          {/* Pulsing icon */}
          <div
            className="w-11 h-11 rounded-xl flex items-center justify-center shrink-0 relative"
            style={{ background: `${cfg.color}18` }}
          >
            <Bike size={20} style={{ color: cfg.color }} />
            {activeOrder.orderStatus === 'OUT_FOR_DELIVERY' && (
              <span
                className="absolute -top-1 -right-1 w-3 h-3 rounded-full"
                style={{ background: cfg.color, animation: 'pulse 1.5s infinite' }}
              />
            )}
          </div>

          {/* Text */}
          <div className="flex-1 min-w-0">
            <p className="text-white font-bold text-sm truncate">
              {cfg.emoji} {cfg.text}
            </p>
            <p className="text-slate-400 text-xs font-medium mt-0.5 truncate">
              Order #{activeOrder.id}
              {activeOrder.restaurantName ? ` · ${activeOrder.restaurantName}` : ''}
            </p>
          </div>

          {/* Track CTA */}
          <Link
            to={`/orders/${activeOrder.id}`}
            className="shrink-0 flex items-center gap-1 px-4 py-2 rounded-xl text-xs font-black transition-all"
            style={{ background: cfg.color, color: 'white' }}
          >
            Track <ChevronRight size={14} />
          </Link>

          {/* Dismiss */}
          <button
            onClick={() => setDismissed(true)}
            className="shrink-0 w-7 h-7 flex items-center justify-center rounded-lg text-slate-500 hover:text-slate-300 hover:bg-white/5 transition-all"
          >
            <X size={14} />
          </button>
        </div>
      </motion.div>
    </AnimatePresence>
  )
}
