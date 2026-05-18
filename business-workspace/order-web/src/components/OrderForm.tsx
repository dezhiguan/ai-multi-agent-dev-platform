import React, { useState } from 'react';
import { OrderCreateRequest } from '../types/order';
import { createOrder } from '../api/orderApi';

interface OrderFormProps {
  onSuccess: () => void;
}

const OrderForm: React.FC<OrderFormProps> = ({ onSuccess }) => {
  const [formData, setFormData] = useState<OrderCreateRequest>({
    userId: 0,
    productId: 0,
    quantity: 0,
    totalPrice: 0,
    status: '',
  });
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'quantity' || name === 'totalPrice' || name === 'userId' || name === 'productId' ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await createOrder(formData);
      onSuccess();
    } catch (err) {
      setError('Failed to create order');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>User ID:</label>
        <input type="number" name="userId" value={formData.userId} onChange={handleChange} required />
      </div>
      <div>
        <label>Product ID:</label>
        <input type="number" name="productId" value={formData.productId} onChange={handleChange} required />
      </div>
      <div>
        <label>Quantity:</label>
        <input type="number" name="quantity" value={formData.quantity} onChange={handleChange} required />
      </div>
      <div>
        <label>Total Price:</label>
        <input type="number" step="0.01" name="totalPrice" value={formData.totalPrice} onChange={handleChange} required />
      </div>
      <div>
        <label>Status:</label>
        <input type="text" name="status" value={formData.status} onChange={handleChange} required />
      </div>
      {error && <div style={{ color: 'red' }}>{error}</div>}
      <button type="submit">Submit</button>
    </form>
  );
};

export default OrderForm;