export interface Customer {
  id?: number;
  name: string;
  email: string;
  phone?: string;
  address?: string;
  createdAt?: Date;
}

export interface CustomerRequest {
  name: string;
  email: string;
  phone?: string;
  address?: string;
}
