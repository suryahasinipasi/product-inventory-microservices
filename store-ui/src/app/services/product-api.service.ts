import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

export interface Product {
  id: number;
  name: string;
  price: number;
  quantity: number;
}

export interface ProductRequest {
  name: string;
  price: number;
  quantity: number;
}

@Injectable({
  providedIn: 'root',
})
export class ProductApiService {
  private readonly apiUrl = '/api/products';
  private readonly adminPasswordKey = 'surya-owner-password';

  constructor(private http: HttpClient) {}

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  loginOwner(password: string): Observable<void> {
    return this.http
      .post<void>('/api/admin/login', { password })
      .pipe(
        tap(() => sessionStorage.setItem(this.adminPasswordKey, password)),
      );
  }

  logoutOwner(): void {
    sessionStorage.removeItem(this.adminPasswordKey);
  }

  isOwnerLoggedIn(): boolean {
    return Boolean(sessionStorage.getItem(this.adminPasswordKey));
  }

  createProduct(request: ProductRequest): Observable<Product> {
    return this.http.post<Product>(
      this.apiUrl,
      request,
      { headers: this.adminHeaders() },
    );
  }

  updateProduct(id: number, request: ProductRequest): Observable<Product> {
    return this.http.put<Product>(
      `${this.apiUrl}/${id}`,
      request,
      { headers: this.adminHeaders() },
    );
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`,
      { headers: this.adminHeaders() },
    );
  }

  private adminHeaders(): HttpHeaders {
    return new HttpHeaders({
      'X-Admin-Password':
        sessionStorage.getItem(this.adminPasswordKey) ?? '',
    });
  }
}
