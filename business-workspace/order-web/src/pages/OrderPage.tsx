import React, { useState, useEffect } from 'react';
import OrderForm from '../components/OrderForm';
import OrderTable from '../components/OrderTable';
import { Order } from '../types/order';
import { getOrders, getOrderById } from '../api/orderApi';

const OrderPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getOrders();
      setOrders(data);
    } catch (err) {
      setError('Failed to fetch orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleViewDetail = async (id: number) => {
    setLoading(true);
    setError(null);
    try {
      const order = await getOrderById(id);
      setSelectedOrder(order);
      setShowForm(false);
    } catch (err) {
      setError('Failed to fetch order detail');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateOrder = () => {
    setSelectedOrder(null);
    setShowForm(true);
  };

  const handleFormSuccess = () => {
    setShowForm(false);
    fetchOrders();
  };

  const handleBackToList = () => {
    setSelectedOrder(null);
    setShowForm(false);
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (selectedOrder) {
    return (
      <div>
        <h2>Order Detail</h2>
        <p>ID: {selectedOrder.id}</p>
        <p>User ID: {selectedOrder.userId}</p>
        <p>Product ID: {selectedOrder.productId}</p>
        <p>Quantity: {selectedOrder.quantity}</p>
        <p>Total Price: {selectedOrder.totalPrice}</p>
        <p>Status: {selectedOrder.status}</p>
        <p>Created At: {selectedOrder.createdAt}</p>
        <p>Updated At: {selectedOrder.updatedAt}</p>
        <button onClick={handleBackToList}>Back to List</button>
      </div>
    );
  }

  if (showForm) {
    return (
      <div>
        <h2>Create Order</h2>
        <OrderForm onSuccess={handleFormSuccess} />
        <button onClick={handleBackToList}>Cancel</button>
      </div>
    );
  }

  return (
    <div>
      <h1>Order Management</h1>
      <button onClick={handleCreateOrder}>Create Order</button>
      <OrderTable orders={orders} onViewDetail={handleViewDetail} />
    </div>
  );
};

export default OrderPage;