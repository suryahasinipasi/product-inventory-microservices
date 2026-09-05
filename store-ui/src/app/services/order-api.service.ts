import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrderItemRequest {
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
}

export interface CreateOrderRequest {
  customerName: string;
  customerEmail: string;
  items: OrderItemRequest[];
}

export interface OrderItemResponse {
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
}

export interface OrderResponse {
  id: number;
  customerName: string;
  customerEmail: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: OrderItemResponse[];
}

@Injectable({
  providedIn: 'root',
})
export class OrderApiService {
  private readonly apiUrl = 'http:' + '//localhost:8082/api/orders';

  constructor(private readonly http: HttpClient) {}

  createOrder(request: CreateOrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.apiUrl, request);
  }

  getOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(this.apiUrl);
  }
}
