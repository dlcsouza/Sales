import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { Product } from '../../../models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe],
  template: `
    <div class="d-flex justify-content-between align-items-center page-header">
      <h2>Products</h2>
      <a routerLink="/products/new" class="btn btn-primary">Add Product</a>
    </div>

    <div class="card">
      <div class="card-body">
        @if (loading) {
          <div class="text-center py-4">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
        } @else if (products.length === 0) {
          <div class="empty-state">
            <p>No products found.</p>
            <a routerLink="/products/new" class="btn btn-primary">Add your first product</a>
          </div>
        } @else {
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Price</th>
                  <th>Stock</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                @for (product of products; track product.id) {
                  <tr>
                    <td>{{ product.id }}</td>
                    <td>{{ product.name }}</td>
                    <td>{{ product.description || '-' }}</td>
                    <td>{{ product.price | currency }}</td>
                    <td>
                      <span [class]="getStockClass(product.stockQuantity)">
                        {{ product.stockQuantity }}
                      </span>
                    </td>
                    <td>
                      <a [routerLink]="['/products/edit', product.id]"
                         class="btn btn-sm btn-outline-primary btn-action me-1">
                        Edit
                      </a>
                      <button class="btn btn-sm btn-outline-danger btn-action"
                              (click)="deleteProduct(product)">
                        Delete
                      </button>
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
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  loading = true;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.findAll().subscribe({
      next: (data) => {
        this.products = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.loading = false;
      }
    });
  }

  getStockClass(quantity: number): string {
    if (quantity === 0) return 'badge bg-danger';
    if (quantity < 10) return 'badge bg-warning text-dark';
    return 'badge bg-success';
  }

  deleteProduct(product: Product): void {
    if (confirm(`Are you sure you want to delete ${product.name}?`)) {
      this.productService.delete(product.id!).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== product.id);
        },
        error: (error) => {
          console.error('Error deleting product:', error);
          alert('Failed to delete product. It may be used in orders.');
        }
      });
    }
  }
}
