import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

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
  private readonly storageKey = 'surya-store-orders';

  createOrder(request: CreateOrderRequest): Observable<OrderResponse> {
    const order: OrderResponse = {
      id: Date.now(),
      customerName: request.customerName,
      customerEmail: request.customerEmail,
      totalAmount: request.items.reduce(
        (total, item) => total + item.unitPrice * item.quantity,
        0,
      ),
      status: 'PLACED',
      createdAt: new Date().toISOString(),
      items: request.items,
    };

    const orders = this.readOrders();
    localStorage.setItem(
      this.storageKey,
      JSON.stringify([order, ...orders]),
    );

    return of(order);
  }

  getOrders(): Observable<OrderResponse[]> {
    return of(this.readOrders());
  }

  private readOrders(): OrderResponse[] {
    try {
      const storedOrders = localStorage.getItem(this.storageKey);
      return storedOrders ? JSON.parse(storedOrders) : [];
    } catch {
      return [];
    }
  }
}
