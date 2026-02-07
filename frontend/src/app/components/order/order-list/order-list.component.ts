import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { Order, OrderStatus } from '../../../models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe, DatePipe],
  template: `
    <div class="d-flex justify-content-between align-items-center page-header">
      <h2>Orders</h2>
      <a routerLink="/orders/new" class="btn btn-primary">New Order</a>
    </div>

    <div class="card">
      <div class="card-body">
        @if (loading) {
          <div class="text-center py-4">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
        } @else if (orders.length === 0) {
          <div class="empty-state">
            <p>No orders found.</p>
            <a routerLink="/orders/new" class="btn btn-primary">Create your first order</a>
          </div>
        } @else {
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Customer</th>
                  <th>Date</th>
                  <th>Status</th>
                  <th>Total</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                @for (order of orders; track order.id) {
                  <tr>
                    <td>{{ order.id }}</td>
                    <td>{{ order.customerName }}</td>
                    <td>{{ order.orderDate | date:'short' }}</td>
                    <td>
                      <span [class]="'badge status-badge ' + getStatusClass(order.status)">
                        {{ order.status }}
                      </span>
                    </td>
                    <td>{{ order.totalAmount | currency }}</td>
                    <td>
                      <a [routerLink]="['/orders', order.id]"
                         class="btn btn-sm btn-outline-info btn-action me-1">
                        View
                      </a>
                      @if (order.status === 'PENDING') {
                        <button class="btn btn-sm btn-outline-danger btn-action"
                                (click)="deleteOrder(order)">
                          Delete
                        </button>
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </div>
    </div>
  `
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  loading = true;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.findAll().subscribe({
      next: (data) => {
        this.orders = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.loading = false;
      }
    });
  }

  getStatusClass(status: OrderStatus): string {
    return `status-${status.toLowerCase()}`;
  }

  deleteOrder(order: Order): void {
    if (confirm(`Are you sure you want to delete order #${order.id}?`)) {
      this.orderService.delete(order.id!).subscribe({
        next: () => {
          this.orders = this.orders.filter(o => o.id !== order.id);
        },
        error: (error) => {
          console.error('Error deleting order:', error);
          alert('Failed to delete order.');
        }
      });
    }
  }
}
