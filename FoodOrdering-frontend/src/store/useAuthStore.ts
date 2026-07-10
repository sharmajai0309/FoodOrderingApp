import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface User {
  id: number
  username: string
  email: string
  role: string
}

interface AuthState {
  user: User | null
  token: string | null
  setCredentials: (user: User, token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      setCredentials: (user, token) => {
        localStorage.setItem('token', token)
        set({ user, token })
      },
      logout: () => {
        localStorage.removeItem('token')
        set({ user: null, token: null })
      },
    }),
    {
      name: 'auth-storage',
    }
  )
)
