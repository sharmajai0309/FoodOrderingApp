import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuthStore } from '../../store/useAuthStore'
import { Bike, ArrowRight, Zap } from 'lucide-react'
import { api } from '../../api/axios'
import { motion } from 'framer-motion'

export default function PartnerLogin() {
  const [driverId, setDriverId] = useState('1')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')
  const setCredentials = useAuthStore((state) => state.setCredentials)
  const navigate = useNavigate()

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError('')
    try {
      const res = await api.get(`/api/deliveryPartners/${driverId}`)
      if (res.data?.data) {
        const partner = res.data.data
        setCredentials(
          { id: Number(driverId), username: partner.name || 'Driver', email: partner.email || 'driver@example.com', role: 'DELIVERY_PARTNER', status: partner.status },
          'dummy-token'
        )
        const status = partner.status
        if (status === 'ACTIVE') navigate('/dashboard')
        else if (status === 'DOCUMENT_VERIFICATION_PENDING' || status === 'DOCUMENT_VERIFICATION_REJECTED') navigate('/status')
        else navigate('/upload')
      }
    } catch {
      setError(`Driver ID ${driverId} not found. Please check your ID or register first.`)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex" style={{ background: 'var(--bg-primary)' }}>

      {/* Left panel — branding */}
      <div className="hidden lg:flex flex-col justify-between flex-1 p-12 relative overflow-hidden"
        style={{ background: 'linear-gradient(145deg, #0c1a30 0%, #0a1525 60%, #060b14 100%)' }}>

        {/* Grid lines bg */}
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'linear-gradient(var(--border) 1px, transparent 1px), linear-gradient(90deg, var(--border) 1px, transparent 1px)', backgroundSize: '48px 48px', opacity: 0.4 }} />

        {/* Glow orb */}
        <div style={{ position: 'absolute', top: '30%', left: '20%', width: 400, height: 400, borderRadius: '50%', background: 'radial-gradient(circle, rgba(59,130,246,0.15) 0%, transparent 70%)', pointerEvents: 'none' }} />

        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-16">
            <div className="w-10 h-10 rounded-xl flex items-center justify-center glow-blue"
              style={{ background: 'linear-gradient(135deg, #3b82f6, #2563eb)' }}>
              <Bike size={20} className="text-white" />
            </div>
            <div>
              <p style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 20, letterSpacing: '-0.03em' }}>ZapDash</p>
              <p style={{ color: 'var(--text-muted)', fontSize: 11, fontWeight: 700, letterSpacing: '0.15em', textTransform: 'uppercase' }}>Partner Portal</p>
            </div>
          </div>

          <h2 style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 36, lineHeight: 1.2, letterSpacing: '-0.03em', maxWidth: 340 }}>
            Earn more.<br />
            <span style={{ background: 'linear-gradient(135deg, #60a5fa, #3b82f6)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
              Deliver smarter.
            </span>
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: 15, marginTop: 16, maxWidth: 320, lineHeight: 1.6 }}>
            Track orders in real-time, manage your earnings, and stay organized with the ZapDash driver portal.
          </p>
        </div>

        {/* Stats row */}
        <div className="relative z-10 flex items-center gap-8">
          {[['₹2.4L+', 'Paid out'],['12K+', 'Deliveries'],['4.9★', 'Avg Rating']].map(([val, label]) => (
            <div key={label}>
              <p style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 22 }}>{val}</p>
              <p style={{ color: 'var(--text-muted)', fontSize: 12, fontWeight: 600 }}>{label}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Right panel — login form */}
      <div className="flex-1 lg:max-w-md flex items-center justify-center p-8">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          style={{ width: '100%', maxWidth: 400 }}
        >
          {/* Mobile logo */}
          <div className="flex items-center gap-3 mb-10 lg:hidden">
            <div className="w-9 h-9 rounded-xl flex items-center justify-center"
              style={{ background: 'linear-gradient(135deg, #3b82f6, #2563eb)' }}>
              <Bike size={18} className="text-white" />
            </div>
            <p style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 18 }}>ZapDash</p>
          </div>

          <div className="mb-8">
            <h1 style={{ color: 'var(--text-primary)', fontWeight: 900, fontSize: 28, letterSpacing: '-0.03em', marginBottom: 6 }}>Sign in</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: 14 }}>Enter your Driver ID to access your dashboard.</p>
          </div>

          <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div>
              <label style={{ display: 'block', color: 'var(--text-muted)', fontSize: 11, fontWeight: 700, letterSpacing: '0.12em', textTransform: 'uppercase', marginBottom: 8 }}>
                Driver ID
              </label>
              <input
                type="number"
                value={driverId}
                onChange={(e) => setDriverId(e.target.value)}
                className="input-field"
                placeholder="e.g. 1"
                required
              />
            </div>

            {error && (
              <div style={{ background: 'var(--rose-soft)', border: '1px solid rgba(244,63,94,0.25)', borderRadius: 10, padding: '10px 14px', color: 'var(--rose)', fontSize: 13 }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading}
              className="btn-primary"
              style={{ width: '100%', padding: '14px', marginTop: 4 }}
            >
              {isLoading ? (
                <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <span className="w-4 h-4 border-2 rounded-full animate-spin" style={{ borderColor: 'rgba(255,255,255,0.3)', borderTopColor: '#fff' }} />
                  Authenticating…
                </span>
              ) : (
                <><Zap size={16} fill="currentColor" /> Access Portal <ArrowRight size={16} /></>
              )}
            </button>
          </form>

          <p style={{ marginTop: 24, textAlign: 'center', color: 'var(--text-muted)', fontSize: 13 }}>
            New to the fleet?{' '}
            <Link to="/register" style={{ color: 'var(--blue-bright)', fontWeight: 600, textDecoration: 'none' }}>
              Apply here →
            </Link>
          </p>
        </motion.div>
      </div>
    </div>
  )
}
