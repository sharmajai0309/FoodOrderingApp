import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { api } from '../api/axios'
import { motion, AnimatePresence } from 'framer-motion'
import { Star, Clock, Info, Plus, Leaf, Snowflake, MapPin, Share2, Heart, ArrowLeft, Search } from 'lucide-react'
import { toast } from 'react-toastify'
import { useState } from 'react'

import { AppCard } from '../components/ui/AppCard'
import { AppButton } from '../components/ui/AppButton'
import { RatingBadge } from '../components/ui/RatingBadge'
import { FoodDetailModal } from '../components/ui/FoodDetailModal'

export default function RestaurantDetail() {
  const { id } = useParams()
  const queryClient = useQueryClient()
  const [filter, setFilter] = useState<'all' | 'veg' | 'seasonal'>('all')
  const [selectedFood, setSelectedFood] = useState<any>(null)
  const [isFavorite, setIsFavorite] = useState(false)

  const { data: restaurant, isLoading } = useQuery({
    queryKey: ['restaurant', id],
    queryFn: async () => {
      const res = await api.get(`/api/customer/restaurants/${id}`)
      return res.data.data
    },
  })

  const { data: foods, isLoading: foodsLoading } = useQuery({
    queryKey: ['restaurant-foods', id, filter],
    queryFn: async () => {
      const isVeg = filter === 'veg' ? 'true' : 'false'
      const isNonVeg = filter === 'all' ? 'false' : 'false'
      const isSeasonal = filter === 'seasonal' ? 'true' : 'false'
      const res = await api.get(
        `/v1/api/Food/restaurants/${id}/foods?isVeg=${isVeg}&isNonVeg=${isNonVeg}&isSeasonal=${isSeasonal}&page=0&size=50`
      )
      const content = res.data.content as Record<string, any>[]
      if (filter === 'veg') return content.filter(f => f.vegetarian || f.isVegetarian)
      if (filter === 'seasonal') return content.filter(f => f.seasonal || f.isSeasonal)
      return content
    },
  })

  const addToCartMutation = useMutation({
    mutationFn: async ({ foodId, quantity = 1 }: { foodId: number, quantity?: number }) => {
      return api.put('/api/Customer/Cart/add-item', {
        foodId,
        quantity,
        ingredients: null
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] })
      setSelectedFood(null)
      toast.success('Added to your bag! 🛍️', {
        position: 'bottom-center',
        autoClose: 2000,
      })
    },
    onError: (error: any) => {
      const backendMsg = error.response?.data?.message
      toast.error(backendMsg || 'Failed to add item. Please try again.')
    }
  })

  if (isLoading) return (
    <div className="max-w-4xl mx-auto space-y-12 py-10 px-4">
      <div className="h-64 bg-slate-100 dark:bg-slate-900 animate-pulse rounded-[3rem] w-full" />
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 pt-10">
        {[1,2,3,4].map(i => <div key={i} className="h-48 bg-slate-100 dark:bg-slate-900 animate-pulse rounded-[2rem]" />)}
      </div>
    </div>
  )
  
  if (!restaurant) return (
    <div className="max-w-md mx-auto text-center py-32 px-4">
       <div className="w-20 h-20 bg-slate-50 dark:bg-slate-800 rounded-full flex items-center justify-center mx-auto mb-8 text-slate-300"><Info size={40} /></div>
       <h2 className="text-3xl font-black mb-4">Restaurant not found</h2>
       <p className="text-slate-500 font-medium mb-10">This establishment might have gone off the grid or is currently undergoing a makeover.</p>
       <AppButton onClick={() => window.history.back()} className="w-full">Go Back</AppButton>
    </div>
  )

  const popularFoods = foods?.slice(0, 4) || [];

  return (
    <div className="relative pb-24 min-h-screen bg-white dark:bg-slate-950">
      {/* ── Mobile Header ── */}
      <header className="relative w-full h-[320px] overflow-hidden">
        <img 
          src={restaurant.images?.[0] || 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=2070&auto=format&fit=crop'} 
          alt={restaurant.name} 
          className="w-full h-full object-cover scale-105" 
        />
        <div className="absolute inset-0 bg-linear-to-b from-black/40 via-transparent to-white dark:to-slate-950" />
        
        {/* Top Controls */}
        <div className="absolute top-6 left-6 right-6 flex justify-between items-center z-10">
          <Link to="/" className="w-10 h-10 bg-white/20 backdrop-blur-xl border border-white/20 rounded-full flex items-center justify-center text-white shadow-xl hover:scale-110 transition-transform">
            <ArrowLeft size={20} />
          </Link>
          <div className="flex gap-3">
            <button className="w-10 h-10 bg-white/20 backdrop-blur-xl border border-white/20 rounded-full flex items-center justify-center text-white shadow-xl hover:scale-110 transition-transform">
              <Share2 size={18} />
            </button>
            <button 
              onClick={() => setIsFavorite(!isFavorite)}
              className={`w-10 h-10 backdrop-blur-xl border border-white/20 rounded-full flex items-center justify-center shadow-xl transition-all hover:scale-110 ${isFavorite ? 'bg-rose-500 text-white' : 'bg-white/20 text-white'}`}
            >
              <Heart size={18} fill={isFavorite ? 'currentColor' : 'none'} />
            </button>
          </div>
        </div>
      </header>

      {/* ── Restaurant Info Card ── */}
      <div className="max-w-4xl mx-auto px-6 -mt-32 relative z-10">
        <AppCard className="p-8 border-slate-100 shadow-2xl shadow-slate-900/10">
          <div className="flex justify-between items-start gap-4 mb-4">
            <div>
              <h1 className="text-3xl md:text-4xl font-black text-slate-900 dark:text-white tracking-tighter mb-2 italic">
                {restaurant.name}
              </h1>
              <div className="flex items-center gap-2 flex-wrap">
                <span className="text-sm font-black text-primary uppercase tracking-widest">{restaurant.cusineType}</span>
                <div className="w-1 h-1 rounded-full bg-slate-300" />
                <span className="text-sm font-medium text-slate-500">{restaurant.openingHours}</span>
              </div>
            </div>
            <RatingBadge rating={4.8} count={240} variant="orange" className="p-2.5 h-auto text-sm" />
          </div>

          <p className="text-slate-500 font-medium mb-6 leading-relaxed line-clamp-2">
            {restaurant.description || "Indulge in a world-class dining experience featuring artisan dishes crafted with the season's finest ingredients."}
          </p>

          <div className="flex items-center gap-8 py-5 border-t border-slate-50 dark:border-slate-800/50">
            <div className="flex flex-col">
              <div className="flex items-center gap-1.5 text-slate-900 dark:text-white font-black text-lg">
                <Clock size={16} className="text-primary" /> 25-35
              </div>
              <span className="text-[10px] uppercase font-black tracking-widest text-slate-400">Delivery Mins</span>
            </div>
            <div className="flex flex-col">
              <div className="flex items-center gap-1.5 text-slate-900 dark:text-white font-black text-lg">
                <MapPin size={16} className="text-primary" /> 1.2
              </div>
              <span className="text-[10px] uppercase font-black tracking-widest text-slate-400">Kilometers</span>
            </div>
            <div className="flex flex-col">
              <div className="flex items-center gap-1.5 text-slate-900 dark:text-white font-black text-lg">
                ₹800
              </div>
              <span className="text-[10px] uppercase font-black tracking-widest text-slate-400">Approx for 2</span>
            </div>
          </div>
        </AppCard>
      </div>

      {/* ── Main Menu ── */}
      <main className="max-w-4xl mx-auto px-6 mt-16 space-y-12">
        {/* Navigation Tabs */}
        <div className="flex gap-2 bg-slate-50 dark:bg-slate-900/50 p-1.5 rounded-3xl border border-slate-100 dark:border-slate-800 shadow-inner">
          {[
            { id: 'all', label: 'Recommended', icon: Star },
            { id: 'veg', label: 'Plant Based', icon: Leaf },
            { id: 'seasonal', label: 'Seasonal', icon: Snowflake }
          ].map((tab: any) => {
            const isActive = filter === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setFilter(tab.id as any)}
                className={`flex-1 flex items-center justify-center gap-2 py-3 rounded-2xl text-[11px] font-black uppercase tracking-[0.15em] transition-all ${
                  isActive 
                    ? 'bg-white dark:bg-slate-800 text-primary shadow-lg' 
                    : 'text-slate-400 hover:text-slate-600'
                }`}
              >
                <tab.icon size={14} className={isActive ? 'text-primary' : 'text-slate-300'} />
                {tab.label}
              </button>
            )
          })}
        </div>

        {/* Section Title */}
        <div className="flex items-center justify-between">
           <h2 className="text-2xl font-black text-slate-900 dark:text-white tracking-tight uppercase">
             Our <span className="text-primary italic">Selection</span>
           </h2>
           <AppButton variant="ghost" className="text-[10px] uppercase tracking-widest py-1 h-auto">
             <Search size={14} />
           </AppButton>
        </div>

        <AnimatePresence mode="wait">
          {foodsLoading ? (
             <div key="loading" className="grid grid-cols-1 md:grid-cols-2 gap-8">
               {[1,2,3,4].map(i => <div key={i} className="animate-pulse bg-slate-50 dark:bg-slate-900 h-48 rounded-[2rem]" />)}
             </div>
          ) : !foods || foods.length === 0 ? (
             <motion.div key="empty" initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="py-20 text-center">
                <div className="text-5xl mb-4 opacity-20">🍽️</div>
                <h3 className="text-xl font-black text-slate-900 dark:text-white mb-2">Chef is busy!</h3>
                <p className="text-slate-500 font-medium">No dishes found in this category right now.</p>
             </motion.div>
          ) : (
            <motion.div key="content" initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-8">
              {/* Popular Items Row */}
              {filter === 'all' && popularFoods.length > 0 && (
                <div className="space-y-4">
                  <span className="text-[10px] font-black text-slate-400 uppercase tracking-[0.3em]">Signature Dishes</span>
                  <div className="flex gap-4 overflow-x-auto pb-4 scrollbar-hide -mx-2 px-2">
                    {popularFoods.map((food: any) => (
                      <motion.button
                        key={`pop-${food.id}`}
                        whileTap={{ scale: 0.97 }}
                        onClick={() => setSelectedFood(food)}
                        className="flex-shrink-0 w-64 h-80 rounded-[2.5rem] overflow-hidden relative group shadow-lg"
                      >
                         <img src={food.images?.[0] || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'} className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" alt={food.name} />
                         <div className="absolute inset-0 bg-linear-to-t from-black/80 via-black/20 to-transparent" />
                         <div className="absolute bottom-6 left-6 right-6 text-left">
                            <h4 className="text-white font-black text-lg leading-tight mb-1">{food.name}</h4>
                            <div className="flex justify-between items-center text-white/80">
                               <span className="text-sm font-black tracking-tighter italic">₹{food.price}</span>
                               <RatingBadge rating={4.9} variant="glass" className="text-[9px] py-1" />
                            </div>
                         </div>
                      </motion.button>
                    ))}
                  </div>
                </div>
              )}

              {/* Full Menu Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {foods.map((food: any, idx: number) => (
                  <motion.div
                    key={food.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: idx * 0.05 }}
                  >
                    <AppCard 
                      noPadding 
                      className="group overflow-hidden flex flex-col h-full bg-white dark:bg-slate-900 border-slate-50 dark:border-slate-800/30 hover:border-primary/20 transition-all duration-500"
                    >
                      <button onClick={() => setSelectedFood(food)} className="relative h-44 w-full overflow-hidden text-left cursor-pointer">
                        <img 
                          src={food.images?.[0] || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c'} 
                          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" 
                          alt={food.name} 
                        />
                        <div className="absolute top-4 left-4">
                          {food.isVegetarian && (
                            <div className="w-6 h-6 bg-white/90 backdrop-blur-md rounded-lg flex items-center justify-center border border-emerald-500/20 shadow-lg">
                               <div className="w-2 h-2 rounded-full border border-emerald-500 flex items-center justify-center"><div className="w-1 h-1 bg-emerald-500 rounded-full" /></div>
                            </div>
                          )}
                        </div>
                      </button>

                      <div className="p-6 flex flex-col flex-1">
                        <div className="flex justify-between items-start gap-3 mb-2">
                           <h3 className="text-lg font-black text-slate-900 dark:text-white leading-tight group-hover:text-primary transition-colors italic truncate">{food.name}</h3>
                           <span className="font-black text-slate-900 dark:text-white tracking-tighter">₹{food.price}</span>
                        </div>
                        <p className="text-xs text-slate-400 font-medium line-clamp-2 mb-6 opacity-80">{food.description || 'A masterpiece of fresh ingredients.'}</p>
                        
                        <div className="mt-auto flex items-center justify-between gap-4">
                          <RatingBadge rating={4.8} variant="white" className="p-0 border-none shadow-none text-slate-400" />
                          <AppButton 
                            onClick={() => addToCartMutation.mutate({ foodId: food.id })}
                            isLoading={addToCartMutation.isPending && addToCartMutation.variables?.foodId === food.id}
                            variant="primary" 
                            className="h-10 px-6 text-[10px] uppercase whitespace-nowrap"
                          >
                            Add <Plus size={14} />
                          </AppButton>
                        </div>
                      </div>
                    </AppCard>
                  </motion.div>
                ))}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </main>

      {/* ── Slide-up Food Detail ── */}
      <FoodDetailModal 
        isOpen={!!selectedFood} 
        food={selectedFood} 
        onClose={() => setSelectedFood(null)}
        onAddToCart={(foodId, quantity) => addToCartMutation.mutate({ foodId, quantity })}
        isAdding={addToCartMutation.isPending}
      />
    </div>
  )
}
