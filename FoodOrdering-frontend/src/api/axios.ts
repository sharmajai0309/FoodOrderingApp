import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:5454',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Auto-inject token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Global error handler
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      // redirect to login etc if needed
    }
    return Promise.reject(error)
  }
)
