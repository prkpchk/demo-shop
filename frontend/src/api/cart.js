import client from './client'

export const getCart = () => client.get('/cart')
export const addToCart = (productId, quantity) =>
  client.post('/cart/items', { productId, quantity })
export const updateCartItem = (id, quantity) =>
  client.put(`/cart/items/${id}`, { quantity })
export const removeCartItem = (id) => client.delete(`/cart/items/${id}`)
