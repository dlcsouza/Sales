import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, ProductRequest } from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  findById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  findByName(name: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/search`, { params: { name } });
  }

  findInStock(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/in-stock`);
  }

  create(product: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  update(id: number, product: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
