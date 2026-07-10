import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { api } from '../api/axios'
import { toast } from 'react-toastify'
import { motion } from 'framer-motion'
import { ArrowLeft, Mail, Lock, User, Briefcase } from 'lucide-react'
import { addAuditLog } from './admin/AuditLogs'

export default function Register() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm()

  const onSubmit = async (data: any) => {
    setLoading(true)
    try {
      await api.post('/v1/auth/signup', data)
      addAuditLog({
        username: data.username,
        action: 'USER_REGISTER',
        entity: 'User',
        description: `New user registered: ${data.username} (${data.role || 'CUSTOMER'})`,
        status: 'SUCCESS',
      })
      toast.success('Registration successful! Please login.')
      navigate('/auth/login')
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex bg-white dark:bg-[#0b0f19]">
      {/* Right Side - Visual (Swapped for variety) */}
      <div className="hidden lg:flex lg:w-1/2 relative overflow-hidden order-last">
        <img 
          src="https://images.unsplash.com/photo-1543353071-10c8ba85a904?q=80&w=2070&auto=format&fit=crop" 
          alt="Fresh ingredients" 
          className="absolute inset-0 w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-primary/10 backdrop-blur-[1px]" />
        <div className="absolute inset-0 bg-linear-to-t from-black/80 via-black/20 to-transparent flex flex-col justify-end p-16 text-white text-right">
          <motion.div
            initial={{ opacity: 0, x: 30 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.2 }}
          >
            <h1 className="text-5xl font-black mb-6 leading-tight text-white">Join the <br /><span className="text-primary italic">revolution.</span></h1>
            <p className="text-xl text-slate-200 ml-auto max-w-lg font-medium leading-relaxed">
              Be a part of the fastest growing food network. Whether you are a foodie or a chef, there's a place for you here.
            </p>
          </motion.div>
        </div>
      </div>

      {/* Left Side - Form */}
      <div className="w-full lg:w-1/2 flex flex-col justify-center items-center p-8 sm:p-12 lg:p-20 relative overflow-y-auto">
        <Link 
          to="/" 
          className="absolute top-8 left-8 flex items-center gap-2 text-slate-500 hover:text-primary transition-colors text-sm font-bold uppercase tracking-wider"
        >
          <ArrowLeft size={18} /> Back to home
        </Link>

        <motion.div 
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="w-full max-w-md py-12"
        >
          <div className="mb-10 text-center lg:text-left">
            <h2 className="text-4xl font-black text-slate-900 dark:text-white mb-2 tracking-tight">Create Account</h2>
            <p className="text-slate-500 font-medium">Join us and start your culinary journey.</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <div className="space-y-1.5 focus-within:text-primary transition-colors">
              <label className="text-xs font-black uppercase tracking-widest text-slate-400">Username</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 transition-colors">
                  <User size={18} />
                </div>
                <input
                  {...register('username', { required: 'Username is required' })}
                  type="text"
                  className="w-full pl-11 pr-4 py-3 bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary outline-none transition-all font-medium text-slate-900 dark:text-white"
                  placeholder="e.g. jai_chef"
                />
              </div>
              {errors.username && <p className="text-destructive text-xs font-bold mt-1">{errors.username.message as string}</p>}
            </div>

            <div className="space-y-1.5 focus-within:text-primary transition-colors">
              <label className="text-xs font-black uppercase tracking-widest text-slate-400">Email Address</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 transition-colors">
                  <Mail size={18} />
                </div>
                <input
                  {...register('email', { 
                    required: 'Email is required',
                    pattern: { value: /^\S+@\S+$/i, message: 'Invalid email format' }
                  })}
                  type="email"
                  className="w-full pl-11 pr-4 py-3 bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary outline-none transition-all font-medium text-slate-900 dark:text-white"
                  placeholder="name@example.com"
                />
              </div>
              {errors.email && <p className="text-destructive text-xs font-bold mt-1">{errors.email.message as string}</p>}
            </div>

            <div className="space-y-1.5 focus-within:text-primary transition-colors">
              <label className="text-xs font-black uppercase tracking-widest text-slate-400">Security Password</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 transition-colors">
                  <Lock size={18} />
                </div>
                <input
                  {...register('password', { required: 'Security check: password missing' })}
                  type="password"
                  className="w-full pl-11 pr-4 py-3 bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary outline-none transition-all font-medium text-slate-900 dark:text-white"
                  placeholder="••••••••"
                />
              </div>
              {errors.password && <p className="text-destructive text-xs font-bold mt-1">{errors.password.message as string}</p>}
            </div>

            <div className="space-y-1.5 focus-within:text-primary transition-colors">
              <label className="text-xs font-black uppercase tracking-widest text-slate-400">I am a...</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 transition-colors">
                  <Briefcase size={18} />
                </div>
                <select
                  {...register('role', { required: 'Selection required' })}
                  className="w-full pl-11 pr-4 py-3 bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary outline-none transition-all font-semibold text-slate-900 dark:text-white appearance-none"
                >
                  <option value="CUSTOMER">Hungry Customer</option>
                  <option value="RESTAURANT_ADMIN">Restaurant Owner</option>
                </select>
                <div className="absolute right-4 inset-y-0 flex items-center pointer-events-none text-slate-400">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" /></svg>
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full flex justify-center items-center py-4 px-4 mt-4 rounded-2xl shadow-xl shadow-primary/20 text-sm font-black uppercase tracking-widest text-white bg-primary hover:bg-primary/90 focus:outline-none focus:ring-4 focus:ring-primary/20 transition-all transform active:scale-[0.98] disabled:opacity-50"
            >
              {loading ? (
                <div className="flex items-center gap-2">
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  <span>Processing...</span>
                </div>
              ) : 'Start Journey'}
            </button>
          </form>

          <div className="mt-10 pt-8 border-t border-slate-100 dark:border-slate-800 text-center lg:text-left">
            <p className="text-sm font-semibold text-slate-600 dark:text-slate-400">
              Already have an account?{' '}
              <Link to="/auth/login" className="font-extrabold text-primary hover:underline ml-1">
                Log in instead
              </Link>
            </p>
          </div>
        </motion.div>
      </div>
    </div>
  )
}
