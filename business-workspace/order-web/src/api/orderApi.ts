import { Order } from '../types/order';

const BASE_URL = '/api/orders';

export const fetchOrders = async (): Promise<Order[]> => {
  const response = await fetch(BASE_URL);
  if (!response.ok) throw new Error('获取订单列表失败');
  return response.json();
};

export const fetchOrderById = async (id: number): Promise<Order> => {
  const response = await fetch(`${BASE_URL}/${id}`);
  if (!response.ok) throw new Error('获取订单详情失败');
  return response.json();
};

export const createOrder = async (order: Omit<Order, 'id' | 'created_at' | 'updated_at'>): Promise<Order> => {
  const response = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(order),
  });
  if (!response.ok) throw new Error('创建订单失败');
  return response.json();
};