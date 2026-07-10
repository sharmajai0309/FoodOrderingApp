import { useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import type { FieldValues } from 'react-hook-form'
import { api } from '../../api/axios'
import { toast } from 'react-toastify'
import { useNavigate } from 'react-router-dom'
import { Plus, X, Pencil, Trash2, Search, Filter, MoreVertical, ExternalLink, UtensilsCrossed } from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'

export default function Restaurants() {
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingRestaurant, setEditingRestaurant] = useState<Record<string, any> | null>(null)
  const [loading, setLoading] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const queryClient = useQueryClient()
  const navigate = useNavigate()
  
  const { register, handleSubmit, reset, setValue } = useForm()

  const { data: restaurants, isLoading } = useQuery({
    queryKey: ['admin-restaurants'],
    queryFn: async () => {
      const res = await api.get('/api/admin/restaurants/restaurant')
      return res.data
    },
  })

  // RestaurantDto shape: { id, title, description, images[] }
  // Client-side filtering on title and description
  const filteredRestaurants = restaurants?.filter((r: Record<string, any>) =>
    r.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    r.description?.toLowerCase().includes(searchTerm.toLowerCase())
  ) || []

  const onSubmit = async (data: FieldValues) => {
    setLoading(true)
    try {
      const payload = {
        name: data.name,
        description: data.description,
        cusineType: data.cusineType,
        address: {
          city: data.city,
          country: "India",
          zipCode: "110001"
        },
        contactInformation: {
          mobile: data.mobile
        },
        openingHours: "Mon-Sun: 10:00 AM - 11:00 PM",
        images: ["https://images.unsplash.com/photo-1517248135467-4c7edcad34c4"]
      }

      if (editingRestaurant) {
        // Update existing restaurant
        await api.put(`/api/admin/restaurants/update/${editingRestaurant.id}`, payload)
        toast.success('Restaurant updated successfully!')
      } else {
        // Create new restaurant  
        await api.post('/api/admin/restaurants/create', payload)
        toast.success('Restaurant onboarding complete!')
      }
      setIsModalOpen(false)
      setEditingRestaurant(null)
      reset()
      queryClient.invalidateQueries({ queryKey: ['admin-restaurants'] })
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Operation failed')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (restaurantId: number, name: string) => {
    if (!confirm(`Delete "${name}"? This cannot be undone.`)) return
    try {
      await api.delete(`/api/admin/restaurants/${restaurantId}`)
      toast.success('Restaurant removed from platform.')
      queryClient.invalidateQueries({ queryKey: ['admin-restaurants'] })
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to delete restaurant')
    }
  }

  const handleEdit = (restaurant: Record<string, any>) => {
    setEditingRestaurant(restaurant)
    setValue('name', restaurant.title || restaurant.name || '')
    setValue('description', restaurant.description || '')
    setValue('cusineType', restaurant.cusineType || '')
    setValue('mobile', restaurant.contactInformation?.mobile || '')
    setValue('city', restaurant.address?.city || '')
    setIsModalOpen(true)
  }

  return (
    <div className="space-y-8 h-full flex flex-col">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 shrink-0">
        <div>
           <h1 className="text-4xl font-black text-slate-900 dark:text-white tracking-tight">Active Portfolio</h1>
           <p className="text-slate-500 font-medium">Manage and monitor all onboarded restaurants.</p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="flex items-center justify-center bg-primary text-white py-3.5 px-8 rounded-2xl hover:bg-primary/90 transition-all shadow-xl shadow-primary/20 font-black uppercase tracking-widest text-xs transform active:scale-95"
        >
          <Plus size={18} className="mr-2" strokeWidth={3} /> Add New Listing
        </button>
      </div>

      <div className="flex items-center gap-4 shrink-0 overflow-x-auto pb-2">
         <div className="relative flex-1 min-w-[300px]">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input 
              type="text" 
              placeholder="Filter by name, cuisine or location..."
              className="w-full pl-12 pr-4 py-3 bg-white dark:bg-slate-950 border border-border rounded-2xl outline-none focus:ring-4 focus:ring-primary/5 focus:border-primary transition-all font-medium text-sm"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
         </div>
         <button className="flex items-center gap-2 bg-white dark:bg-slate-950 border border-border px-5 py-3 rounded-2xl text-xs font-black uppercase tracking-widest text-slate-500 hover:text-primary transition-all shrink-0">
            <Filter size={16} /> Filters
         </button>
      </div>

      <div className="bg-white dark:bg-slate-950 border border-border rounded-[2.5rem] shadow-sm flex-1 overflow-hidden flex flex-col relative">
        <div className="overflow-x-auto h-full custom-scrollbar">
          <table className="w-full text-sm text-left border-separate border-spacing-0">
            <thead className="sticky top-0 z-10">
              <tr className="bg-slate-50 dark:bg-slate-900/50 backdrop-blur-md">
                <th className="px-8 py-5 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-border">Restaurant identity</th>
                <th className="px-8 py-5 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-border">Description</th>
                <th className="px-8 py-5 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-border">Operations</th>
                <th className="px-8 py-5 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-border text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 dark:divide-slate-900">
              {isLoading ? (
                <tr>
                  <td colSpan={4} className="px-8 py-16 text-center">
                     <div className="inline-flex items-center gap-3 text-slate-400 font-bold italic">
                        <div className="w-5 h-5 border-2 border-primary/20 border-t-primary rounded-full animate-spin" />
                        Synchronizing listings...
                     </div>
                  </td>
                </tr>
              ) : filteredRestaurants.length === 0 ? (
                <tr>
                  <td colSpan={4} className="px-8 py-16 text-center text-slate-400 font-bold">No assets found matching criteria.</td>
                </tr>
              ) : (
                filteredRestaurants.map((restaurant: Record<string, any>, idx: number) => (
                  <motion.tr 
                    initial={{ opacity: 0, x: -10 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: idx * 0.03 }}
                    key={restaurant.id} 
                    className="hover:bg-slate-50 dark:hover:bg-slate-900/40 transition-colors group cursor-default"
                  >
                    <td className="px-8 py-5">
                      <div className="flex items-center gap-4">
                        <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 overflow-hidden shrink-0 border border-border/50 shadow-inner group-hover:scale-110 transition-transform flex items-center justify-center">
                          {restaurant.images?.[0]
                            ? <img src={restaurant.images[0]} alt={restaurant.title} className="w-full h-full object-cover" onError={(e) => { (e.target as HTMLImageElement).style.display = 'none' }}/>
                            : <UtensilsCrossed size={22} className="text-slate-300" />}
                        </div>
                         <div>
                            {/* RestaurantDto uses "title" field, not "name" */}
                            <p className="font-black text-slate-900 dark:text-white group-hover:text-primary transition-colors text-base tracking-tight">{restaurant.title}</p>
                            <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mt-1">ID: {String(restaurant.id).padStart(6, '0')}</p>
                         </div>
                       </div>
                     </td>
                     <td className="px-8 py-5">
                        {/* description is the only text field available besides title in RestaurantDto */}
                        <p className="text-xs text-slate-500 font-medium max-w-xs line-clamp-2">
                          {restaurant.description || <span className="text-slate-300 italic">No description</span>}
                        </p>
                     </td>
                     <td className="px-8 py-5">
                       {/* Status not available in RestaurantDto — show as managed */}
                       <span className="inline-flex items-center gap-1.5 text-[10px] font-black uppercase tracking-widest text-blue-500 bg-blue-500/10 px-3 py-1.5 rounded-full border border-blue-500/20">
                         <div className="w-1.5 h-1.5 rounded-full bg-blue-500" /> Listed
                       </span>
                    </td>
                    <td className="px-8 py-5 text-right">
                       <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                         <button 
                           onClick={() => navigate(`/restaurant/${restaurant.id}`)}
                           title="View Restaurant"
                           className="w-9 h-9 flex items-center justify-center rounded-xl bg-white dark:bg-slate-800 border border-border text-slate-500 hover:text-primary hover:border-primary transition-all shadow-sm">
                            <ExternalLink size={16} />
                         </button>
                         <button 
                           onClick={() => handleEdit(restaurant)}
                           title="Edit Restaurant"
                           className="w-9 h-9 flex items-center justify-center rounded-xl bg-white dark:bg-slate-800 border border-border text-slate-500 hover:text-primary hover:border-primary transition-all shadow-sm">
                            <Pencil size={16} />
                         </button>
                         <button 
                           onClick={() => handleDelete(restaurant.id, restaurant.title || restaurant.name)}
                           title="Delete Restaurant"
                           className="w-9 h-9 flex items-center justify-center rounded-xl bg-white dark:bg-slate-800 border border-border text-slate-500 hover:text-rose-500 hover:border-rose-500 transition-all shadow-sm">
                            <Trash2 size={16} />
                         </button>
                       </div>
                       <MoreVertical className="text-slate-300 group-hover:hidden transition-all ml-auto" size={18} />
                     </td>
                  </motion.tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <AnimatePresence>
        {isModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div 
               initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
               onClick={() => setIsModalOpen(false)}
               className="absolute inset-0 bg-slate-900/60 backdrop-blur-xl" 
            />
            
            <motion.div 
              initial={{ opacity: 0, scale: 0.95, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95, y: 20 }}
              className="bg-white dark:bg-slate-950 rounded-[2.5rem] max-w-xl w-full p-10 shadow-2xl relative border border-border overflow-hidden"
            >
              <div className="absolute top-0 right-0 p-8">
                 <button onClick={() => setIsModalOpen(false)} className="w-10 h-10 rounded-2xl bg-slate-100 dark:bg-slate-900 flex items-center justify-center text-slate-500 hover:text-slate-900 dark:hover:text-white transition-all"><X size={20} /></button>
              </div>

              <div className="mb-10 text-center lg:text-left">
                <h2 className="text-3xl font-black text-slate-900 dark:text-white tracking-tight">
                  {editingRestaurant ? 'Edit Restaurant' : 'Onboard Branding'}
                </h2>
                <p className="text-slate-500 font-medium">
                  {editingRestaurant ? 'Update restaurant information.' : 'Add a new restaurant to your digital ecosystem.'}
                </p>
              </div>
              
              <form onSubmit={handleSubmit(onSubmit)} className="grid grid-cols-2 gap-6">
                <div className="col-span-2 space-y-1.5">
                  <label className="text-[10px] font-black uppercase tracking-[0.2em] text-slate-400">Full Trading Name</label>
                  <input {...register('name', { required: true })} className="w-full px-5 py-3.5 bg-slate-50 dark:bg-slate-900/50 border border-border rounded-2xl outline-none focus:ring-4 focus:ring-primary/10 focus:border-primary transition-all font-semibold" placeholder="e.g. Royal Kitchens" />
                </div>
                
                <div className="space-y-1.5">
                  <label className="text-[10px] font-black uppercase tracking-[0.2em] text-slate-400">Cuisine Style</label>
                  <input {...register('cusineType', { required: true })} className="w-full px-5 py-3.5 bg-slate-50 dark:bg-slate-900/50 border border-border rounded-2xl outline-none focus:ring-4 focus:ring-primary/10 focus:border-primary transition-all font-semibold" placeholder="Fine Dining" />
                </div>

                <div className="space-y-1.5">
                  <label className="text-[10px] font-black uppercase tracking-[0.2em] text-slate-400">Operational City</label>
                  <input {...register('city', { required: true })} className="w-full px-5 py-3.5 bg-slate-50 dark:bg-slate-900/50 border border-border rounded-2xl outline-none focus:ring-4 focus:ring-primary/10 focus:border-primary transition-all font-semibold" placeholder="New Delhi" />
                </div>

                <div className="col-span-2 space-y-1.5">
                  <label className="text-[10px] font-black uppercase tracking-[0.2em] text-slate-400">Corporate Contact</label>
                  <input {...register('mobile', { required: true })} className="w-full px-5 py-3.5 bg-slate-50 dark:bg-slate-900/50 border border-border rounded-2xl outline-none focus:ring-4 focus:ring-primary/10 focus:border-primary transition-all font-semibold" placeholder="+91 98765-43210" />
                </div>

                <div className="col-span-2 space-y-1.5">
                  <label className="text-[10px] font-black uppercase tracking-[0.2em] text-slate-400">Brand Story / Mission</label>
                  <textarea {...register('description', { required: true })} className="w-full px-5 py-3.5 bg-slate-50 dark:bg-slate-900/50 border border-border rounded-2xl outline-none focus:ring-4 focus:ring-primary/10 focus:border-primary transition-all font-semibold min-h-[120px]" placeholder="Briefly describe the dining experience..." />
                </div>

                <div className="col-span-2 pt-4 flex gap-4">
                  <button type="button" onClick={() => { setIsModalOpen(false); setEditingRestaurant(null); reset() }} className="flex-1 px-8 py-4 border border-border rounded-2xl font-black uppercase tracking-widest text-[10px] hover:bg-slate-50 dark:hover:bg-slate-900 transition-all">Cancel</button>
                  <button type="submit" disabled={loading} className="flex-1 px-8 py-4 bg-primary text-white rounded-2xl font-black uppercase tracking-widest text-[10px] shadow-xl shadow-primary/20 hover:bg-primary/90 transition-all disabled:opacity-50">
                    {loading ? 'Processing...' : editingRestaurant ? 'Save Changes' : 'Complete Onboarding'}
                  </button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  )
}

