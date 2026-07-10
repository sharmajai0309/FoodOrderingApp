import { useEffect } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import { CheckCircle2, XCircle, Package, Home } from 'lucide-react'

export default function PaymentCallback() {
  const [searchParams] = useSearchParams()
  const queryClient = useQueryClient()
  const orderId = searchParams.get('orderId')
  const isSuccess = window.location.pathname.includes('/payment/success')

  useEffect(() => {
    // The backend payment controller handles status update via redirect,
    // but we may want to invalidate orders cache
    queryClient.invalidateQueries({ queryKey: ['orders'] })
    queryClient.invalidateQueries({ queryKey: ['cart'] })
  }, [queryClient])

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-950 px-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className={`bg-white dark:bg-slate-900 rounded-[3rem] p-16 max-w-lg w-full text-center border shadow-2xl ${
          isSuccess ? 'border-emerald-200 dark:border-emerald-900' : 'border-rose-200 dark:border-rose-900'
        }`}
      >
        {isSuccess ? (
          <>
            <div className="w-24 h-24 rounded-full bg-emerald-500/10 flex items-center justify-center mx-auto mb-8 text-emerald-500">
              <CheckCircle2 size={56} />
            </div>
            <h1 className="text-4xl font-black text-slate-900 dark:text-white tracking-tight mb-4">Payment Successful!</h1>
            <p className="text-slate-500 font-medium mb-2">Your order #{orderId} has been confirmed.</p>
            <p className="text-slate-400 text-sm mb-12">You'll receive your delicious meal shortly. Sit back and relax!</p>
          </>
        ) : (
          <>
            <div className="w-24 h-24 rounded-full bg-rose-500/10 flex items-center justify-center mx-auto mb-8 text-rose-500">
              <XCircle size={56} />
            </div>
            <h1 className="text-4xl font-black text-slate-900 dark:text-white tracking-tight mb-4">Payment Cancelled</h1>
            <p className="text-slate-500 font-medium mb-2">Your order #{orderId} could not be completed.</p>
            <p className="text-slate-400 text-sm mb-12">No worries — your cart is safe. You can retry the payment anytime.</p>
          </>
        )}

        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link
            to="/orders"
            className="flex items-center justify-center gap-2 bg-primary text-white px-8 py-4 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all"
          >
            <Package size={16} /> My Orders
          </Link>
          <Link
            to="/"
            className="flex items-center justify-center gap-2 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 px-8 py-4 rounded-2xl font-black uppercase tracking-widest text-xs hover:bg-slate-200 dark:hover:bg-slate-700 transition-all"
          >
            <Home size={16} /> Back Home
          </Link>
        </div>
      </motion.div>
    </div>
  )
}
