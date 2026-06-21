import { useState, useEffect } from 'react'
import { getProfile, topUp } from '../api/users'
import { useAuth } from '../context/AuthContext'

export default function ProfilePage() {
  const { login } = useAuth()
  const [profile, setProfile] = useState(null)
  const [amount, setAmount] = useState('')
  const [loading, setLoading] = useState(true)
  const [topUpLoading, setTopUpLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)

  useEffect(() => {
    getProfile()
      .then(res => setProfile(res.data))
      .catch(() => setError('Failed to load profile'))
      .finally(() => setLoading(false))
  }, [])

  const handleTopUp = async e => {
    e.preventDefault()
    const value = parseFloat(amount)
    if (!value || value <= 0) { setError('Enter a valid amount'); return }
    setTopUpLoading(true)
    setError(null)
    setSuccess(null)
    try {
      const res = await topUp(value)
      setProfile(res.data)
      login({ email: res.data.email, name: res.data.name, role: res.data.role },
        localStorage.getItem('token'))
      setSuccess(`Balance topped up. New balance: $${res.data.balance}`)
      setAmount('')
    } catch (err) {
      setError(err.response?.data?.error || 'Top-up failed')
    } finally {
      setTopUpLoading(false)
    }
  }

  if (loading) return <div className="loading">Loading...</div>
  if (!profile) return <div className="error-msg">{error}</div>

  return (
    <div className="profile-page">
      <h1>Profile</h1>
      <div className="profile-card">
        <div className="profile-field">
          <label>Name</label>
          <p>{profile.name}</p>
        </div>
        <div className="profile-field">
          <label>Email</label>
          <p>{profile.email}</p>
        </div>
        <div className="profile-field">
          <label>Role</label>
          <p>{profile.role}</p>
        </div>
        <div className="profile-field balance-field">
          <label>Balance</label>
          <p className="balance-amount">${profile.balance}</p>
        </div>
      </div>

      <div className="topup-section">
        <h2>Top Up Balance</h2>
        <form onSubmit={handleTopUp} className="topup-form">
          <input
            type="number"
            min="0.01"
            step="0.01"
            placeholder="Amount"
            value={amount}
            onChange={e => setAmount(e.target.value)}
            className="amount-input"
          />
          <button type="submit" className="btn btn-primary" disabled={topUpLoading}>
            {topUpLoading ? 'Processing...' : 'Top Up'}
          </button>
        </form>
        {error && <p className="error-msg">{error}</p>}
        {success && <p className="success-msg">{success}</p>}
      </div>
    </div>
  )
}
