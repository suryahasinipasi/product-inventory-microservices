import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ProductEvent {
  eventType: string;
  productId: number;
  name: string;
  price: number;
  quantity: number;
  occurredAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class EventApiService {
  private readonly apiUrl = 'http:' + '//localhost:8081/api/events';

  constructor(private http: HttpClient) {}

  getEvents(): Observable<ProductEvent[]> {
    return this.http.get<ProductEvent[]>(this.apiUrl);
  }
}
