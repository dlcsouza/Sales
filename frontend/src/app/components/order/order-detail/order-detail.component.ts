import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { Order, OrderStatus } from '../../../models/order.model';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CurrencyPipe, DatePipe],
  template: `
    @if (loading) {
      <div class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    } @else if (order) {
      <div class="d-flex justify-content-between align-items-center page-header">
        <h2>Order #{{ order.id }}</h2>
        <a routerLink="/orders" class="btn btn-secondary">Back to Orders</a>
      </div>

      <div class="row">
        <div class="col-md-8">
          <div class="card mb-4">
            <div class="card-header">
              <h5 class="mb-0">Order Items</h5>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table">
                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>Unit Price</th>
                      <th>Quantity</th>
                      <th>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (item of order.items; track item.id) {
                      <tr>
                        <td>{{ item.productName }}</td>
                        <td>{{ item.unitPrice | currency }}</td>
                        <td>{{ item.quantity }}</td>
                        <td>{{ item.subtotal | currency }}</td>
                      </tr>
                    }
                  </tbody>
                  <tfoot>
                    <tr>
                      <th colspan="3" class="text-end">Total:</th>
                      <th>{{ order.totalAmount | currency }}</th>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="card mb-4">
            <div class="card-header">
              <h5 class="mb-0">Order Details</h5>
            </div>
            <div class="card-body">
              <dl class="row mb-0">
                <dt class="col-sm-5">Customer:</dt>
                <dd class="col-sm-7">{{ order.customerName }}</dd>

                <dt class="col-sm-5">Date:</dt>
                <dd class="col-sm-7">{{ order.orderDate | date:'medium' }}</dd>

                <dt class="col-sm-5">Status:</dt>
                <dd class="col-sm-7">
                  <span [class]="'badge status-badge ' + getStatusClass(order.status)">
                    {{ order.status }}
                  </span>
                </dd>

                <dt class="col-sm-5">Total:</dt>
                <dd class="col-sm-7 fw-bold">{{ order.totalAmount | currency }}</dd>
              </dl>
            </div>
          </div>

          @if (order.status !== 'CANCELLED' && order.status !== 'DELIVERED') {
            <div class="card">
              <div class="card-header">
                <h5 class="mb-0">Update Status</h5>
              </div>
              <div class="card-body">
                <div class="mb-3">
                  <select class="form-select" [(ngModel)]="newStatus">
                    @for (status of availableStatuses; track status) {
                      <option [value]="status">{{ status }}</option>
                    }
                  </select>
                </div>
                <button class="btn btn-primary w-100"
                        [disabled]="updating || newStatus === order.status"
                        (click)="updateStatus()">
                  {{ updating ? 'Updating...' : 'Update Status' }}
                </button>
              </div>
            </div>
          }
        </div>
      </div>
    }
  `
})
export class OrderDetailComponent implements OnInit {
  order?: Order;
  loading = true;
  updating = false;
  newStatus: OrderStatus = 'PENDING';

  availableStatuses: OrderStatus[] = [
    'PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'
  ];

  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadOrder(+id);
    } else {
      this.router.navigate(['/orders']);
    }
  }

  loadOrder(id: number): void {
    this.loading = true;
    this.orderService.findById(id).subscribe({
      next: (data) => {
        this.order = data;
        this.newStatus = data.status;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading order:', error);
        this.router.navigate(['/orders']);
      }
    });
  }

  getStatusClass(status: OrderStatus): string {
    return `status-${status.toLowerCase()}`;
  }

  updateStatus(): void {
    if (!this.order || this.newStatus === this.order.status) return;

    this.updating = true;
    this.orderService.updateStatus(this.order.id!, this.newStatus).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.updating = false;
      },
      error: (error) => {
        console.error('Error updating status:', error);
        this.updating = false;
        alert('Failed to update order status.');
      }
    });
  }
}
