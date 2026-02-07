export interface Product {
  id?: number;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  createdAt?: Date;
}

export interface ProductRequest {
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
}
