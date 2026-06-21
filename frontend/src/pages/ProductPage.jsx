import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getProduct } from '../api/products'
import { addToCart } from '../api/cart'
import { useAuth } from '../context/AuthContext'

export default function ProductPage() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [product, setProduct] = useState(null)
  const [quantity, setQuantity] = useState(1)
  const [loading, setLoading] = useState(true)
  const [addLoading, setAddLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    getProduct(id)
      .then(res => setProduct(res.data))
      .catch(() => setError('Product not found'))
      .finally(() => setLoading(false))
  }, [id])

  const handleAddToCart = async () => {
    if (!user) { navigate('/login'); return }
    setAddLoading(true)
    setError(null)
    try {
      await addToCart(product.id, quantity)
      setSuccess(true)
      setTimeout(() => setSuccess(false), 2000)
    } catch (err) {
      setError(err.response?.data?.error || 'Could not add to cart')
    } finally {
      setAddLoading(false)
    }
  }

  if (loading) return <div className="loading">Loading...</div>
  if (error && !product) return <div className="error-msg">{error}</div>

  return (
    <div className="product-page">
      <div className="product-detail">
        {product.imageUrl && (
          <img src={product.imageUrl} alt={product.name} className="product-detail-img" />
        )}
        <div className="product-detail-info">
          <p className="product-category">{product.category}</p>
          <h1>{product.name}</h1>
          <p className="product-description">{product.description}</p>
          <p className="product-price-large">${product.price}</p>
          <p className={`product-stock ${product.stock === 0 ? 'out-of-stock' : ''}`}>
            {product.stock > 0 ? `${product.stock} in stock` : 'Out of stock'}
          </p>
          {product.stock > 0 && (
            <div className="add-to-cart">
              <input
                type="number"
                min="1"
                max={product.stock}
                value={quantity}
                onChange={e => setQuantity(Math.min(Number(e.target.value), product.stock))}
                className="quantity-input"
              />
              <button
                onClick={handleAddToCart}
                className="btn btn-primary"
                disabled={addLoading}
              >
                {addLoading ? 'Adding...' : success ? 'Added!' : 'Add to Cart'}
              </button>
            </div>
          )}
          {error && <p className="error-msg">{error}</p>}
        </div>
      </div>
    </div>
  )
}
