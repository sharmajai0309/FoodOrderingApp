import { useState, useEffect, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { ScrollText, Search, Trash2, Download, Filter, CheckCircle2, XCircle, ShoppingBag, User, LogIn, LogOut, Utensils } from 'lucide-react'

export type AuditAction =
  | 'USER_LOGIN'
  | 'USER_LOGOUT'
  | 'USER_REGISTER'
  | 'ORDER_CREATED'
  | 'ORDER_CANCELLED'
  | 'CART_ITEM_ADDED'
  | 'CART_ITEM_REMOVED'
  | 'RESTAURANT_CREATED'
  | 'RESTAURANT_UPDATED'
  | 'RESTAURANT_DELETED'
  | 'FOOD_ITEM_ADDED'
  | 'ADMIN_ACTION'

export interface AuditLog {
  id: string
  timestamp: string
  userId?: number | null
  username?: string | null
  action: AuditAction
  entity: string
  description: string
  status: 'SUCCESS' | 'FAILURE'
  meta?: Record<string, any>
}

const AUDIT_KEY = 'foodapp_audit_logs'

export function addAuditLog(log: Omit<AuditLog, 'id' | 'timestamp'>) {
  const logs: AuditLog[] = JSON.parse(localStorage.getItem(AUDIT_KEY) || '[]')
  const newLog: AuditLog = {
    ...log,
    id: `${Date.now()}-${Math.random().toString(36).slice(2)}`,
    timestamp: new Date().toISOString(),
  }
  // Keep max 200 logs
  const updated = [newLog, ...logs].slice(0, 200)
  localStorage.setItem(AUDIT_KEY, JSON.stringify(updated))
  return newLog
}

export function useAuditLogs() {
  const [logs, setLogs] = useState<AuditLog[]>([])

  const load = useCallback(() => {
    const raw = localStorage.getItem(AUDIT_KEY)
    setLogs(raw ? JSON.parse(raw) : [])
  }, [])

  useEffect(() => { load() }, [load])

  return { logs, reload: load }
}

const ACTION_CONFIG: Record<AuditAction, { icon: any; color: string; bg: string; label: string }> = {
  USER_LOGIN:          { icon: LogIn,       color: 'text-emerald-600', bg: 'bg-emerald-50 dark:bg-emerald-950/20', label: 'Login' },
  USER_LOGOUT:         { icon: LogOut,      color: 'text-slate-600',   bg: 'bg-slate-50 dark:bg-slate-900',        label: 'Logout' },
  USER_REGISTER:       { icon: User,        color: 'text-blue-600',    bg: 'bg-blue-50 dark:bg-blue-950/20',       label: 'Register' },
  ORDER_CREATED:       { icon: ShoppingBag, color: 'text-primary',     bg: 'bg-primary/5',                         label: 'Order Created' },
  ORDER_CANCELLED:     { icon: XCircle,     color: 'text-rose-600',    bg: 'bg-rose-50 dark:bg-rose-950/20',       label: 'Order Cancelled' },
  CART_ITEM_ADDED:     { icon: CheckCircle2,color: 'text-emerald-600', bg: 'bg-emerald-50 dark:bg-emerald-950/20', label: 'Cart Add' },
  CART_ITEM_REMOVED:   { icon: Trash2,      color: 'text-rose-500',    bg: 'bg-rose-50 dark:bg-rose-950/20',       label: 'Cart Remove' },
  RESTAURANT_CREATED:  { icon: Utensils,    color: 'text-purple-600',  bg: 'bg-purple-50 dark:bg-purple-950/20',   label: 'Restaurant Created' },
  RESTAURANT_UPDATED:  { icon: Utensils,    color: 'text-amber-600',   bg: 'bg-amber-50 dark:bg-amber-950/20',     label: 'Restaurant Updated' },
  RESTAURANT_DELETED:  { icon: Trash2,      color: 'text-rose-600',    bg: 'bg-rose-50 dark:bg-rose-950/20',       label: 'Restaurant Deleted' },
  FOOD_ITEM_ADDED:     { icon: Utensils,    color: 'text-blue-600',    bg: 'bg-blue-50 dark:bg-blue-950/20',       label: 'Food Added' },
  ADMIN_ACTION:        { icon: CheckCircle2,color: 'text-slate-600',   bg: 'bg-slate-50 dark:bg-slate-900',        label: 'Admin Action' },
}

function formatTime(iso: string) {
  const d = new Date(iso)
  return d.toLocaleString('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

function exportCSV(logs: AuditLog[]) {
  const headers = ['ID', 'Timestamp', 'User', 'Action', 'Entity', 'Description', 'Status']
  const rows = logs.map(l => [l.id, l.timestamp, l.username || '', l.action, l.entity, l.description, l.status])
  const csv = [headers, ...rows].map(r => r.map(v => `"${String(v).replace(/"/g, '""')}"`).join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `audit-log-${Date.now()}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

export default function AuditLogs() {
  const { logs, reload } = useAuditLogs()
  const [search, setSearch] = useState('')
  const [actionFilter, setActionFilter] = useState<string>('ALL')

  const actionTypes = ['ALL', ...Array.from(new Set(logs.map(l => l.action)))]

  const filtered = logs.filter(l => {
    const matchesSearch = !search ||
      l.description.toLowerCase().includes(search.toLowerCase()) ||
      l.action.toLowerCase().includes(search.toLowerCase()) ||
      (l.username || '').toLowerCase().includes(search.toLowerCase())
    const matchesAction = actionFilter === 'ALL' || l.action === actionFilter
    return matchesSearch && matchesAction
  })

  const clearLogs = () => {
    if (!confirm('Clear all audit logs? This cannot be undone.')) return
    localStorage.removeItem(AUDIT_KEY)
    reload()
  }

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-black text-slate-900 dark:text-white tracking-tight">Audit Trail</h1>
          <p className="text-slate-500 font-medium mt-1">Track all system events and user actions.</p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => exportCSV(filtered)}
            disabled={filtered.length === 0}
            className="flex items-center gap-2 px-5 py-2.5 bg-white dark:bg-slate-900 border border-border rounded-2xl text-xs font-black uppercase tracking-widest text-slate-600 hover:text-primary transition-all shadow-sm disabled:opacity-40"
          >
            <Download size={14} /> Export CSV
          </button>
          <button
            onClick={clearLogs}
            disabled={logs.length === 0}
            className="flex items-center gap-2 px-5 py-2.5 bg-rose-50 dark:bg-rose-950/20 border border-rose-200 dark:border-rose-800 rounded-2xl text-xs font-black uppercase tracking-widest text-rose-600 hover:bg-rose-100 transition-all disabled:opacity-40"
          >
            <Trash2 size={14} /> Clear
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
          <input
            type="text"
            placeholder="Search logs by action, user, or description..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="w-full pl-12 pr-4 py-3.5 bg-white dark:bg-slate-950 border border-border rounded-2xl outline-none focus:ring-4 focus:ring-primary/5 focus:border-primary font-semibold text-sm transition-all"
          />
        </div>
        <div className="relative">
          <Filter className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
          <select
            value={actionFilter}
            onChange={e => setActionFilter(e.target.value)}
            className="pl-10 pr-8 py-3.5 bg-white dark:bg-slate-950 border border-border rounded-2xl outline-none focus:ring-4 focus:ring-primary/5 focus:border-primary font-semibold text-sm text-slate-700 dark:text-slate-300 transition-all appearance-none cursor-pointer"
          >
            {actionTypes.map(a => (
              <option key={a} value={a}>{a === 'ALL' ? 'All Actions' : ACTION_CONFIG[a as AuditAction]?.label || a}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Log count */}
      <div className="flex items-center gap-3">
        <ScrollText size={16} className="text-slate-400" />
        <span className="text-sm font-bold text-slate-500">
          {filtered.length} event{filtered.length !== 1 ? 's' : ''}
          {logs.length !== filtered.length && ` (${logs.length} total)`}
        </span>
      </div>

      {/* Log List */}
      <div className="bg-white dark:bg-slate-950 border border-border rounded-3xl shadow-sm overflow-hidden">
        {filtered.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20 text-slate-400">
            <ScrollText size={48} className="mb-4 opacity-30" />
            <p className="font-black uppercase tracking-widest text-xs">
              {logs.length === 0 ? 'No audit events recorded yet.' : 'No events match your filters.'}
            </p>
            {logs.length === 0 && (
              <p className="text-xs font-medium mt-2 max-w-xs text-center">Events are automatically logged when users register, login, add items to cart, place orders, and more.</p>
            )}
          </div>
        ) : (
          <div className="divide-y divide-slate-50 dark:divide-slate-900">
            <AnimatePresence>
              {filtered.map((log, idx) => {
                const cfg = ACTION_CONFIG[log.action] || ACTION_CONFIG.ADMIN_ACTION
                const Icon = cfg.icon
                return (
                  <motion.div
                    key={log.id}
                    initial={{ opacity: 0, x: -10 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: Math.min(idx * 0.02, 0.3) }}
                    className="flex items-start gap-4 px-6 py-4 hover:bg-slate-50 dark:hover:bg-slate-900/50 transition-colors"
                  >
                    <div className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0 mt-0.5 ${cfg.bg}`}>
                      <Icon size={16} className={cfg.color} />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className={`text-[10px] font-black uppercase tracking-widest px-2.5 py-1 rounded-full border ${
                          log.status === 'SUCCESS'
                            ? 'text-emerald-600 bg-emerald-50 dark:bg-emerald-950/20 border-emerald-200 dark:border-emerald-800'
                            : 'text-rose-600 bg-rose-50 dark:bg-rose-950/20 border-rose-200 dark:border-rose-800'
                        }`}>{cfg.label}</span>
                        {log.username && (
                          <span className="text-[10px] font-black uppercase tracking-widest text-slate-400 bg-slate-100 dark:bg-slate-800 px-2.5 py-1 rounded-full">
                            @{log.username}
                          </span>
                        )}
                        <span className="text-[10px] font-bold text-slate-400">{log.entity}</span>
                      </div>
                      <p className="text-sm font-semibold text-slate-700 dark:text-slate-300 mt-1 truncate">{log.description}</p>
                    </div>
                    <div className="text-right shrink-0">
                      <p className="text-[10px] font-bold text-slate-400">{formatTime(log.timestamp)}</p>
                      {log.userId && <p className="text-[10px] text-slate-300 font-bold mt-0.5">UID: {log.userId}</p>}
                    </div>
                  </motion.div>
                )
              })}
            </AnimatePresence>
          </div>
        )}
      </div>
    </div>
  )
}
