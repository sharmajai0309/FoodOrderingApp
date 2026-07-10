import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import { Toaster } from 'sonner'
import 'react-toastify/dist/ReactToastify.css'
import './index.css'
import App from './App.tsx'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 1000 * 60 * 2,
      gcTime:    1000 * 60 * 5,
    },
  },
})

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <App />
        <ToastContainer position="bottom-right" theme="colored" />
        <Toaster
          position="top-right"
          richColors
          expand
          toastOptions={{
            duration: 6000,
            style: { fontFamily: 'Inter, sans-serif', fontWeight: '600' },
          }}
        />
      </BrowserRouter>
    </QueryClientProvider>
  </StrictMode>,
)
