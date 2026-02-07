import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { Product, ProductRequest } from '../models/product.model';
import { environment } from '../../environments/environment';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/products`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService]
    });
    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all products', () => {
    const mockProducts: Product[] = [
      { id: 1, name: 'Product 1', price: 99.99, stockQuantity: 100 },
      { id: 2, name: 'Product 2', price: 49.99, stockQuantity: 50 }
    ];

    service.findAll().subscribe(products => {
      expect(products.length).toBe(2);
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  it('should get product by id', () => {
    const mockProduct: Product = { id: 1, name: 'Product 1', price: 99.99, stockQuantity: 100 };

    service.findById(1).subscribe(product => {
      expect(product).toEqual(mockProduct);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProduct);
  });

  it('should search products by name', () => {
    const mockProducts: Product[] = [
      { id: 1, name: 'Test Product', price: 99.99, stockQuantity: 100 }
    ];

    service.findByName('Test').subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(`${apiUrl}/search?name=Test`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  it('should get products in stock', () => {
    const mockProducts: Product[] = [
      { id: 1, name: 'Product 1', price: 99.99, stockQuantity: 100 }
    ];

    service.findInStock().subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(`${apiUrl}/in-stock`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  it('should create product', () => {
    const productRequest: ProductRequest = { name: 'New Product', price: 99.99, stockQuantity: 100 };
    const mockResponse: Product = { id: 1, ...productRequest };

    service.create(productRequest).subscribe(product => {
      expect(product).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(productRequest);
    req.flush(mockResponse);
  });

  it('should update product', () => {
    const productRequest: ProductRequest = { name: 'Updated Product', price: 149.99, stockQuantity: 200 };
    const mockResponse: Product = { id: 1, ...productRequest };

    service.update(1, productRequest).subscribe(product => {
      expect(product).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(productRequest);
    req.flush(mockResponse);
  });

  it('should delete product', () => {
    service.delete(1).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
