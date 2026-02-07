import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderRequest, OrderStatus, OrderStatusUpdate } from '../models/order.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly apiUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl);
  }

  findById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  findByCustomerId(customerId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  findByStatus(status: OrderStatus): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/status/${status}`);
  }

  create(order: OrderRequest): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, order);
  }

  updateStatus(id: number, status: OrderStatus): Observable<Order> {
    const body: OrderStatusUpdate = { status };
    return this.http.put<Order>(`${this.apiUrl}/${id}/status`, body);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
