```tsx
// main.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

```tsx
// App.tsx
import React from 'react';
import OrderPage from './OrderPage';

const App: React.FC = () => {
  return <OrderPage />;
};

export default App;
```

```tsx
// OrderPage.tsx
import React, { useState, useEffect } from 'react';
import OrderForm from './OrderForm';
import OrderTable from './OrderTable';
import { Order } from './order';
import { fetchOrders, createOrder, updateOrder, deleteOrder } from './orderApi';

const OrderPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [editingOrder, setEditingOrder] = useState<Order | null>(null);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    const data = await fetchOrders();
    setOrders(data);
  };

  const handleCreate = async (order: Omit<Order, 'id'>) => {
    await createOrder(order);
    loadOrders();
  };

  const handleUpdate = async (order: Order) => {
    await updateOrder(order);
    setEditingOrder(null);
    loadOrders();
  };

  const handleDelete = async (id: number) => {
    await deleteOrder(id);
    loadOrders();
  };

  const handleEdit = (order: Order) => {
    setEditingOrder(order);
  };

  return (
    <div>
      <h1>订单管理</h1>
      <OrderForm
        onSubmit={editingOrder ? handleUpdate : handleCreate}
        initialData={editingOrder}
        onCancel={() => setEditingOrder(null)}
      />
      <OrderTable orders={orders} onEdit={handleEdit} onDelete={handleDelete} />
    </div>
  );
};

export default OrderPage;
```

```tsx
// OrderForm.tsx
import React, { useState, useEffect } from 'react';
import { Order } from './order';

interface OrderFormProps {
  onSubmit: (order: Omit<Order, 'id'> | Order) => void;
  initialData?: Order | null;
  onCancel: () => void;
}

const OrderForm: React.FC<OrderFormProps> = ({ onSubmit, initialData, onCancel }) => {
  const [customerName, setCustomerName] = useState('');
  const [product, setProduct] = useState('');
  const [quantity, setQuantity] = useState(0);
  const [price, setPrice] = useState(0);

  useEffect(() => {
    if (initialData) {
      setCustomerName(initialData.customerName);
      setProduct(initialData.product);
      setQuantity(initialData.quantity);
      setPrice(initialData.price);
    } else {
      setCustomerName('');
      setProduct('');
      setQuantity(0);
      setPrice(0);
    }
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (initialData) {
      onSubmit({ ...initialData, customerName, product, quantity, price });
    } else {
      onSubmit({ customerName, product, quantity, price });
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="客户名称"
        value={customerName}
        onChange={(e) => setCustomerName(e.target.value)}
        required
      />
      <input
        type="text"
        placeholder="产品"
        value={product}
        onChange={(e) => setProduct(e.target.value)}
        required
      />
      <input
        type="number"
        placeholder="数量"
        value={quantity}
        onChange={(e) => setQuantity(Number(e.target.value))}
        required
      />
      <input
        type="number"
        placeholder="价格"
        value={price}
        onChange={(e) => setPrice(Number(e.target.value))}
        required
      />
      <button type="submit">{initialData ? '更新' : '创建'}</button>
      {initialData && <button type="button" onClick={onCancel}>取消</button>}
    </form>
  );
};

export default OrderForm;
```

```tsx
// OrderTable.tsx
import React from 'react';
import { Order } from './order';

interface OrderTableProps {
  orders: Order[];
  onEdit: (order: Order) => void;
  onDelete: (id: number) => void;
}

const OrderTable: React.FC<OrderTableProps> = ({ orders, onEdit, onDelete }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>客户名称</th>
          <th>产品</th>
          <th>数量</th>
          <th>价格</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        {orders.map((order) => (
          <tr key={order.id}>
            <td>{order.id}</td>
            <td>{order.customerName}</td>
            <td>{order.product}</td>
            <td>{order.quantity}</td>
            <td>{order.price}</td>
            <td>
              <button onClick={() => onEdit(order)}>编辑</button>
              <button onClick={() => onDelete(order.id)}>删除</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default OrderTable;
```

```tsx
// orderApi.ts
import { Order } from './order';

const BASE_URL = '/api/orders';

export const fetchOrders = async (): Promise<Order[]> => {
  const response = await fetch(BASE_URL);
  return response.json();
};

export const createOrder = async (order: Omit<Order, 'id'>): Promise<Order> => {
  const response = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(order),
  });
  return response.json();
};

export const updateOrder = async (order: Order): Promise<Order> => {
  const response = await fetch(`${BASE_URL}/${order.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(order),
  });
  return response.json();
};

export const deleteOrder = async (id: number): Promise<void> => {
  await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
};
```

```tsx
// order.ts
export interface Order {
  id: number;
  customerName: string;
  product: string;
  quantity: number;
  price: number;
}
```