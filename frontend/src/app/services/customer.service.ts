import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer, CustomerRequest } from '../models/customer.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly apiUrl = `${environment.apiUrl}/customers`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.apiUrl);
  }

  findById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/${id}`);
  }

  create(customer: CustomerRequest): Observable<Customer> {
    return this.http.post<Customer>(this.apiUrl, customer);
  }

  update(id: number, customer: CustomerRequest): Observable<Customer> {
    return this.http.put<Customer>(`${this.apiUrl}/${id}`, customer);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
