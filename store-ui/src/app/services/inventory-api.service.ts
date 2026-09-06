import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface InventoryItem {
  id: number;
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class InventoryApiService {
  private readonly apiUrl = '/api/inventory';

  constructor(private http: HttpClient) {}

  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(this.apiUrl);
  }

  getInventoryByProductId(productId: number): Observable<InventoryItem> {
    return this.http.get<InventoryItem>(`${this.apiUrl}/${productId}`);
  }
}
