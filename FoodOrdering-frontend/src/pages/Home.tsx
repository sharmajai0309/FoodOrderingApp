import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/axios'
import { useQuery } from '@tanstack/react-query'
import { Search, MapPin, Star, Clock, Flame, ChevronRight, Zap, TrendingUp, Award, Store, ShoppingBag, Truck, CheckCircle, Smartphone, Heart } from 'lucide-react'
import { motion, useMotionValue, useTransform, animate, AnimatePresence } from 'framer-motion'
import { AppCard } from '../components/ui/AppCard'
import { AppButton } from '../components/ui/AppButton'
import { RatingBadge } from '../components/ui/RatingBadge'

// Animated counter hook
function useCounter(target: number, duration = 2) {
  const count = useMotionValue(0)
  const rounded = useTransform(count, (v) => Math.round(v).toLocaleString())
  useEffect(() => {
    const controls = animate(count, target, { duration, ease: 'easeOut' })
    return controls.stop
  }, [target, count, duration])
  return rounded
}

interface Restaurant {
  id: number
  title: string
  name: string
  description: string
  images: string[]
  open: boolean
  cusineType: string
}

const CATEGORIES = [
  { label: 'All', emoji: '🍽️', filter: '' },
  { label: 'Pizza', emoji: '🍕', filter: 'pizza' },
  { label: 'Biryani', emoji: '🍛', filter: 'biryani' },
  { label: 'Burgers', emoji: '🍔', filter: 'burger' },
  { label: 'Chinese', emoji: '🥢', filter: 'chinese' },
  { label: 'North Indian', emoji: '🧆', filter: 'north indian' },
  { label: 'Desserts', emoji: '🍰', filter: 'dessert' },
  { label: 'Beverages', emoji: '🧃', filter: 'beverage' },
]

function SkeletonCard() {
  return (
    <div className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-100 dark:border-slate-800 overflow-hidden min-w-[280px]">
      <div className="h-52 bg-slate-100 dark:bg-slate-800 animate-pulse shimmer" />
      <div className="p-5 space-y-3">
        <div className="h-5 bg-slate-100 dark:bg-slate-800 rounded-xl w-3/4 animate-pulse" />
        <div className="h-4 bg-slate-100 dark:bg-slate-800 rounded-xl w-1/2 animate-pulse" />
        <div className="h-px bg-slate-50 dark:bg-slate-800" />
        <div className="flex gap-3">
          <div className="h-4 bg-slate-100 dark:bg-slate-800 rounded-xl w-20 animate-pulse" />
          <div className="h-4 bg-slate-100 dark:bg-slate-800 rounded-xl w-16 animate-pulse" />
        </div>
      </div>
    </div>
  )
}

function RestaurantCard({ restaurant, index, className = "" }: { restaurant: Restaurant, index: number, className?: string }) {
  const [isFavorite, setIsFavorite] = useState(false);

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ delay: Math.min(index * 0.05, 0.4) }}
      className={className}
    >
      <Link to={`/restaurant/${restaurant.id}`} className="block group">
        <AppCard noPadding className="overflow-hidden h-full flex flex-col border-none shadow-sm hover:shadow-xl transition-all duration-500">
          {/* Image Container */}
          <div className="relative h-48 w-full overflow-hidden bg-slate-100 dark:bg-slate-800">
            {restaurant.images?.[0] ? (
              <img
                src={restaurant.images[0]}
                alt={restaurant.name}
                className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                loading="lazy"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center text-3xl">🍲</div>
            )}
            
            {/* Top Badges */}
            <div className="absolute top-3 left-3 right-3 flex justify-between items-start">
              {!restaurant.open && (
                <div className="bg-black/60 backdrop-blur-md text-white text-[10px] font-black uppercase tracking-wider px-2.5 py-1 rounded-lg">
                  Closed
                </div>
              )}
              
              <motion.button
                whileTap={{ scale: 0.8 }}
                onClick={(e) => {
                  e.preventDefault();
                  setIsFavorite(!isFavorite);
                }}
                className={`p-2 rounded-full backdrop-blur-md border border-white/20 shadow-lg transition-all ${isFavorite ? 'bg-rose-500 text-white' : 'bg-white/70 text-slate-400 hover:text-rose-500'}`}
              >
                <Heart size={16} fill={isFavorite ? 'currentColor' : 'none'} />
              </motion.button>
            </div>

            {/* Bottom Gradient Overlay */}
            <div className="absolute inset-x-0 bottom-0 h-1/2 bg-gradient-to-t from-black/20 to-transparent" />
          </div>

          {/* Details Container */}
          <div className="p-4 flex flex-col flex-1">
            <div className="flex justify-between items-start gap-2 mb-1">
              <h3 className="font-black text-slate-900 dark:text-white text-lg leading-tight group-hover:text-primary transition-colors line-clamp-1">
                {restaurant.name}
              </h3>
              <RatingBadge rating={4.8} count={120} variant="white" className="shrink-0" />
            </div>

            <p className="text-sm text-slate-500 dark:text-slate-400 font-medium line-clamp-1 mb-4">
              {restaurant.cusineType || restaurant.description || 'Global Fusion • Bistro'}
            </p>

            <div className="mt-auto pt-3 border-t border-slate-50 dark:border-slate-800/50 flex items-center justify-between">
              <div className="flex items-center gap-1.5 text-xs text-slate-400 font-bold">
                <Clock size={14} className="text-primary/50" />
                <span>25–35 min</span>
              </div>
              <div className="text-xs font-black text-slate-900 dark:text-white bg-slate-50 dark:bg-slate-800 px-3 py-1 rounded-lg">
                ₹{restaurant.id % 2 === 0 ? '300' : '500'} for two
              </div>
            </div>
          </div>
        </AppCard>
      </Link>
    </motion.div>
  )
}

function HorizontalSection({ title, subtitle, icon: Icon, restaurants, isLoading }: { title: string, subtitle: string, icon: any, restaurants: Restaurant[], isLoading: boolean }) {
  if (!isLoading && (!restaurants || restaurants.length === 0)) return null;

  return (
    <section className="pt-8">
      <div className="flex items-end justify-between mb-8 px-2">
        <div className="space-y-1">
          <div className="flex items-center gap-2 mb-1">
             <div className="w-1.5 h-6 bg-primary rounded-full shadow-[0_0_12px_rgba(255,122,47,0.5)]" />
             <h2 className="text-2xl md:text-3xl font-black text-slate-900 dark:text-white tracking-tight">{title}</h2>
          </div>
          <p className="text-sm text-slate-500 font-medium pl-3.5">{subtitle}</p>
        </div>
        <AppButton variant="ghost" className="text-xs uppercase tracking-widest gap-2">
          Explore <ChevronRight size={14} />
        </AppButton>
      </div>

      <div className="flex gap-6 overflow-x-auto pb-8 scrollbar-hide snap-x px-2 pt-2">
        {isLoading
          ? Array.from({ length: 4 }).map((_, i) => <div key={i} className="min-w-[300px] snap-start"><SkeletonCard /></div>)
          : restaurants.map((r, i) => (
              <RestaurantCard key={r.id} restaurant={r} index={i} className="min-w-[300px] snap-start" />
            ))
        }
      </div>
    </section>
  )
}

export default function Home() {
  const [search, setSearch] = useState('')
  const [searchQuery, setSearchQuery] = useState('')
  const [activeCategory, setActiveCategory] = useState('')

  const { data, isLoading, refetch } = useQuery({
    queryKey: ['restaurants', searchQuery],
    queryFn: async () => {
      if (searchQuery) {
        const res = await api.get(`/api/customer/restaurants/search/${searchQuery}`)
        return res.data.data as Restaurant[]
      }
      const res = await api.get('/api/customer/restaurants/allRestaurants?pageNo=0&pageSize=30')
      return (res.data.Restaurants || res.data.content || res.data || []) as Restaurant[]
    },
  })

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setSearchQuery(search)
    refetch()
  }

  // Client-side category filter
  const filteredData = activeCategory
    ? data?.filter(r => {
        const ctype = (r.cusineType || r.description || '').toLowerCase()
        return ctype.includes(activeCategory.toLowerCase())
      })
    : data

  // Mock derived lists from loaded data
  const bestInArea = data ? [...data].sort((a, b) => b.id - a.id).slice(0, 5) : []
  const newBrands = data ? [...data].filter(r => r.title?.length % 2 === 0).slice(0, 5) : []
  const recentlyOpened = data ? [...data].sort((a, b) => a.id - b.id).slice(0, 5) : []

  const isBrowsingAll = !searchQuery && !activeCategory

  const restaurantCount = useCounter(500)
  const orderCount = useCounter(50000)
  const cityCount = useCounter(30)

  return (
    <div className="relative space-y-12 pb-16 min-h-screen">
      {/* ── Page Background ── */}
      <div 
        className="fixed inset-0 z-[-1] pointer-events-none opacity-[0.4] dark:opacity-[0.1]" 
        style={{ 
          backgroundImage: "url('https://images.unsplash.com/photo-1606787366850-de6330128bfc?q=80&w=2070&auto=format&fit=crop')", 
          backgroundSize: 'cover', 
          backgroundAttachment: 'fixed',
          backgroundPosition: 'center' 
        }} 
      />

      {/* ── Hero ── */}
      <section className="relative w-full rounded-[3rem] overflow-hidden min-h-[520px] flex items-center shadow-2xl bg-white dark:bg-slate-950">
        {/* Background Image with Overlay */}
        <div className="absolute inset-0 z-0">
          <img
            src="https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=2070&auto=format&fit=crop"
            alt="Hero background"
            className="w-full h-full object-cover opacity-90 scale-105"
          />
          <div className="absolute inset-0 bg-linear-to-r from-slate-950/95 via-slate-900/60 to-transparent" />
          <div className="absolute inset-0 bg-linear-to-t from-slate-950/50 via-transparent to-transparent" />
        </div>

        <div className="relative z-10 px-8 md:px-20 py-20 max-w-3xl">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="inline-flex items-center gap-2 bg-white/10 backdrop-blur-xl border border-white/20 text-white text-[10px] font-black uppercase tracking-[0.2em] px-5 py-2.5 rounded-full mb-8 shadow-2xl"
          >
            <div className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
            Reliable Delivery · Guaranteed Fresh
          </motion.div>

          <motion.h1
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1, duration: 0.8 }}
            className="text-6xl md:text-8xl font-black text-white mb-6 leading-[0.95] tracking-tighter"
          >
            Taste the <br />
            <span className="text-gradient">Extraordinary.</span>
          </motion.h1>

          <motion.p
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, duration: 0.8 }}
            className="text-lg md:text-xl text-slate-300 mb-12 max-w-lg font-medium leading-relaxed opacity-90"
          >
            Expertly curated flavors from the trendiest spots in your city, delivered with unmatched precision.
          </motion.p>

          <motion.form
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3, duration: 0.8 }}
            onSubmit={handleSearch}
            className="flex flex-col sm:flex-row gap-3 bg-white/5 backdrop-blur-2xl p-2.5 rounded-[2rem] border border-white/10 shadow-3xl max-w-xl group focus-within:border-primary/50 transition-all duration-500"
          >
            <div className="flex-1 flex items-center gap-4 pl-4 py-2">
              <Search size={22} className="text-primary group-focus-within:scale-110 transition-transform" />
              <input
                type="text"
                className="flex-1 border-none bg-transparent focus:ring-0 text-white text-base placeholder:text-slate-400 outline-none font-medium"
                placeholder="Find your next favorite meal..."
                value={search}
                onChange={e => {
                  setSearch(e.target.value)
                  if (e.target.value === '') { setSearchQuery(''); setActiveCategory('') }
                }}
              />
            </div>
            <AppButton type="submit" className="px-10 h-14">
              Explore
            </AppButton>
          </motion.form>
        </div>
      </section>

      {/* ── Animated Stats Strip ── */}
      <motion.section
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        className="grid grid-cols-3 gap-4"
      >
        {[
          { value: restaurantCount, suffix: '+', label: 'Restaurants', emoji: '🏪' },
          { value: orderCount,      suffix: '+', label: 'Orders Delivered', emoji: '🛵' },
          { value: cityCount,       suffix: '',  label: 'Cities Covered', emoji: '🏙️' },
        ].map(({ value, suffix, label, emoji }, i) => (
          <motion.div
            key={label}
            whileHover={{ scale: 1.03, y: -3 }}
            transition={{ type: 'spring', stiffness: 400, damping: 20 }}
            className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-100 dark:border-slate-800 p-6 text-center shadow-sm hover:shadow-lg transition-shadow"
          >
            <div className="text-3xl mb-2">{emoji}</div>
            <div className="flex items-end justify-center gap-0.5">
              <motion.span className="text-3xl font-black text-slate-900 dark:text-white tabular-nums">{value}</motion.span>
              <span className="text-xl font-black text-primary mb-0.5">{suffix}</span>
            </div>
            <p className="text-sm text-slate-500 dark:text-slate-400 font-semibold mt-1">{label}</p>
          </motion.div>
        ))}
      </motion.section>

      {/* ── Cuisine Spotlight ── */}
      <motion.section
        initial={{ opacity: 0 }}
        whileInView={{ opacity: 1 }}
        viewport={{ once: true }}
      >
        <div className="flex items-center justify-between mb-8 px-2">
           <div className="space-y-1">
            <h2 className="text-2xl md:text-3xl font-black text-slate-900 dark:text-white tracking-tight italic">Spotlight <span className="text-primary not-italic">Cuisines</span></h2>
            <p className="text-sm text-slate-500 font-medium">Handpicked flavors from our top kitchen</p>
          </div>
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-6">
          {[
            { label: 'North Indian',  emoji: '🧆', color: 'from-orange-500/80 to-red-600/80',    img: 'https://images.unsplash.com/photo-1546833998-877b37c2e5c6?w=400&auto=format&fit=crop' },
            { label: 'Pizza',         emoji: '🍕', color: 'from-amber-400/80 to-orange-600/80', img: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&auto=format&fit=crop' },
            { label: 'Biryani',       emoji: '🍛', color: 'from-emerald-400/80 to-teal-600/80', img: 'https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=400&auto=format&fit=crop' },
            { label: 'Street Food',   emoji: '🌮', color: 'from-pink-500/80 to-rose-600/80',     img: 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=400&auto=format&fit=crop' },
          ].map(({ label, emoji, color, img }, i) => (
            <motion.button
              key={label}
              onClick={() => { setActiveCategory(label.toLowerCase()); setSearchQuery(''); setSearch('') }}
              whileHover={{ scale: 1.05, y: -4 }}
              whileTap={{ scale: 0.97 }}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.08 }}
              className="relative rounded-[2.5rem] overflow-hidden h-44 group cursor-pointer border-0 p-0 shadow-lg"
            >
              <img src={img} alt={label} className="absolute inset-0 w-full h-full object-cover group-hover:scale-110 transition-transform duration-700 font-black" />
              <div className={`absolute inset-0 bg-linear-to-t ${color} transition-opacity duration-500 group-hover:opacity-90`} />
              <div className="absolute inset-0 flex flex-col items-center justify-center p-4">
                <div className="w-12 h-12 bg-white/20 backdrop-blur-md rounded-2xl flex items-center justify-center text-2xl mb-2 border border-white/30 transform -rotate-12 group-hover:rotate-0 transition-transform">
                  {emoji}
                </div>
                <span className="text-white font-black text-sm tracking-tight drop-shadow-md text-center">{label}</span>
              </div>
            </motion.button>
          ))}
        </div>
      </motion.section>

      {/* ── Categories ── */}
      <section className="-mt-6">
        <div className="flex gap-3 overflow-x-auto pb-4 scrollbar-hide py-1 px-4 -mx-4">
          {CATEGORIES.map(cat => {
            const isActive = activeCategory === cat.filter;
            return (
              <motion.button
                key={cat.label}
                whileTap={{ scale: 0.95 }}
                onClick={() => setActiveCategory(cat.filter)}
                className={`flex flex-col items-center gap-2 min-w-[80px] transition-all ${isActive ? 'scale-110' : 'opacity-70 hover:opacity-100'}`}
              >
                <div className={`w-16 h-16 rounded-2xl flex items-center justify-center text-3xl shadow-sm border transition-all duration-300 ${
                  isActive 
                    ? 'bg-primary border-primary text-white shadow-primary/20 -translate-y-1' 
                    : 'bg-white dark:bg-slate-900 border-slate-100 dark:border-slate-800 text-slate-600'
                }`}>
                  {cat.emoji}
                </div>
                <span className={`text-[11px] font-black uppercase tracking-wider ${isActive ? 'text-primary' : 'text-slate-500'}`}>
                  {cat.label}
                </span>
                {isActive && (
                  <motion.div layoutId="activeCat" className="w-1 h-1 rounded-full bg-primary" />
                )}
              </motion.button>
            )
          })}
        </div>
      </section>

      {/* ── Dynamic Horizontal Sections (Only show when not searching or filtering) ── */}
      {isBrowsingAll && (
        <div className="space-y-4">
          <HorizontalSection 
            title="Best in your area" 
            subtitle="Top rated spots loved by people near you"
            icon={Award}
            restaurants={bestInArea}
            isLoading={isLoading}
          />
          
          <HorizontalSection 
            title="Newly Opened" 
            subtitle="Fresh arrivals in town"
            icon={TrendingUp}
            restaurants={recentlyOpened}
            isLoading={isLoading}
          />

          <HorizontalSection 
            title="Top Brands" 
            subtitle="Your favorite chains and premium spots"
            icon={Store}
            restaurants={newBrands}
            isLoading={isLoading}
          />
        </div>
      )}

      {/* ── All Restaurant List / Search Results ── */}
      <section className="pt-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-black text-slate-900 dark:text-white tracking-tight">
              {searchQuery ? `Search Results for "${searchQuery}"` : activeCategory ? `${CATEGORIES.find(c=>c.filter===activeCategory)?.label} near you` : 'All Restaurants'}
            </h2>
            {!isLoading && (
              <p className="text-slate-500 font-medium mt-1 flex items-center gap-1.5 text-sm">
                <Clock size={14} /> Fast delivery · {filteredData?.length ?? 0} places
              </p>
            )}
          </div>
        </div>

        {isLoading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {Array.from({ length: 8 }).map((_, i) => <SkeletonCard key={i} />)}
          </div>
        ) : !filteredData?.length ? (
          <div className="text-center py-20 bg-white dark:bg-slate-900 rounded-3xl border border-slate-100 dark:border-slate-800">
            <div className="w-20 h-20 mx-auto bg-slate-100 dark:bg-slate-800 rounded-full flex items-center justify-center mb-4 text-3xl">
              🍽️
            </div>
            <h3 className="text-xl font-black mb-2">No restaurants found</h3>
            <p className="text-slate-500 font-medium max-w-sm mx-auto">
              {searchQuery ? `We couldn't find "${searchQuery}". Try a different search.` : "No restaurants match this category yet."}
            </p>
            <button onClick={() => { setSearch(''); setSearchQuery(''); setActiveCategory('') }} className="mt-6 btn-primary">
              Show all restaurants
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredData.map((restaurant, index) => (
              <RestaurantCard key={restaurant.id} restaurant={restaurant} index={index} />
            ))}
          </div>
        )}
      </section>

      {/* ── How It Works ── */}
      {!searchQuery && !activeCategory && (
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="py-4"
        >
          <div className="text-center mb-10">
            <h2 className="text-2xl md:text-3xl font-black text-slate-900 dark:text-white tracking-tight">How it works</h2>
            <p className="text-slate-500 font-medium mt-2">3 simple steps to your next meal</p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[
              { step: '01', icon: ShoppingBag, title: 'Browse & Choose', desc: 'Explore hundreds of restaurants and pick your favourite dishes.', color: 'text-primary', bg: 'bg-primary/10' },
              { step: '02', icon: Truck,       title: 'We Deliver Fast', desc: 'Our delivery partners pick up your order and bring it hot to your door.', color: 'text-emerald-500', bg: 'bg-emerald-50 dark:bg-emerald-500/10' },
              { step: '03', icon: CheckCircle, title: 'Enjoy Your Meal', desc: 'Sit back, relax and enjoy a restaurant-quality meal at home.', color: 'text-purple-500', bg: 'bg-purple-50 dark:bg-purple-500/10' },
            ].map(({ step, icon: Icon, title, desc, color, bg }, i) => (
              <motion.div
                key={step}
                initial={{ opacity: 0, y: 24 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.12 }}
                whileHover={{ y: -6 }}
                className="bg-white dark:bg-slate-900 rounded-3xl border border-slate-100 dark:border-slate-800 p-8 text-center hover:shadow-xl transition-all"
              >
                <div className={`w-14 h-14 ${bg} rounded-2xl flex items-center justify-center mx-auto mb-5`}>
                  <Icon size={26} className={color} />
                </div>
                <span className="text-xs font-black text-slate-300 dark:text-slate-600 tracking-[0.2em]">{step}</span>
                <h3 className="text-lg font-black text-slate-900 dark:text-white mt-1 mb-2">{title}</h3>
                <p className="text-sm text-slate-500 dark:text-slate-400 font-medium leading-relaxed">{desc}</p>
              </motion.div>
            ))}
          </div>
        </motion.section>
      )}

      {/* ── Promotional Banner ── */}
      {!searchQuery && !activeCategory && (
        <motion.section
          initial={{ opacity: 0, scale: 0.97 }}
          whileInView={{ opacity: 1, scale: 1 }}
          viewport={{ once: true }}
          className="relative rounded-[2rem] overflow-hidden bg-gradient-to-r from-primary to-orange-400 p-10 md:p-14"
        >
          {/* Animated floating emojis in banner */}
          {['🍕','🍔','🌮'].map((e, i) => (
            <motion.span
              key={e}
              className="absolute text-7xl md:text-8xl opacity-20 pointer-events-none"
              style={{ right: `${8 + i * 18}%`, top: i % 2 === 0 ? '8%' : '40%' }}
              animate={{ y: [0, -10, 0], rotate: [0, i % 2 === 0 ? 10 : -10, 0] }}
              transition={{ duration: 3 + i, repeat: Infinity, ease: 'easeInOut', delay: i * 0.5 }}
            >{e}</motion.span>
          ))}
          <div className="relative max-w-lg">
            <p className="text-white/80 font-bold text-sm mb-3 uppercase tracking-wider">Limited Time Offer</p>
            <h2 className="text-3xl md:text-4xl font-black text-white mb-4 leading-tight">
              Get 20% off your first order! 🎉
            </h2>
            <p className="text-white/80 font-medium mb-8 text-lg">
              Use code <span className="font-black text-white bg-white/20 px-2 py-0.5 rounded-lg">CRAVEFIRST</span> at checkout.
            </p>
            <Link to="/" className="inline-flex items-center gap-2 bg-white text-primary font-black px-8 py-3.5 rounded-2xl shadow-xl hover:shadow-2xl hover:scale-105 transition-all">
              Order Now <ChevronRight size={18} />
            </Link>
          </div>
        </motion.section>
      )}

      {/* ── App Download Banner ── */}
      {!searchQuery && !activeCategory && (
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="bg-slate-900 dark:bg-slate-950 rounded-[2rem] p-10 md:p-14 flex flex-col md:flex-row items-center justify-between gap-8"
        >
          <div>
            <div className="flex items-center gap-2 mb-3">
              <Smartphone size={18} className="text-primary" />
              <span className="text-primary font-bold text-sm uppercase tracking-widest">Mobile App</span>
            </div>
            <h2 className="text-2xl md:text-3xl font-black text-white mb-3 leading-tight">
              Order faster on the app. <span className="text-primary">Always.</span>
            </h2>
            <p className="text-slate-400 font-medium max-w-sm">
              Track your order live, get exclusive app-only deals, and reorder your favourites in one tap.
            </p>
          </div>
          <div className="flex gap-4 shrink-0">
            {[
              { store: 'App Store', icon: '🍎', sub: 'Download on the' },
              { store: 'Google Play', icon: '▶', sub: 'Get it on' },
            ].map(({ store, icon, sub }) => (
              <motion.button
                key={store}
                whileHover={{ scale: 1.05, y: -2 }}
                whileTap={{ scale: 0.97 }}
                className="flex items-center gap-3 bg-white/10 hover:bg-white/15 border border-white/10 text-white px-6 py-3.5 rounded-2xl transition-all"
              >
                <span className="text-2xl">{icon}</span>
                <div className="text-left">
                  <p className="text-white/60 text-[10px] font-semibold">{sub}</p>
                  <p className="font-black text-sm">{store}</p>
                </div>
              </motion.button>
            ))}
          </div>
        </motion.section>
      )}
    </div>
  )
}
