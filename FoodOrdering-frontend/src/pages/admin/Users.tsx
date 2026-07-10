import { useState, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../../api/axios'
import { motion } from 'framer-motion'
import { Search, Shield, User, Mail, MoreHorizontal } from 'lucide-react'

type RoleFilter = 'ALL' | 'CUSTOMER' | 'RESTAURANT_ADMIN' | 'ADMIN'

export default function Users() {
  const [searchTerm, setSearchTerm] = useState('')
  const [roleFilter, setRoleFilter] = useState<RoleFilter>('ALL')

  const { data: users, isLoading } = useQuery({
    queryKey: ['admin-users'],
    queryFn: async () => {
      const res = await api.get('/v1/user/GetAll')
      return res.data
    },
  })

  // Client-side filtering — search by username/email, filter by role
  const filteredUsers = useMemo(() => {
    if (!users) return []
    return users.filter((user: Record<string, any>) => {
      const matchesSearch =
        !searchTerm ||
        user.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.email?.toLowerCase().includes(searchTerm.toLowerCase())
      const matchesRole =
        roleFilter === 'ALL' ||
        user.role === roleFilter
      return matchesSearch && matchesRole
    })
  }, [users, searchTerm, roleFilter])

  const tabs: { label: string; value: RoleFilter }[] = [
    { label: 'All', value: 'ALL' },
    { label: 'Customers', value: 'CUSTOMER' },
    { label: 'Restaurant Admins', value: 'RESTAURANT_ADMIN' },
    { label: 'Platform Admins', value: 'ADMIN' },
  ]

  return (
    <div className="space-y-8 flex flex-col h-full">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
           <h1 className="text-4xl font-black text-slate-900 dark:text-white tracking-tight text-balance">Personnel Directory</h1>
           <p className="text-slate-500 font-medium">Overview of all system stakeholders and access levels.</p>
        </div>
        
        <div className="flex bg-slate-100 dark:bg-slate-900 p-1 rounded-2xl border border-border shrink-0 overflow-x-auto">
           {tabs.map(tab => (
             <button
               key={tab.value}
               onClick={() => setRoleFilter(tab.value)}
               className={`px-5 py-2 rounded-xl text-xs font-black uppercase tracking-widest transition-all whitespace-nowrap ${
                 roleFilter === tab.value
                   ? 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white shadow-sm'
                   : 'text-slate-500 hover:text-slate-900 dark:hover:text-white'
               }`}
             >
               {tab.label}
             </button>
           ))}
        </div>
      </div>

      <div className="relative shrink-0">
         <Search className="absolute left-5 top-1/2 -translate-y-1/2 text-slate-400" size={20} />
         <input 
           type="text" 
           placeholder="Search by username or email..."
           value={searchTerm}
           onChange={e => setSearchTerm(e.target.value)}
           className="w-full pl-14 pr-6 py-4 bg-white dark:bg-slate-950 border border-border rounded-3xl outline-none focus:ring-4 focus:ring-primary/5 focus:border-primary transition-all font-semibold"
         />
      </div>

      <div className="bg-white dark:bg-slate-950 border border-border rounded-[2.5rem] shadow-sm flex-1 overflow-hidden flex flex-col relative">
        <div className="overflow-x-auto h-full custom-scrollbar">
          <table className="w-full text-sm text-left border-separate border-spacing-0">
            <thead className="sticky top-0 z-10">
              <tr className="bg-slate-50 dark:bg-slate-900/50 backdrop-blur-md">
                <th className="px-8 py-5 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-border">Profile Identity</th>
                <th className="px-8 py-5 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-border">Secure Contact</th>
                <th className="px-8 py-5 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-border">Access Level</th>
                <th className="px-8 py-5 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-border text-right">
                  {filteredUsers.length > 0 && <span className="font-medium normal-case tracking-normal">{filteredUsers.length} result{filteredUsers.length !== 1 ? 's' : ''}</span>}
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 dark:divide-slate-900">
              {isLoading ? (
                <tr>
                  <td colSpan={4} className="px-8 py-16 text-center">
                     <div className="inline-flex items-center gap-3 text-slate-400 font-bold italic">
                        <div className="w-5 h-5 border-2 border-primary/10 border-t-primary rounded-full animate-spin" />
                        Fetching directory...
                     </div>
                  </td>
                </tr>
              ) : filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={4} className="px-8 py-16 text-center text-slate-400 font-bold uppercase tracking-widest text-xs">
                    {searchTerm || roleFilter !== 'ALL' ? 'No users match your filters.' : 'No users found.'}
                  </td>
                </tr>
              ) : (
                filteredUsers.map((user: Record<string, any>, idx: number) => (
                  <motion.tr 
                    initial={{ opacity: 0, scale: 0.98 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: idx * 0.02 }}
                    key={user.id} 
                    className="hover:bg-slate-50 dark:hover:bg-slate-900/40 transition-colors group cursor-default"
                  >
                    <td className="px-8 py-5">
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 rounded-2xl bg-slate-100 dark:bg-slate-800 border border-border/50 flex items-center justify-center text-primary shadow-inner group-hover:scale-110 transition-transform">
                          <User size={20} />
                        </div>
                        <div>
                           <p className="font-black text-slate-900 dark:text-white group-hover:text-primary transition-colors text-base tracking-tight">{user.username}</p>
                           <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mt-1">Ref: {String(user.id).padStart(5, '0')}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-8 py-5">
                       <div className="flex items-center gap-2 text-slate-500 font-bold">
                          <Mail size={14} className="text-slate-400" /> {user.email}
                       </div>
                    </td>
                    <td className="px-8 py-5">
                      <div className={`inline-flex items-center gap-2 px-3 py-1.5 rounded-full border text-[10px] font-black uppercase tracking-widest ${
                        user.role === 'ADMIN'
                          ? 'bg-rose-500/10 text-rose-600 border-rose-500/20'
                          : user.role === 'RESTAURANT_ADMIN'
                          ? 'bg-primary/10 text-primary border-primary/20' 
                          : 'bg-slate-100 dark:bg-slate-800 text-slate-500 border-border/50'
                      }`}>
                        <Shield size={12} /> {user.role}
                      </div>
                    </td>
                    <td className="px-8 py-5 text-right">
                       <button className="w-10 h-10 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 flex items-center justify-center text-slate-300 hover:text-slate-900 dark:hover:text-white transition-all ml-auto">
                          <MoreHorizontal size={20} />
                       </button>
                    </td>
                  </motion.tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
