import { useState, useEffect } from 'react'
import { getOrders } from '../api/orders'
import PaymentModal from '../components/PaymentModal'

export default function OrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [payingOrder, setPayingOrder] = useState(null)

  useEffect(() => {
    getOrders()
      .then(res => setOrders(res.data))
      .catch(() => setError('Failed to load orders'))
      .finally(() => setLoading(false))
  }, [])

  const handlePaymentSuccess = (updatedOrder) => {
    setOrders(orders.map(o => o.id === updatedOrder.id ? updatedOrder : o))
    setPayingOrder(null)
  }

  const statusClass = (status) => {
    if (status === 'PAID') return 'status-paid'
    if (status === 'CANCELLED') return 'status-cancelled'
    return 'status-pending'
  }

  if (loading) return <div className="loading">Loading...</div>

  return (
    <div className="orders-page">
      <h1>My Orders</h1>
      {error && <p className="error-msg">{error}</p>}
      {orders.length === 0 ? (
        <p className="empty-state">No orders yet.</p>
      ) : (
        <div className="orders-list">
          {orders.map(order => (
            <div key={order.id} className="order-card">
              <div className="order-header">
                <span className="order-id">Order #{order.id}</span>
                <span className={`order-status ${statusClass(order.status)}`}>
                  {order.status}
                </span>
              </div>
              <div className="order-items">
                {order.items.map(item => (
                  <div key={item.id} className="order-item">
                    <span>{item.productName}</span>
                    <span>×{item.quantity}</span>
                    <span>${item.subtotal}</span>
                  </div>
                ))}
              </div>
              <div className="order-footer">
                <span className="order-total">Total: <strong>${order.totalAmount}</strong></span>
                <span className="order-date">
                  {new Date(order.createdAt).toLocaleDateString()}
                </span>
                {order.status === 'PENDING' && (
                  <button
                    onClick={() => setPayingOrder(order)}
                    className="btn btn-primary btn-sm"
                  >
                    Pay
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
      {payingOrder && (
        <PaymentModal
          order={payingOrder}
          onClose={() => setPayingOrder(null)}
          onSuccess={handlePaymentSuccess}
        />
      )}
    </div>
  )
}
