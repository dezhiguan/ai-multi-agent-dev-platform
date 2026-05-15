import React from 'react';
import { Order } from '../types/order';

interface OrderTableProps {
  orders: Order[];
  onViewDetail: (id: number) => void;
}

export const OrderTable: React.FC<OrderTableProps> = ({ orders, onViewDetail }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>用户ID</th>
          <th>产品ID</th>
          <th>数量</th>
          <th>总价</th>
          <th>状态</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        {orders.map(order => (
          <tr key={order.id}>
            <td>{order.id}</td>
            <td>{order.user_id}</td>
            <td>{order.product_id}</td>
            <td>{order.quantity}</td>
            <td>{order.total_price}</td>
            <td>{order.status}</td>
            <td>{order.created_at}</td>
            <td><button onClick={() => onViewDetail(order.id)}>查看详情</button></td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};