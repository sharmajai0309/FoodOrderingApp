import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import type { FieldValues } from 'react-hook-form'
import { useAuthStore } from '../store/useAuthStore'
import { api } from '../api/axios'
import { toast } from 'react-toastify'
import { motion } from 'framer-motion'
import { ArrowLeft, Lock, User } from 'lucide-react'
import { addAuditLog } from './admin/AuditLogs'

export default function Login() {
  const navigate = useNavigate()
  const { setCredentials } = useAuthStore()
  const [loading, setLoading] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm()

  const onSubmit = async (data: FieldValues) => {
    setLoading(true)
    try {
      // POST /v1/auth/login → returns { token, username, role, message }
      const res = await api.post('/v1/auth/login', {
        username: data.username,
        password: data.password
      })
      const { token, username, role } = res.data

      // Also fetch full profile to get the user id
      const profileRes = await api.get('/v1/user/profile', {
        headers: { Authorization: `Bearer ${token}` }
      })
      
      setCredentials({
        id: profileRes.data.id,
        username: profileRes.data.username || username,
        email: profileRes.data.email,
        role: profileRes.data.role || role,
      }, token)
      
      addAuditLog({
        userId: profileRes.data.id,
        username: profileRes.data.username || username,
        action: 'USER_LOGIN',
        entity: 'User',
        description: `User ${profileRes.data.username || username} logged in successfully`,
        status: 'SUCCESS',
      })

      toast.success('Welcome back!')
      
      const userRole = profileRes.data.role || role
      if (userRole === 'RESTAURANT_ADMIN' || userRole === 'ADMIN') {
        navigate('/admin')
      } else {
        navigate('/')
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Invalid credentials')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex bg-white dark:bg-[#0b0f19]">
      {/* Left Side - Visual */}
      <div className="hidden lg:flex lg:w-1/2 relative overflow-hidden">
        <img 
          src="https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=1974&auto=format&fit=crop" 
          alt="Luxury dining" 
          className="absolute inset-0 w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-primary/20 backdrop-blur-[2px]" />
        <div className="absolute inset-0 bg-linear-to-t from-black/80 via-black/20 to-transparent flex flex-col justify-end p-16 text-white">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
          >
            <h1 className="text-5xl font-black mb-6 leading-tight">Feed your <br /><span className="text-primary italic">imagination.</span></h1>
            <p className="text-xl text-slate-200 max-w-lg font-medium leading-relaxed">
              Experience the finest culinary delights delivered straight from the most premium kitchens in the city.
            </p>
          </motion.div>
        </div>
      </div>

      {/* Right Side - Form */}
      <div className="w-full lg:w-1/2 flex flex-col justify-center items-center p-8 sm:p-12 lg:p-20 relative">
        <Link 
          to="/" 
          className="absolute top-8 left-8 flex items-center gap-2 text-slate-500 hover:text-primary transition-colors text-sm font-bold uppercase tracking-wider"
        >
          <ArrowLeft size={18} /> Back to home
        </Link>

        <motion.div 
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="w-full max-w-md"
        >
          <div className="mb-10">
            <h2 className="text-4xl font-black text-slate-900 dark:text-white mb-2 tracking-tight">Login</h2>
            <p className="text-slate-500 font-medium">Please enter your details to sign in.</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div className="space-y-1.5 focus-within:text-primary transition-colors">
              <label className="text-xs font-black uppercase tracking-widest text-slate-400">Username / Email</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 group-focus-within:text-primary transition-colors">
                  <User size={18} />
                </div>
                <input
                  {...register('username', { required: 'This field is required' })}
                  type="text"
                  className="w-full pl-11 pr-4 py-3.5 bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary outline-none transition-all font-medium text-slate-900 dark:text-white"
                  placeholder="e.g. jai_sharma"
                />
              </div>
              {errors.username && <p className="text-destructive text-xs font-bold mt-1">{errors.username.message as string}</p>}
            </div>

            <div className="space-y-1.5 focus-within:text-primary transition-colors">
              <label className="text-xs font-black uppercase tracking-widest text-slate-400">Password</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 focus-within:text-primary transition-colors">
                  <Lock size={18} />
                </div>
                <input
                  {...register('password', { required: 'Password is required' })}
                  type="password"
                  className="w-full pl-11 pr-4 py-3.5 bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary outline-none transition-all font-medium text-slate-900 dark:text-white"
                  placeholder="••••••••"
                />
              </div>
              {errors.password && <p className="text-destructive text-xs font-bold mt-1">{errors.password.message as string}</p>}
            </div>

            <div className="flex items-center justify-between">
              <label className="flex items-center gap-2 cursor-pointer group">
                <input type="checkbox" className="w-4 h-4 rounded border-slate-300 text-primary focus:ring-primary" />
                <span className="text-sm font-semibold text-slate-600 dark:text-slate-400 group-hover:text-primary transition-colors">Remember me</span>
              </label>
              <a href="#" className="text-sm font-bold text-primary hover:underline">Forgot password?</a>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full flex justify-center items-center py-4 px-4 rounded-2xl shadow-xl shadow-primary/20 text-sm font-black uppercase tracking-widest text-white bg-primary hover:bg-primary/90 focus:outline-none focus:ring-4 focus:ring-primary/20 transition-all transform active:scale-[0.98] disabled:opacity-50"
            >
              {loading ? (
                <div className="flex items-center gap-2">
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  <span>Signing in...</span>
                </div>
              ) : 'Sign in'}
            </button>
          </form>

          <div className="mt-10 pt-8 border-t border-slate-100 dark:border-slate-800 text-center">
            <p className="text-sm font-semibold text-slate-600 dark:text-slate-400">
              New to FoodApp?{' '}
              <Link to="/auth/register" className="font-extrabold text-primary hover:underline ml-1">
                Create an account
              </Link>
            </p>
          </div>
        </motion.div>
      </div>
    </div>
  )
}
