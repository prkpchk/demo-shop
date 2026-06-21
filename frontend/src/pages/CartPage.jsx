import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCart, updateCartItem, removeCartItem } from '../api/cart'
import { placeOrder } from '../api/orders'

export default function CartPage() {
  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(true)
  const [orderLoading, setOrderLoading] = useState(false)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    getCart()
      .then(res => setCart(res.data))
      .catch(() => setError('Failed to load cart'))
      .finally(() => setLoading(false))
  }, [])

  const handleQuantityChange = async (itemId, quantity) => {
    if (quantity < 1) return
    try {
      const res = await updateCartItem(itemId, quantity)
      setCart(res.data)
    } catch (err) {
      setError(err.response?.data?.error || 'Update failed')
    }
  }

  const handleRemove = async (itemId) => {
    try {
      const res = await removeCartItem(itemId)
      setCart(res.data)
    } catch {
      setError('Remove failed')
    }
  }

  const handlePlaceOrder = async () => {
    setOrderLoading(true)
    setError(null)
    try {
      const res = await placeOrder()
      navigate('/orders', { state: { newOrderId: res.data.id } })
    } catch (err) {
      setError(err.response?.data?.error || 'Could not place order')
    } finally {
      setOrderLoading(false)
    }
  }

  if (loading) return <div className="loading">Loading...</div>

  return (
    <div className="cart-page">
      <h1>Your Cart</h1>
      {error && <p className="error-msg">{error}</p>}
      {!cart || cart.items.length === 0 ? (
        <p className="empty-state">Your cart is empty.</p>
      ) : (
        <>
          <div className="cart-items">
            {cart.items.map(item => (
              <div key={item.id} className="cart-item">
                <div className="cart-item-info">
                  <p className="cart-item-name">{item.productName}</p>
                  <p className="cart-item-price">${item.price} each</p>
                </div>
                <div className="cart-item-actions">
                  <input
                    type="number"
                    min="1"
                    value={item.quantity}
                    onChange={e => handleQuantityChange(item.id, Number(e.target.value))}
                    className="quantity-input"
                  />
                  <p className="cart-item-subtotal">${item.subtotal}</p>
                  <button
                    onClick={() => handleRemove(item.id)}
                    className="btn btn-danger btn-sm"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>
          <div className="cart-summary">
            <p className="cart-total">Total: <strong>${cart.total}</strong></p>
            <button
              onClick={handlePlaceOrder}
              className="btn btn-primary"
              disabled={orderLoading}
            >
              {orderLoading ? 'Placing order...' : 'Place Order'}
            </button>
          </div>
        </>
      )}
    </div>
  )
}
