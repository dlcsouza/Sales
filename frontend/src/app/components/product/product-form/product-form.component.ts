import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../../services/product.service';
import { Product, ProductRequest } from '../../../models/product.model';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>{{ isEditMode ? 'Edit Product' : 'New Product' }}</h2>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #productForm="ngForm">
          <div class="mb-3">
            <label for="name" class="form-label">Name *</label>
            <input type="text" class="form-control" id="name" name="name"
                   [(ngModel)]="product.name" required
                   #nameField="ngModel">
            @if (nameField.invalid && nameField.touched) {
              <div class="text-danger">Name is required</div>
            }
          </div>

          <div class="mb-3">
            <label for="description" class="form-label">Description</label>
            <textarea class="form-control" id="description" name="description" rows="3"
                      [(ngModel)]="product.description"></textarea>
          </div>

          <div class="row">
            <div class="col-md-6">
              <div class="mb-3">
                <label for="price" class="form-label">Price *</label>
                <div class="input-group">
                  <span class="input-group-text">$</span>
                  <input type="number" class="form-control" id="price" name="price"
                         [(ngModel)]="product.price" required min="0.01" step="0.01"
                         #priceField="ngModel">
                </div>
                @if (priceField.invalid && priceField.touched) {
                  <div class="text-danger">Price must be greater than 0</div>
                }
              </div>
            </div>
            <div class="col-md-6">
              <div class="mb-3">
                <label for="stockQuantity" class="form-label">Stock Quantity *</label>
                <input type="number" class="form-control" id="stockQuantity" name="stockQuantity"
                       [(ngModel)]="product.stockQuantity" required min="0"
                       #stockField="ngModel">
                @if (stockField.invalid && stockField.touched) {
                  <div class="text-danger">Stock quantity cannot be negative</div>
                }
              </div>
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary"
                    [disabled]="productForm.invalid || submitting">
              {{ submitting ? 'Saving...' : (isEditMode ? 'Update' : 'Create') }}
            </button>
            <a routerLink="/products" class="btn btn-secondary ms-2">Cancel</a>
          </div>
        </form>
      </div>
    </div>
  `
})
export class ProductFormComponent implements OnInit {
  product: ProductRequest = {
    name: '',
    description: '',
    price: 0,
    stockQuantity: 0
  };
  isEditMode = false;
  productId?: number;
  submitting = false;

  constructor(
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.productId = +id;
      this.loadProduct();
    }
  }

  loadProduct(): void {
    if (this.productId) {
      this.productService.findById(this.productId).subscribe({
        next: (data) => {
          this.product = {
            name: data.name,
            description: data.description || '',
            price: data.price,
            stockQuantity: data.stockQuantity
          };
        },
        error: (error) => {
          console.error('Error loading product:', error);
          this.router.navigate(['/products']);
        }
      });
    }
  }

  onSubmit(): void {
    this.submitting = true;

    const operation = this.isEditMode
      ? this.productService.update(this.productId!, this.product)
      : this.productService.create(this.product);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/products']);
      },
      error: (error) => {
        console.error('Error saving product:', error);
        this.submitting = false;
        alert('Failed to save product.');
      }
    });
  }
}
