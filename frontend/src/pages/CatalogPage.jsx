import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getProducts } from '../api/products'

export default function CatalogPage() {
  const [products, setProducts] = useState([])
  const [category, setCategory] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setLoading(true)
    getProducts({ category: category || undefined, size: 20 })
      .then(res => setProducts(res.data.content))
      .catch(() => setError('Failed to load products'))
      .finally(() => setLoading(false))
  }, [category])

  const categories = ['', 'Electronics', 'Home', 'Stationery', 'Accessories', 'Bags']

  if (loading) return <div className="loading">Loading...</div>
  if (error) return <div className="error-msg">{error}</div>

  return (
    <div className="catalog-page">
      <div className="catalog-header">
        <h1>Products</h1>
        <div className="category-filter">
          {categories.map(cat => (
            <button
              key={cat}
              onClick={() => setCategory(cat)}
              className={`btn-chip ${category === cat ? 'active' : ''}`}
            >
              {cat || 'All'}
            </button>
          ))}
        </div>
      </div>
      <div className="product-grid">
        {products.map(product => (
          <Link to={`/products/${product.id}`} key={product.id} className="product-card">
            {product.imageUrl && (
              <img src={product.imageUrl} alt={product.name} className="product-img" />
            )}
            <div className="product-info">
              <h3>{product.name}</h3>
              <p className="product-category">{product.category}</p>
              <p className="product-price">${product.price}</p>
              <p className={`product-stock ${product.stock === 0 ? 'out-of-stock' : ''}`}>
                {product.stock > 0 ? `${product.stock} in stock` : 'Out of stock'}
              </p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  )
}
