import { CommonModule } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Product,
  ProductApiService,
  ProductRequest
} from './services/product-api.service';
import {
  InventoryApiService,
  InventoryItem
} from './services/inventory-api.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {

  activeView: 'products' | 'inventory' = 'products';

  products: Product[] = [];
  inventoryItems: InventoryItem[] = [];

  productName = '';
  productPrice: number | null = null;
  productQuantity: number | null = null;

  constructor(
    private readonly productApi: ProductApiService,
    private readonly inventoryApi: InventoryApiService,
    private readonly changeDetector: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadInventory();
  }

  showProducts(): void {
    this.activeView = 'products';
  }

  showInventory(): void {
    this.activeView = 'inventory';
    this.loadInventory();
  }

  loadProducts(): void {
    this.productApi.getProducts().subscribe({
      next: products => {
        this.products = products;
        this.changeDetector.markForCheck();
      },
      error: error => {
        console.error('Unable to load products', error);
      }
    });
  }

  loadInventory(): void {
    this.inventoryApi.getInventory().subscribe({
      next: inventoryItems => {
        this.inventoryItems = inventoryItems;
        this.changeDetector.markForCheck();
      },
      error: error => {
        console.error('Unable to load inventory', error);
      }
    });
  }

  get totalProducts(): number {
    return this.products.length;
  }

  get totalStock(): number {
    return this.products.reduce(
      (total, product) => total + product.quantity,
      0
    );
  }

  get inventoryValue(): number {
    return this.products.reduce(
      (total, product) => total + product.price * product.quantity,
      0
    );
  }

  addProduct(): void {
    if (
      !this.productName.trim() ||
      this.productPrice === null ||
      this.productPrice <= 0 ||
      this.productQuantity === null ||
      this.productQuantity < 0
    ) {
      return;
    }

    const request: ProductRequest = {
      name: this.productName.trim(),
      price: this.productPrice,
      quantity: this.productQuantity
    };

    this.productApi.createProduct(request).subscribe({
      next: createdProduct => {
        this.products = [...this.products, createdProduct];
        this.productName = '';
        this.productPrice = null;
        this.productQuantity = null;
        this.changeDetector.markForCheck();

        setTimeout(() => this.loadInventory(), 500);
      },
      error: error => {
        console.error('Unable to create product', error);
      }
    });
  }

  deleteProduct(id: number): void {
    this.productApi.deleteProduct(id).subscribe({
      next: () => {
        this.products = this.products.filter(
          product => product.id !== id
        );
        this.changeDetector.markForCheck();

        setTimeout(() => this.loadInventory(), 500);
      },
      error: error => {
        console.error('Unable to delete product', error);
      }
    });
  }

  trackProduct(index: number, product: Product): number {
    return product.id;
  }

  trackInventory(
    index: number,
    item: InventoryItem
  ): number {
    return item.id;
  }
}
