import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/customers', pathMatch: 'full' },
  {
    path: 'customers',
    loadComponent: () => import('./components/customer/customer-list/customer-list.component')
      .then(m => m.CustomerListComponent)
  },
  {
    path: 'customers/new',
    loadComponent: () => import('./components/customer/customer-form/customer-form.component')
      .then(m => m.CustomerFormComponent)
  },
  {
    path: 'customers/edit/:id',
    loadComponent: () => import('./components/customer/customer-form/customer-form.component')
      .then(m => m.CustomerFormComponent)
  },
  {
    path: 'products',
    loadComponent: () => import('./components/product/product-list/product-list.component')
      .then(m => m.ProductListComponent)
  },
  {
    path: 'products/new',
    loadComponent: () => import('./components/product/product-form/product-form.component')
      .then(m => m.ProductFormComponent)
  },
  {
    path: 'products/edit/:id',
    loadComponent: () => import('./components/product/product-form/product-form.component')
      .then(m => m.ProductFormComponent)
  },
  {
    path: 'orders',
    loadComponent: () => import('./components/order/order-list/order-list.component')
      .then(m => m.OrderListComponent)
  },
  {
    path: 'orders/new',
    loadComponent: () => import('./components/order/order-form/order-form.component')
      .then(m => m.OrderFormComponent)
  },
  {
    path: 'orders/:id',
    loadComponent: () => import('./components/order/order-detail/order-detail.component')
      .then(m => m.OrderDetailComponent)
  }
];
