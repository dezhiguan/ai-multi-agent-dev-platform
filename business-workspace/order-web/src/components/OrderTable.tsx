import React from 'react';
import { Order } from '../types/order';

interface OrderTableProps {
  orders: Order[];
  onViewDetail: (id: number) => void;
}

const OrderTable: React.FC<OrderTableProps> = ({ orders, onViewDetail }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>User ID</th>
          <th>Product ID</th>
          <th>Quantity</th>
          <th>Total Price</th>
          <th>Status</th>
          <th>Created At</th>
          <th>Updated At</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {orders.map((order) => (
          <tr key={order.id}>
            <td>{order.id}</td>
            <td>{order.userId}</td>
            <td>{order.productId}</td>
            <td>{order.quantity}</td>
            <td>{order.totalPrice}</td>
            <td>{order.status}</td>
            <td>{order.createdAt}</td>
            <td>{order.updatedAt}</td>
            <td>
              <button onClick={() => onViewDetail(order.id)}>View Detail</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default OrderTable;