import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CustomerService } from './customer.service';
import { Customer, CustomerRequest } from '../models/customer.model';
import { environment } from '../../environments/environment';

describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/customers`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CustomerService]
    });
    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all customers', () => {
    const mockCustomers: Customer[] = [
      { id: 1, name: 'John Doe', email: 'john@example.com' },
      { id: 2, name: 'Jane Doe', email: 'jane@example.com' }
    ];

    service.findAll().subscribe(customers => {
      expect(customers.length).toBe(2);
      expect(customers).toEqual(mockCustomers);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockCustomers);
  });

  it('should get customer by id', () => {
    const mockCustomer: Customer = { id: 1, name: 'John Doe', email: 'john@example.com' };

    service.findById(1).subscribe(customer => {
      expect(customer).toEqual(mockCustomer);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCustomer);
  });

  it('should create customer', () => {
    const customerRequest: CustomerRequest = { name: 'John Doe', email: 'john@example.com' };
    const mockResponse: Customer = { id: 1, ...customerRequest };

    service.create(customerRequest).subscribe(customer => {
      expect(customer).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(customerRequest);
    req.flush(mockResponse);
  });

  it('should update customer', () => {
    const customerRequest: CustomerRequest = { name: 'John Updated', email: 'john.updated@example.com' };
    const mockResponse: Customer = { id: 1, ...customerRequest };

    service.update(1, customerRequest).subscribe(customer => {
      expect(customer).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(customerRequest);
    req.flush(mockResponse);
  });

  it('should delete customer', () => {
    service.delete(1).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
