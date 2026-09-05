import { Product } from '../services/product-api.service';

export interface CartItem {
  product: Product;
  quantity: number;
}
