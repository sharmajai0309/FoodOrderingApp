import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/useAuthStore'
import { UploadCloud, ArrowRight } from 'lucide-react'
import { api } from '../../api/axios'

export default function DocumentUpload() {
  const user = useAuthStore((state) => state.user)
  const setCredentials = useAuthStore((state) => state.setCredentials)
  const token = useAuthStore((state) => state.token)
  
  const [documentType, setDocumentType] = useState('DRIVERS_LICENSE')
  const [documentUrl, setDocumentUrl] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!user) return

    setIsLoading(true)
    setError('')
    
    // Fallback if user leaves it empty (mocking a file upload URL)
    const finalUrl = documentUrl.trim() || `https://example.com/mock-doc-${Date.now()}.jpg`
    
    try {
      await api.post(`/api/driverDocument/${user.id}/documents`, {
        documentType,
        documentUrl: finalUrl
      })
      
      // Document uploaded successfully, the backend sets status to DOCUMENT_VERIFICATION_PENDING or similar.
      // Re-fetch user profile to sync the new status
      const res = await api.get(`/api/deliveryPartners/${user.id}`)
      if (res.data?.data) {
          const partner = res.data.data
          setCredentials(
            { ...user, status: partner.status },
            token || 'dummy-token'
          )
          
          if (partner.status === 'ACTIVE') {
              navigate('/dashboard')
          } else {
              navigate('/status')
          }
      } else {
          navigate('/status')
      }
      
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to upload document. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#0b0f19] flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white dark:bg-slate-950 p-8 rounded-4xl border border-border flex flex-col items-center">
        <div className="w-16 h-16 bg-blue-50 dark:bg-blue-500/10 text-blue-500 rounded-2xl flex items-center justify-center mb-6">
          <UploadCloud size={32} />
        </div>
        <h1 className="text-2xl font-black text-slate-900 dark:text-white mb-2 tracking-tight">Upload Documents</h1>
        <p className="text-slate-500 font-medium mb-8 text-center text-sm">
          Please provide your identity documents for verification before you can start delivering.
        </p>

        {error && (
            <div className="w-full p-4 mb-4 text-sm text-red-800 rounded-lg bg-red-50 dark:bg-gray-800 dark:text-red-400" role="alert">
                <span className="font-medium">Error:</span> {error}
            </div>
        )}

        <form onSubmit={handleUpload} className="w-full space-y-4">
          <div>
            <label className="text-xs font-black uppercase tracking-widest text-slate-500 mb-2 block">Document Type</label>
            <select
                value={documentType}
                onChange={(e) => setDocumentType(e.target.value)}
                className="w-full px-5 py-4 rounded-xl border border-border bg-slate-50 dark:bg-slate-900 font-bold text-slate-900 dark:text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all appearance-none"
                required
            >
                <option value="DRIVERS_LICENSE">Driver's License</option>
                <option value="PASSPORT">Passport</option>
                <option value="NATIONAL_ID">National ID</option>
                <option value="VEHICLE_REGISTRATION">Vehicle Registration</option>
            </select>
          </div>
          <div>
            <label className="text-xs font-black uppercase tracking-widest text-slate-500 mb-2 block">Document Link (Optional)</label>
            <input
              type="url"
              value={documentUrl}
              onChange={(e) => setDocumentUrl(e.target.value)}
              className="w-full px-5 py-4 rounded-xl border border-border bg-slate-50 dark:bg-slate-900 font-bold text-slate-900 dark:text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all"
              placeholder="https://..."
            />
            <p className="text-xs text-slate-400 mt-2 font-medium">Leave empty to auto-generate a mock URL for testing.</p>
          </div>
          
          <button
            type="submit"
            disabled={isLoading}
            className="w-full flex items-center justify-center gap-2 py-4 bg-slate-900 dark:bg-slate-800 hover:bg-black text-white font-black rounded-xl transition-all shadow-lg disabled:opacity-50 mt-4"
          >
            {isLoading ? 'Uploading...' : 'Submit Document'} <ArrowRight size={18} />
          </button>
        </form>
      </div>
    </div>
  )
}
