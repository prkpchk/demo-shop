import client from './client'

export const getProfile = () => client.get('/users/me')
export const updateProfile = (data) => client.put('/users/me', data)
export const topUp = (amount) => client.post('/users/me/top-up', { amount })
