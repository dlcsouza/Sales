export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItem {
  id?: number;
  productId: number;
  productName?: string;
  quantity: number;
  unitPrice: number;
  subtotal?: number;
}

export interface Order {
  id?: number;
  customerId: number;
  customerName?: string;
  orderDate?: Date;
  status: OrderStatus;
  totalAmount?: number;
  items: OrderItem[];
}

export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface OrderRequest {
  customerId: number;
  items: OrderItemRequest[];
}

export interface OrderStatusUpdate {
  status: OrderStatus;
}
