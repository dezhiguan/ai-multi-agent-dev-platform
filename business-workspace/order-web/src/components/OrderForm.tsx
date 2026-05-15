import React, { useState } from 'react';
import { Order } from '../types/order';

interface OrderFormProps {
  onSubmit: (order: Omit<Order, 'id' | 'created_at' | 'updated_at'>) => void;
  onCancel: () => void;
}

export const OrderForm: React.FC<OrderFormProps> = ({ onSubmit, onCancel }) => {
  const [user_id, setUserId] = useState<number>(0);
  const [product_id, setProductId] = useState<number>(0);
  const [quantity, setQuantity] = useState<number>(1);
  const [total_price, setTotalPrice] = useState<number>(0);
  const [status, setStatus] = useState<string>('pending');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ user_id, product_id, quantity, total_price, status });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>用户ID:</label>
        <input type="number" value={user_id} onChange={e => setUserId(Number(e.target.value))} required />
      </div>
      <div>
        <label>产品ID:</label>
        <input type="number" value={product_id} onChange={e => setProductId(Number(e.target.value))} required />
      </div>
      <div>
        <label>数量:</label>
        <input type="number" value={quantity} onChange={e => setQuantity(Number(e.target.value))} min="1" required />
      </div>
      <div>
        <label>总价:</label>
        <input type="number" value={total_price} onChange={e => setTotalPrice(Number(e.target.value))} min="0" step="0.01" required />
      </div>
      <div>
        <label>状态:</label>
        <select value={status} onChange={e => setStatus(e.target.value)}>
          <option value="pending">待处理</option>
          <option value="completed">已完成</option>
          <option value="cancelled">已取消</option>
        </select>
      </div>
      <button type="submit">提交</button>
      <button type="button" onClick={onCancel}>取消</button>
    </form>
  );
};