import { Link } from 'react-router-dom'
import { useAuthStore } from '../../store/useAuthStore'
import { Clock, AlertTriangle, ArrowLeft } from 'lucide-react'

export default function StatusPending() {
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)

  const isRejected = user?.status === 'DOCUMENT_VERIFICATION_REJECTED'

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#0b0f19] flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white dark:bg-slate-950 p-8 rounded-4xl border border-border flex flex-col items-center text-center">
        <div className={`w-20 h-20 rounded-3xl flex items-center justify-center mb-6 shadow-xl ${
            isRejected 
            ? 'bg-red-500 shadow-red-500/20 text-white' 
            : 'bg-amber-500 shadow-amber-500/20 text-white'
        }`}>
          {isRejected ? <AlertTriangle size={40} /> : <Clock size={40} />}
        </div>
        
        <h1 className="text-2xl font-black text-slate-900 dark:text-white mb-4 tracking-tight">
            {isRejected ? 'Application Rejected' : 'Under Review'}
        </h1>
        
        <div className="bg-slate-50 dark:bg-slate-900 rounded-2xl p-6 mb-8 w-full border border-border">
            <p className="text-sm font-semibold text-slate-700 dark:text-slate-300">
                Driver ID: <span className="text-emerald-500">{user?.id}</span>
            </p>
            <p className="text-sm font-semibold text-slate-700 dark:text-slate-300 mt-2">
                Status: <span className={isRejected ? 'text-red-500' : 'text-amber-500'}>{user?.status?.replace(/_/g, ' ')}</span>
            </p>
        </div>

        <p className="text-slate-500 font-medium mb-8 text-sm leading-relaxed">
            {isRejected 
                ? 'Unfortunately, your documents were rejected. Please click the button below to re-upload them.' 
                : 'Your documents have been submitted and are currently awaiting admin verification. Please check back later.'}
        </p>

        {isRejected ? (
            <Link
                to="/upload"
                className="w-full py-4 bg-red-500 hover:bg-red-600 text-white font-black rounded-xl transition-all shadow-lg shadow-red-500/20"
            >
                Re-upload Documents
            </Link>
        ) : (
            <button
                onClick={() => {
                   logout()
                   window.location.href = '/login'
                }}
                className="w-full flex items-center justify-center gap-2 py-4 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-900 dark:text-white font-black rounded-xl transition-all"
            >
                <ArrowLeft size={18} /> Back to Login
            </button>
        )}
      </div>
    </div>
  )
}
