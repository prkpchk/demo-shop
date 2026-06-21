import client from './client'

export const placeOrder = () => client.post('/orders')
export const getOrders = () => client.get('/orders')
export const getOrder = (id) => client.get(`/orders/${id}`)
export const payOrder = (id, simulateFailure = false) =>
  client.post(`/orders/${id}/pay`, null, { params: { simulateFailure } })
