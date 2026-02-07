import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { OrderService } from './order.service';
import { Order, OrderRequest } from '../models/order.model';
import { environment } from '../../environments/environment';

describe('OrderService', () => {
  let service: OrderService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/orders`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [OrderService]
    });
    service = TestBed.inject(OrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all orders', () => {
    const mockOrders: Order[] = [
      { id: 1, customerId: 1, customerName: 'John', status: 'PENDING', items: [], totalAmount: 100 },
      { id: 2, customerId: 2, customerName: 'Jane', status: 'CONFIRMED', items: [], totalAmount: 200 }
    ];

    service.findAll().subscribe(orders => {
      expect(orders.length).toBe(2);
      expect(orders).toEqual(mockOrders);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockOrders);
  });

  it('should get order by id', () => {
    const mockOrder: Order = {
      id: 1, customerId: 1, customerName: 'John', status: 'PENDING',
      items: [{ productId: 1, productName: 'Product 1', quantity: 2, unitPrice: 50, subtotal: 100 }],
      totalAmount: 100
    };

    service.findById(1).subscribe(order => {
      expect(order).toEqual(mockOrder);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockOrder);
  });

  it('should get orders by customer id', () => {
    const mockOrders: Order[] = [
      { id: 1, customerId: 1, customerName: 'John', status: 'PENDING', items: [], totalAmount: 100 }
    ];

    service.findByCustomerId(1).subscribe(orders => {
      expect(orders).toEqual(mockOrders);
    });

    const req = httpMock.expectOne(`${apiUrl}/customer/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockOrders);
  });

  it('should get orders by status', () => {
    const mockOrders: Order[] = [
      { id: 1, customerId: 1, customerName: 'John', status: 'PENDING', items: [], totalAmount: 100 }
    ];

    service.findByStatus('PENDING').subscribe(orders => {
      expect(orders).toEqual(mockOrders);
    });

    const req = httpMock.expectOne(`${apiUrl}/status/PENDING`);
    expect(req.request.method).toBe('GET');
    req.flush(mockOrders);
  });

  it('should create order', () => {
    const orderRequest: OrderRequest = {
      customerId: 1,
      items: [{ productId: 1, quantity: 2 }]
    };
    const mockResponse: Order = {
      id: 1, customerId: 1, customerName: 'John', status: 'PENDING',
      items: [{ productId: 1, productName: 'Product 1', quantity: 2, unitPrice: 50, subtotal: 100 }],
      totalAmount: 100
    };

    service.create(orderRequest).subscribe(order => {
      expect(order).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(orderRequest);
    req.flush(mockResponse);
  });

  it('should update order status', () => {
    const mockResponse: Order = {
      id: 1, customerId: 1, customerName: 'John', status: 'CONFIRMED',
      items: [], totalAmount: 100
    };

    service.updateStatus(1, 'CONFIRMED').subscribe(order => {
      expect(order.status).toBe('CONFIRMED');
    });

    const req = httpMock.expectOne(`${apiUrl}/1/status`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ status: 'CONFIRMED' });
    req.flush(mockResponse);
  });

  it('should delete order', () => {
    service.delete(1).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
