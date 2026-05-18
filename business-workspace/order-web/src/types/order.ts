export interface Order {
  id: number;
  userId: number;
  productId: number;
  quantity: number;
  totalPrice: number;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface OrderCreateRequest {
  userId: number;
  productId: number;
  quantity: number;
  totalPrice: number;
  status: string;
}