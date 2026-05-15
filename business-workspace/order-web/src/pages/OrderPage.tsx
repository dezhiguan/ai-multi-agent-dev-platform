import React, { useState, useEffect } from 'react';
import { OrderTable } from '../components/OrderTable';
import { OrderForm } from '../components/OrderForm';
import { fetchOrders, createOrder, fetchOrderById } from '../api/orderApi';
import { Order } from '../types/order';

const OrderPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [selectedOrderId, setSelectedOrderId] = useState<number | null>(null);
  const [view, setView] = useState<'list' | 'create' | 'detail'>('list');

  useEffect(() => {
    if (view === 'list') {
      fetchOrders().then(setOrders);
    }
  }, [view]);

  const handleCreate = async (order: Omit<Order, 'id' | 'created_at' | 'updated_at'>) => {
    await createOrder(order);
    setView('list');
  };

  const handleViewDetail = (id: number) => {
    setSelectedOrderId(id);
    setView('detail');
  };

  const handleBack = () => {
    setView('list');
    setSelectedOrderId(null);
  };

  return (
    <div>
      <h1>订单管理系统</h1>
      {view === 'list' && (
        <>
          <button onClick={() => setView('create')}>创建订单</button>
          <OrderTable orders={orders} onViewDetail={handleViewDetail} />
        </>
      )}
      {view === 'create' && <OrderForm onSubmit={handleCreate} onCancel={handleBack} />}
      {view === 'detail' && selectedOrderId && <OrderDetail id={selectedOrderId} onBack={handleBack} />}
    </div>
  );
};

const OrderDetail: React.FC<{ id: number; onBack: () => void }> = ({ id, onBack }) => {
  const [order, setOrder] = useState<Order | null>(null);

  useEffect(() => {
    fetchOrderById(id).then(setOrder);
  }, [id]);

  if (!order) return <div>加载中...</div>;

  return (
    <div>
      <h2>订单详情</h2>
      <p>ID: {order.id}</p>
      <p>用户ID: {order.user_id}</p>
      <p>产品ID: {order.product_id}</p>
      <p>数量: {order.quantity}</p>
      <p>总价: {order.total_price}</p>
      <p>状态: {order.status}</p>
      <p>创建时间: {order.created_at}</p>
      <p>更新时间: {order.updated_at}</p>
      <button onClick={onBack}>返回</button>
    </div>
  );
};

export default OrderPage;