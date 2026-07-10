import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuthStore } from '../../store/useAuthStore'
import { UserPlus, ArrowRight } from 'lucide-react'
import { api } from '../../api/axios'

export default function PartnerRegister() {
  const [name, setName] = useState('')
  const [phone, setPhone] = useState('')
  const [vehicleType, setVehicleType] = useState('BIKE')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')
  
  const setCredentials = useAuthStore((state) => state.setCredentials)
  const navigate = useNavigate()

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError('')
    
    try {
      const res = await api.post('/api/deliveryPartners/register', {
        name,
        phone,
        vehicleType
      })
      
      const partner = res.data?.data
      if (partner) {
        setCredentials(
          {
            id: partner.id,
            username: partner.name,
            email: 'driver@example.com',
            role: 'DELIVERY_PARTNER',
            status: partner.status
          },
          'dummy-token'
        )
        // Check status and route. It should be PENDING or DOCUMENT_UPLOAD_PENDING
        navigate('/upload')
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to register. Phone number might already exist.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#0b0f19] flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white dark:bg-slate-950 p-8 rounded-4xl border border-border flex flex-col items-center">
        <div className="w-16 h-16 bg-blue-500 rounded-2xl flex items-center justify-center text-white mb-6 shadow-xl shadow-blue-500/20">
          <UserPlus size={32} />
        </div>
        <h1 className="text-2xl font-black text-slate-900 dark:text-white mb-2 tracking-tight">Become a Partner</h1>
        <p className="text-slate-500 font-medium mb-8 text-center text-sm">Join our delivery fleet and start earning today.</p>

        {error && (
            <div className="w-full p-4 mb-4 text-sm text-red-800 rounded-lg bg-red-50 dark:bg-gray-800 dark:text-red-400" role="alert">
                <span className="font-medium">Error:</span> {error}
            </div>
        )}

        <form onSubmit={handleRegister} className="w-full space-y-4">
          <div>
            <label className="text-xs font-black uppercase tracking-widest text-slate-500 mb-2 block">Full Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-5 py-4 rounded-xl border border-border bg-slate-50 dark:bg-slate-900 font-bold text-slate-900 dark:text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all"
              placeholder="e.g. John Doe"
              required
            />
          </div>
          <div>
            <label className="text-xs font-black uppercase tracking-widest text-slate-500 mb-2 block">Phone Number</label>
            <input
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              className="w-full px-5 py-4 rounded-xl border border-border bg-slate-50 dark:bg-slate-900 font-bold text-slate-900 dark:text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all"
              placeholder="e.g. 9876543210"
              required
            />
          </div>
          <div>
            <label className="text-xs font-black uppercase tracking-widest text-slate-500 mb-2 block">Vehicle Type</label>
            <select
                value={vehicleType}
                onChange={(e) => setVehicleType(e.target.value)}
                className="w-full px-5 py-4 rounded-xl border border-border bg-slate-50 dark:bg-slate-900 font-bold text-slate-900 dark:text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all appearance-none"
                required
            >
                <option value="BIKE">Bike</option>
                <option value="ELECTRIC_BIKE">Electric Bike</option>
                <option value="SCOOTER">Scooter</option>
                <option value="BICYCLE">Bicycle</option>
                <option value="WALKING_DELIVERY">Walking</option>
            </select>
          </div>
          
          <button
            type="submit"
            disabled={isLoading}
            className="w-full flex items-center justify-center gap-2 py-4 bg-blue-500 hover:bg-blue-600 text-white font-black rounded-xl transition-all shadow-lg shadow-blue-500/20 disabled:opacity-50 mt-4"
          >
            {isLoading ? 'Registering...' : 'Register'} <ArrowRight size={18} />
          </button>
        </form>

        <p className="mt-8 text-sm text-slate-500 font-medium">
          Already a partner? <Link to="/login" className="text-blue-500 hover:underline">Log in</Link>
        </p>
      </div>
    </div>
  )
}
