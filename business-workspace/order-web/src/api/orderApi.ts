import { Order, OrderCreateRequest } from '../types/order';

const BASE_URL = '/api/orders';

export async function getOrders(): Promise<Order[]> {
  const response = await fetch(BASE_URL, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  if (!response.ok) {
    throw new Error('Failed to fetch orders');
  }
  return response.json();
}

export async function getOrderById(id: number): Promise<Order> {
  const response = await fetch(`${BASE_URL}/${id}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  if (!response.ok) {
    throw new Error('Failed to fetch order');
  }
  return response.json();
}

export async function createOrder(order: OrderCreateRequest): Promise<Order> {
  const response = await fetch(BASE_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(order),
  });
  if (!response.ok) {
    throw new Error('Failed to create order');
  }
  return response.json();
}