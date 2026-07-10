import { useAuthStore } from '../store/useAuthStore'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { api } from '../api/axios'
import { motion, AnimatePresence } from 'framer-motion'
import { Trash2, ShoppingBag, ArrowRight, MapPin, CreditCard, Clock, ChevronLeft } from 'lucide-react'
import { toast } from 'react-toastify'
import { useNavigate } from 'react-router-dom'
import { useState } from 'react'

import { AppCard } from '../components/ui/AppCard'
import { AppButton } from '../components/ui/AppButton'
import { QuantitySelector } from '../components/ui/QuantitySelector'

export default function Cart() {
  const { user } = useAuthStore()
  const queryClient = useQueryClient()
  const navigate = useNavigate()
  const [showCheckout, setShowCheckout] = useState(false)
  const [address, setAddress] = useState({
    street: '',
    city: '',
    zipCode: '',
    country: 'India'
  })

  const { data: cartData, isLoading } = useQuery({
    queryKey: ['cart', user?.id],
    queryFn: async () => {
      if (!user?.id) return null
      const res = await api.get(`/api/Customer/Cart/user/${user.id}`)
      return res.data.data
    },
    enabled: !!user?.id,
    staleTime: 0
  })

  const updateQuantityMutation = useMutation({
    mutationFn: async ({ itemId, quantity }: { itemId: number, quantity: number }) => {
      return api.put('/api/Customer/Cart/updateCartItem', { cartItemId: itemId, quantity })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart', user?.id] })
    }
  })

  const removeMutation = useMutation({
    mutationFn: async (itemId: number) => {
      return api.delete(`/api/Customer/Cart/delete/${itemId}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] })
      toast.success('Item removed')
    }
  })

  const checkoutMutation = useMutation({
    mutationFn: async () => {
      if (!cartData?.items?.length) return
      const res = await api.post('/v1/api/customer/order/create', {
        restaurantId: cartData.items[0].food.restaurant.id,
        deliveryAddress: address
      })
      return res.data.data
    },
    onSuccess: (data) => {
      toast.success('Order placed successfully! 🚀')
      queryClient.invalidateQueries({ queryKey: ['cart'] })
      if (data.paymentUrl) {
        window.location.href = data.paymentUrl
      } else {
        navigate('/orders')
      }
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Failed to place order')
    }
  })

  if (!user) return (
    <div className="max-w-md mx-auto text-center py-32 px-6">
      <div className="w-24 h-24 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-8 text-primary animate-float">
          <ShoppingBag size={48} />
      </div>
      <h2 className="text-3xl font-black mb-4">Login to order</h2>
      <p className="text-slate-500 mb-10 font-medium">Please login to access your selection and checkout.</p>
      <AppButton onClick={() => navigate('/auth/login')} className="w-full">Login Now</AppButton>
    </div>
  )

  if (isLoading) return (
    <div className="max-w-4xl mx-auto py-10 px-6 space-y-8 animate-pulse">
      <div className="h-10 w-48 bg-slate-100 dark:bg-slate-900 rounded-2xl" />
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
        <div className="lg:col-span-2 space-y-6">
          {[1,2].map(i => <div key={i} className="h-32 bg-slate-100 dark:bg-slate-900 rounded-3xl" />)}
        </div>
        <div className="h-64 bg-slate-100 dark:bg-slate-900 rounded-[2.5rem]" />
      </div>
    </div>
  )

  const items = cartData?.items || []
  const total = cartData?.total || 0

  return (
    <div className="max-w-4xl mx-auto pb-32 px-6">
      {/* Header */}
      <div className="flex items-center gap-4 py-8">
        <button onClick={() => navigate(-1)} className="w-10 h-10 bg-white dark:bg-slate-900 rounded-full flex items-center justify-center shadow-sm border border-slate-100 dark:border-slate-800">
           <ChevronLeft size={20} />
        </button>
        <h1 className="text-2xl font-black tracking-tight text-slate-900 dark:text-white uppercase italic">
          Your <span className="text-primary not-italic">Bag</span>
        </h1>
      </div>

      {items.length === 0 ? (
        <div className="text-center py-32">
          <div className="w-24 h-24 bg-slate-50 dark:bg-slate-900 rounded-full flex items-center justify-center mx-auto mb-8 text-slate-200 animate-float">
            <ShoppingBag size={48} strokeWidth={1} />
          </div>
          <h2 className="text-3xl font-black mb-4 text-slate-900 dark:text-white uppercase">Bag is empty</h2>
          <p className="text-slate-400 mb-10 max-w-xs mx-auto font-medium">Time to fill it with some delicious goodness from our top chefs!</p>
          <AppButton onClick={() => navigate('/')} className="px-10">Explore Menu</AppButton>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-10 items-start">
          <div className="lg:col-span-2 space-y-6">
            <AnimatePresence mode="popLayout">
              {items.map((item: any, idx: number) => (
                <motion.div 
                  key={item.id}
                  layout
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, scale: 0.95 }}
                  transition={{ delay: idx * 0.05 }}
                >
                  <AppCard noPadding className="group flex gap-4 p-4 items-center bg-white dark:bg-slate-900 border-slate-50 dark:border-slate-800/30 hover:shadow-2xl transition-all duration-500">
                    <div className="w-24 h-24 rounded-2xl bg-slate-50 dark:bg-slate-800 overflow-hidden shrink-0 group-hover:scale-105 transition-transform duration-500">
                      {item.food?.images?.[0] ? (
                        <img src={item.food.images[0]} alt="" className="w-full h-full object-cover" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-slate-200 text-3xl">🍽️</div>
                      )}
                    </div>
                    
                    <div className="flex-1 min-w-0">
                      <div className="flex justify-between items-start mb-1">
                         <h3 className="font-black text-slate-900 dark:text-white italic truncate pr-2">{item.food.name}</h3>
                         <button 
                          onClick={() => removeMutation.mutate(item.id)}
                          className="text-slate-300 hover:text-red-500 transition-colors p-1"
                         >
                           <Trash2 size={16} />
                         </button>
                      </div>
                      
                      <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-1 mb-4">
                        <MapPin size={10} className="text-primary/50" /> {item.food?.restaurant?.name || 'Kitchen'}
                      </p>

                      <div className="flex justify-between items-center">
                         <QuantitySelector 
                           quantity={item.quantity}
                           onIncrease={() => updateQuantityMutation.mutate({ itemId: item.id, quantity: item.quantity + 1 })}
                           onDecrease={() => updateQuantityMutation.mutate({ itemId: item.id, quantity: Math.max(1, item.quantity - 1) })}
                         />
                         <span className="font-black text-lg text-slate-900 dark:text-white tracking-tighter shrink-0">₹{item.totalPrize}</span>
                      </div>
                    </div>
                  </AppCard>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>

          {/* Checkout Section */}
          <div className="sticky top-10 space-y-6">
            <div className="bg-slate-950 text-white p-8 rounded-[2.5rem] shadow-2xl relative overflow-hidden premium-shadow">
              <div className="absolute top-0 right-0 w-32 h-32 bg-primary opacity-20 blur-3xl -translate-y-1/2 translate-x-1/2" />
              
              <h3 className="text-[10px] font-black uppercase tracking-[0.3em] mb-8 text-primary">Order Bill</h3>
              
              <div className="space-y-4 mb-8">
                <div className="flex justify-between text-sm font-bold">
                  <span className="opacity-40">Total Amount</span>
                  <span>₹{total}</span>
                </div>
                <div className="flex justify-between text-sm font-bold text-emerald-400">
                  <span>Shipping</span>
                  <span className="text-[10px] uppercase font-black tracking-widest bg-emerald-400/10 px-2 py-0.5 rounded-md">Free</span>
                </div>
              </div>
              
              <div className="border-t border-white/10 pt-6 mb-8">
                <div className="flex justify-between items-end">
                  <div>
                    <span className="text-[10px] font-black uppercase tracking-widest opacity-40 block mb-1">To Pay</span>
                    <span className="text-3xl font-black tracking-tighter">₹{total}</span>
                  </div>
                </div>
              </div>

              {!showCheckout ? (
                <AppButton 
                  onClick={() => setShowCheckout(true)}
                  className="w-full h-14"
                >
                  Checkout <ArrowRight size={16} />
                </AppButton>
              ) : (
                <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }} className="space-y-4">
                   <div className="space-y-2">
                      <input 
                        type="text" 
                        placeholder="Delivery Street"
                        className="w-full bg-white/5 border border-white/10 rounded-2xl px-4 py-3 outline-none focus:border-primary text-sm font-medium transition-all"
                        value={address.street}
                        onChange={e => setAddress({...address, street: e.target.value})}
                      />
                      <div className="grid grid-cols-2 gap-2">
                        <input 
                          type="text" 
                          placeholder="City"
                          className="w-full bg-white/5 border border-white/10 rounded-2xl px-4 py-3 outline-none focus:border-primary text-sm font-medium transition-all"
                          value={address.city}
                          onChange={e => setAddress({...address, city: e.target.value})}
                        />
                        <input 
                          type="text" 
                          placeholder="ZIP"
                          className="w-full bg-white/5 border border-white/10 rounded-2xl px-4 py-3 outline-none focus:border-primary text-sm font-medium transition-all"
                          value={address.zipCode}
                          onChange={e => setAddress({...address, zipCode: e.target.value})}
                        />
                      </div>
                   </div>

                   <div className="flex gap-2">
                      <button 
                        onClick={() => setShowCheckout(false)}
                        className="flex-1 text-[10px] uppercase font-black opacity-40 hover:opacity-100 transition-opacity"
                      >
                        Cancel
                      </button>
                      <AppButton 
                        onClick={() => checkoutMutation.mutate()}
                        isLoading={checkoutMutation.isPending}
                        disabled={!address.street || !address.city}
                        className="flex-2 h-12"
                      >
                        <CreditCard size={14} /> Pay
                      </AppButton>
                   </div>
                </motion.div>
              )}
            </div>

            <div className="bg-white dark:bg-slate-900 p-6 rounded-[2rem] border border-slate-100 dark:border-slate-800/50 flex items-center gap-4">
              <div className="w-12 h-12 bg-emerald-500/10 rounded-2xl flex items-center justify-center text-emerald-500">
                <Clock size={20} />
              </div>
              <div>
                <p className="text-[10px] font-black uppercase tracking-widest text-slate-900 dark:text-white">Express</p>
                <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Arrives in 30m</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
