import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { CustomerService } from '../../../services/customer.service';
import { Customer, CustomerRequest } from '../../../models/customer.model';

@Component({
  selector: 'app-customer-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>{{ isEditMode ? 'Edit Customer' : 'New Customer' }}</h2>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #customerForm="ngForm">
          <div class="mb-3">
            <label for="name" class="form-label">Name *</label>
            <input type="text" class="form-control" id="name" name="name"
                   [(ngModel)]="customer.name" required
                   #nameField="ngModel">
            @if (nameField.invalid && nameField.touched) {
              <div class="text-danger">Name is required</div>
            }
          </div>

          <div class="mb-3">
            <label for="email" class="form-label">Email *</label>
            <input type="email" class="form-control" id="email" name="email"
                   [(ngModel)]="customer.email" required email
                   #emailField="ngModel">
            @if (emailField.invalid && emailField.touched) {
              <div class="text-danger">Valid email is required</div>
            }
          </div>

          <div class="mb-3">
            <label for="phone" class="form-label">Phone</label>
            <input type="text" class="form-control" id="phone" name="phone"
                   [(ngModel)]="customer.phone">
          </div>

          <div class="mb-3">
            <label for="address" class="form-label">Address</label>
            <textarea class="form-control" id="address" name="address" rows="3"
                      [(ngModel)]="customer.address"></textarea>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary"
                    [disabled]="customerForm.invalid || submitting">
              {{ submitting ? 'Saving...' : (isEditMode ? 'Update' : 'Create') }}
            </button>
            <a routerLink="/customers" class="btn btn-secondary ms-2">Cancel</a>
          </div>
        </form>
      </div>
    </div>
  `
})
export class CustomerFormComponent implements OnInit {
  customer: CustomerRequest = {
    name: '',
    email: '',
    phone: '',
    address: ''
  };
  isEditMode = false;
  customerId?: number;
  submitting = false;

  constructor(
    private customerService: CustomerService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.customerId = +id;
      this.loadCustomer();
    }
  }

  loadCustomer(): void {
    if (this.customerId) {
      this.customerService.findById(this.customerId).subscribe({
        next: (data) => {
          this.customer = {
            name: data.name,
            email: data.email,
            phone: data.phone || '',
            address: data.address || ''
          };
        },
        error: (error) => {
          console.error('Error loading customer:', error);
          this.router.navigate(['/customers']);
        }
      });
    }
  }

  onSubmit(): void {
    this.submitting = true;

    const operation = this.isEditMode
      ? this.customerService.update(this.customerId!, this.customer)
      : this.customerService.create(this.customer);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/customers']);
      },
      error: (error) => {
        console.error('Error saving customer:', error);
        this.submitting = false;
        alert('Failed to save customer. Email may already exist.');
      }
    });
  }
}
