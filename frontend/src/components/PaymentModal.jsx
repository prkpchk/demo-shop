import { useState } from 'react'
import { payOrder } from '../api/orders'

export default function PaymentModal({ order, onClose, onSuccess }) {
  const [simulateFailure, setSimulateFailure] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handlePay = async () => {
    setLoading(true)
    setError(null)
    try {
      const { data } = await payOrder(order.id, simulateFailure)
      onSuccess(data)
    } catch (err) {
      setError(err.response?.data?.error || 'Payment failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <h2>Pay Order #{order.id}</h2>
        <p className="modal-amount">Amount: <strong>${order.totalAmount}</strong></p>
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={simulateFailure}
            onChange={e => setSimulateFailure(e.target.checked)}
          />
          Simulate payment failure
        </label>
        {error && <p className="error-msg">{error}</p>}
        <div className="modal-actions">
          <button onClick={onClose} className="btn btn-secondary" disabled={loading}>
            Cancel
          </button>
          <button
            onClick={handlePay}
            className={`btn ${simulateFailure ? 'btn-danger' : 'btn-primary'}`}
            disabled={loading}
          >
            {loading ? 'Processing...' : simulateFailure ? 'Simulate Failure' : 'Pay Now'}
          </button>
        </div>
      </div>
    </div>
  )
}
