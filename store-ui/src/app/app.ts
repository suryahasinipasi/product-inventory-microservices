import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CartItem } from './models/cart-item';
import { Product, ProductApiService, ProductRequest } from './services/product-api.service';
import { InventoryApiService, InventoryItem } from './services/inventory-api.service';
import { EventApiService, ProductEvent } from './services/event-api.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  activeView: 'storefront' | 'products' | 'inventory' | 'events' = 'products';

  products: Product[] = [];
  inventoryItems: InventoryItem[] = [];
  events: ProductEvent[] = [];
  cartItems: CartItem[] = [];

  productName = '';
  productPrice: number | null = null;
  productQuantity: number | null = null;
  editingProductId: number | null = null;

  constructor(
    private readonly productApi: ProductApiService,
    private readonly inventoryApi: InventoryApiService,
    private readonly eventApi: EventApiService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadInventory();
    this.loadEvents();
  }

  showStorefront(): void {
    this.activeView = 'storefront';
  }

  showProducts(): void {
    this.activeView = 'products';
  }

  showInventory(): void {
    this.activeView = 'inventory';
    this.loadInventory();
  }

  showEvents(): void {
    this.activeView = 'events';
    this.loadEvents();
  }

  loadProducts(): void {
    this.productApi.getProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.changeDetector.markForCheck();
      },
      error: (error) => {
        console.error('Unable to load products', error);
      },
    });
  }

  loadInventory(): void {
    this.inventoryApi.getInventory().subscribe({
      next: (inventoryItems) => {
        this.inventoryItems = inventoryItems;
        this.changeDetector.markForCheck();
      },
      error: (error) => {
        console.error('Unable to load inventory', error);
      },
    });
  }

  loadEvents(): void {
    this.eventApi.getEvents().subscribe({
      next: (events) => {
        this.events = events;
        this.changeDetector.markForCheck();
      },
      error: (error) => {
        console.error('Unable to load Kafka events', error);
      },
    });
  }

  get totalProducts(): number {
    return this.products.length;
  }

  get totalStock(): number {
    return this.products.reduce((total, product) => total + product.quantity, 0);
  }

  get inventoryValue(): number {
    return this.products.reduce((total, product) => total + product.price * product.quantity, 0);
  }

  get cartCount(): number {
    return this.cartItems.reduce((total, item) => total + item.quantity, 0);
  }

  get cartSubtotal(): number {
    return this.cartItems.reduce((total, item) => total + item.product.price * item.quantity, 0);
  }

  addToCart(product: Product): void {
    if (product.quantity <= 0) {
      return;
    }

    const existingItem = this.cartItems.find((item) => item.product.id === product.id);

    if (existingItem) {
      this.cartItems = this.cartItems.map((item) =>
        item.product.id === product.id
          ? {
              ...item,
              quantity: Math.min(item.quantity + 1, product.quantity),
            }
          : item,
      );
    } else {
      this.cartItems = [
        ...this.cartItems,
        {
          product,
          quantity: 1,
        },
      ];
    }

    this.changeDetector.markForCheck();
  }

  decreaseCartQuantity(productId: number): void {
    this.cartItems = this.cartItems
      .map((item) =>
        item.product.id === productId
          ? {
              ...item,
              quantity: item.quantity - 1,
            }
          : item,
      )
      .filter((item) => item.quantity > 0);

    this.changeDetector.markForCheck();
  }

  removeFromCart(productId: number): void {
    this.cartItems = this.cartItems.filter((item) => item.product.id !== productId);

    this.changeDetector.markForCheck();
  }

  clearCart(): void {
    this.cartItems = [];
    this.changeDetector.markForCheck();
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
      quantity: this.productQuantity,
    };

    if (this.editingProductId !== null) {
      const productId = this.editingProductId;

      this.productApi.updateProduct(productId, request).subscribe({
        next: (updatedProduct) => {
          this.products = this.products.map((product) =>
            product.id === productId ? updatedProduct : product,
          );

          this.cartItems = this.cartItems
            .map((item) =>
              item.product.id === productId
                ? {
                    product: updatedProduct,
                    quantity: Math.min(item.quantity, updatedProduct.quantity),
                  }
                : item,
            )
            .filter((item) => item.quantity > 0);

          this.resetForm();
          this.changeDetector.markForCheck();
          this.refreshEventData();
        },
        error: (error) => {
          console.error('Unable to update product', error);
        },
      });

      return;
    }

    this.productApi.createProduct(request).subscribe({
      next: (createdProduct) => {
        this.products = [...this.products, createdProduct];

        this.resetForm();
        this.changeDetector.markForCheck();
        this.refreshEventData();
      },
      error: (error) => {
        console.error('Unable to create product', error);
      },
    });
  }

  editProduct(product: Product): void {
    this.editingProductId = product.id;
    this.productName = product.name;
    this.productPrice = product.price;
    this.productQuantity = product.quantity;
  }

  cancelEdit(): void {
    this.resetForm();
  }

  deleteProduct(id: number): void {
    this.productApi.deleteProduct(id).subscribe({
      next: () => {
        this.products = this.products.filter((product) => product.id !== id);

        this.removeFromCart(id);

        if (this.editingProductId === id) {
          this.resetForm();
        }

        this.changeDetector.markForCheck();
        this.refreshEventData();
      },
      error: (error) => {
        console.error('Unable to delete product', error);
      },
    });
  }

  private refreshEventData(): void {
    setTimeout(() => {
      this.loadInventory();
      this.loadEvents();
    }, 500);
  }

  private resetForm(): void {
    this.editingProductId = null;
    this.productName = '';
    this.productPrice = null;
    this.productQuantity = null;
  }

  trackProduct(index: number, product: Product): number {
    return product.id;
  }

  trackInventory(index: number, item: InventoryItem): number {
    return item.id;
  }

  trackEvent(index: number, event: ProductEvent): string {
    return `${event.productId}-${event.occurredAt}-${index}`;
  }

  trackCart(index: number, item: CartItem): number {
    return item.product.id;
  }
}

