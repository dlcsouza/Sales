import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../../../services/customer.service';
import { Customer } from '../../../models/customer.model';

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="d-flex justify-content-between align-items-center page-header">
      <h2>Customers</h2>
      <a routerLink="/customers/new" class="btn btn-primary">Add Customer</a>
    </div>

    <div class="card">
      <div class="card-body">
        @if (loading) {
          <div class="text-center py-4">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
        } @else if (customers.length === 0) {
          <div class="empty-state">
            <p>No customers found.</p>
            <a routerLink="/customers/new" class="btn btn-primary">Add your first customer</a>
          </div>
        } @else {
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Phone</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                @for (customer of customers; track customer.id) {
                  <tr>
                    <td>{{ customer.id }}</td>
                    <td>{{ customer.name }}</td>
                    <td>{{ customer.email }}</td>
                    <td>{{ customer.phone || '-' }}</td>
                    <td>
                      <a [routerLink]="['/customers/edit', customer.id]"
                         class="btn btn-sm btn-outline-primary btn-action me-1">
                        Edit
                      </a>
                      <button class="btn btn-sm btn-outline-danger btn-action"
                              (click)="deleteCustomer(customer)">
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
export class CustomerListComponent implements OnInit {
  customers: Customer[] = [];
  loading = true;

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.customerService.findAll().subscribe({
      next: (data) => {
        this.customers = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading customers:', error);
        this.loading = false;
      }
    });
  }

  deleteCustomer(customer: Customer): void {
    if (confirm(`Are you sure you want to delete ${customer.name}?`)) {
      this.customerService.delete(customer.id!).subscribe({
        next: () => {
          this.customers = this.customers.filter(c => c.id !== customer.id);
        },
        error: (error) => {
          console.error('Error deleting customer:', error);
          alert('Failed to delete customer. It may have associated orders.');
        }
      });
    }
  }
}
