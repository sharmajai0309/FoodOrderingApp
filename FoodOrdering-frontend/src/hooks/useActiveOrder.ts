import { useQuery } from '@tanstack/react-query'
import { useAuthStore } from '../store/useAuthStore'
import { api } from '../api/axios'
import { useEffect, useRef } from 'react'
import { toast } from 'sonner'

interface ActiveOrder {
  id: number
  orderStatus: string
  restaurantName?: string
  restaurantImage?: string
  totalAmount: number
  deliveryPartnerId?: number
  createdAt?: string
}

const ACTIVE_STATUSES = new Set([
  'PAID', 'ACCEPTED', 'PREPARING', 'PICKED_UP', 'OUT_FOR_DELIVERY'
])

const STATUS_MESSAGES: Record<string, string> = {
  PAID: 'Payment confirmed — finding your delivery partner...',
  ACCEPTED: 'Order accepted by restaurant! 🎉',
  PREPARING: 'Restaurant is preparing your food 👨‍🍳',
  PICKED_UP: 'Food picked up — on the way! 🛵',
  OUT_FOR_DELIVERY: 'Your order is out for delivery! 🚀',
  DELIVERED: 'Order delivered! Enjoy your meal 😋',
}

/**
 * Global hook — polls all user orders for any currently active one.
 * Fires browser + toast notifications when status changes.
 */
export function useActiveOrder() {
  const { user } = useAuthStore()
  const prevStatusRef = useRef<Record<number, string>>({})

  const { data: orders } = useQuery<ActiveOrder[]>({
    queryKey: ['orders', user?.id],
    queryFn: async () => {
      const res = await api.get('/v1/api/customer/order/user')
      return res.data.data ?? []
    },
    enabled: !!user,
    refetchInterval: 5000, // poll every 5s
  })

  // Detect status changes and fire notifications
  useEffect(() => {
    if (!orders) return
    orders.forEach((order) => {
      const prev = prevStatusRef.current[order.id]
      const curr = order.orderStatus
      if (prev && prev !== curr && STATUS_MESSAGES[curr]) {
        // Toast notification
        toast(STATUS_MESSAGES[curr], {
          description: `Order #${order.id} from ${order.restaurantName ?? 'your restaurant'}`,
          duration: 6000,
          icon: curr === 'OUT_FOR_DELIVERY' ? '🛵' : curr === 'DELIVERED' ? '✅' : '🍽️',
        })

        // Browser push notification (if permitted)
        if (Notification.permission === 'granted') {
          new Notification(`CraveRush — Order #${order.id}`, {
            body: STATUS_MESSAGES[curr],
            icon: '/vite.svg',
            tag: `order-${order.id}`,
          })
        }
      }
      prevStatusRef.current[order.id] = curr
    })
  }, [orders])

  // Request browser notification permission on first use
  useEffect(() => {
    if (user && Notification.permission === 'default') {
      Notification.requestPermission()
    }
  }, [user])

  const activeOrder = orders?.find(o => ACTIVE_STATUSES.has(o.orderStatus)) ?? null

  return { activeOrder, orders }
}
