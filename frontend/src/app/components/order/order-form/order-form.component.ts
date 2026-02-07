import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OrderService } from '../../../services/order.service';
import { CustomerService } from '../../../services/customer.service';
import { ProductService } from '../../../services/product.service';
import { Customer } from '../../../models/customer.model';
import { Product } from '../../../models/product.model';
import { OrderRequest, OrderItemRequest } from '../../../models/order.model';

interface OrderItemForm {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
  maxStock: number;
}

@Component({
  selector: 'app-order-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CurrencyPipe],
  template: `
    <div class="page-header">
      <h2>New Order</h2>
    </div>

    <div class="card mb-4">
      <div class="card-header">
        <h5 class="mb-0">Customer</h5>
      </div>
      <div class="card-body">
        <div class="mb-3">
          <label for="customer" class="form-label">Select Customer *</label>
          <select class="form-select" id="customer" name="customer"
                  [(ngModel)]="selectedCustomerId" required>
            <option value="">-- Select a customer --</option>
            @for (customer of customers; track customer.id) {
              <option [value]="customer.id">{{ customer.name }} ({{ customer.email }})</option>
            }
          </select>
        </div>
      </div>
    </div>

    <div class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Items</h5>
      </div>
      <div class="card-body">
        <div class="row mb-3">
          <div class="col-md-6">
            <label for="product" class="form-label">Add Product</label>
            <select class="form-select" id="product" name="product"
                    [(ngModel)]="selectedProductId" (change)="onProductSelect()">
              <option value="">-- Select a product --</option>
              @for (product of availableProducts; track product.id) {
                <option [value]="product.id">
                  {{ product.name }} - {{ product.price | currency }} (Stock: {{ product.stockQuantity }})
                </option>
              }
            </select>
          </div>
        </div>

        @if (orderItems.length > 0) {
          <div class="table-responsive">
            <table class="table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Price</th>
                  <th width="150">Quantity</th>
                  <th>Subtotal</th>
                  <th width="80">Actions</th>
                </tr>
              </thead>
              <tbody>
                @for (item of orderItems; track item.productId) {
                  <tr>
                    <td>{{ item.productName }}</td>
                    <td>{{ item.price | currency }}</td>
                    <td>
                      <input type="number" class="form-control form-control-sm"
                             [(ngModel)]="item.quantity"
                             [name]="'qty_' + item.productId"
                             min="1" [max]="item.maxStock"
                             (change)="recalculateTotal()">
                    </td>
                    <td>{{ item.price * item.quantity | currency }}</td>
                    <td>
                      <button class="btn btn-sm btn-outline-danger"
                              (click)="removeItem(item)">
                        Remove
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
              <tfoot>
                <tr>
                  <th colspan="3" class="text-end">Total:</th>
                  <th colspan="2">{{ totalAmount | currency }}</th>
                </tr>
              </tfoot>
            </table>
          </div>
        } @else {
          <p class="text-muted text-center py-3">No items added yet. Select a product above.</p>
        }
      </div>
    </div>

    <div class="form-actions">
      <button type="button" class="btn btn-primary"
              [disabled]="!isFormValid() || submitting"
              (click)="onSubmit()">
        {{ submitting ? 'Creating...' : 'Create Order' }}
      </button>
      <a routerLink="/orders" class="btn btn-secondary ms-2">Cancel</a>
    </div>
  `
})
export class OrderFormComponent implements OnInit {
  customers: Customer[] = [];
  products: Product[] = [];
  orderItems: OrderItemForm[] = [];

  selectedCustomerId = '';
  selectedProductId = '';
  totalAmount = 0;
  submitting = false;

  constructor(
    private orderService: OrderService,
    private customerService: CustomerService,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.loadProducts();
  }

  loadCustomers(): void {
    this.customerService.findAll().subscribe({
      next: (data) => this.customers = data,
      error: (error) => console.error('Error loading customers:', error)
    });
  }

  loadProducts(): void {
    this.productService.findInStock().subscribe({
      next: (data) => this.products = data,
      error: (error) => console.error('Error loading products:', error)
    });
  }

  get availableProducts(): Product[] {
    const usedProductIds = this.orderItems.map(i => i.productId);
    return this.products.filter(p => !usedProductIds.includes(p.id!));
  }

  onProductSelect(): void {
    if (!this.selectedProductId) return;

    const product = this.products.find(p => p.id === +this.selectedProductId);
    if (product) {
      this.orderItems.push({
        productId: product.id!,
        productName: product.name,
        quantity: 1,
        price: product.price,
        maxStock: product.stockQuantity
      });
      this.recalculateTotal();
    }
    this.selectedProductId = '';
  }

  removeItem(item: OrderItemForm): void {
    this.orderItems = this.orderItems.filter(i => i.productId !== item.productId);
    this.recalculateTotal();
  }

  recalculateTotal(): void {
    this.totalAmount = this.orderItems.reduce(
      (sum, item) => sum + (item.price * item.quantity), 0
    );
  }

  isFormValid(): boolean {
    return !!this.selectedCustomerId && this.orderItems.length > 0;
  }

  onSubmit(): void {
    if (!this.isFormValid()) return;

    this.submitting = true;

    const orderRequest: OrderRequest = {
      customerId: +this.selectedCustomerId,
      items: this.orderItems.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      }))
    };

    this.orderService.create(orderRequest).subscribe({
      next: (order) => {
        this.router.navigate(['/orders', order.id]);
      },
      error: (error) => {
        console.error('Error creating order:', error);
        this.submitting = false;
        alert('Failed to create order. Check stock availability.');
      }
    });
  }
}
