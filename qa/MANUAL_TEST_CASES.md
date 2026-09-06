# Surya Store Manual Test Cases

**Tester:** Surya Hasini Pasi  
**Environment:** Local development  
**Application URL:** http://localhost:4200

## Test Execution Status

| ID | Module | Test scenario | Test steps | Expected result | Status |
|---|---|---|---|---|---|
| TC-SMK-001 | Environment | Verify services are running | Check ports 8080, 8081, 8082 and 4200 | All four ports are listening | PASS |
| TC-SMK-002 | API | Verify APIs are available | Send GET requests to products, inventory, events and orders | Every API returns HTTP 200 | PASS |
| TC-UI-001 | Store | Verify storefront loads | Open the application and select Store | Products and shopping cart are displayed | PASS |
| TC-CART-001 | Cart | Add a product to cart | Select an available product and click Add to Cart | Product appears in cart and cart count increases | NOT RUN |
| TC-CART-002 | Cart | Remove a product from cart | Add a product and click Remove | Product disappears and subtotal is recalculated | NOT RUN |
| TC-CART-003 | Cart | Prevent quantity exceeding stock | Keep increasing a cart item to the available-stock limit | Cart quantity does not exceed product stock | NOT RUN |
| TC-CHK-001 | Checkout | Prevent checkout with an empty cart | Leave cart empty and attempt checkout | “Your cart is empty” message appears | NOT RUN |
| TC-CHK-002 | Checkout | Validate customer details | Add an item but leave name or email empty | Order is not submitted and validation message appears | NOT RUN |
| TC-CHK-003 | Checkout | Place a valid order | Add an item, enter name and email, and submit | Success message with order number appears and cart clears | NOT RUN |
| TC-ORD-001 | Orders | Verify order history | Place an order and open Orders | New order displays with customer, items, total and CREATED status | NOT RUN |
| TC-PRD-001 | Products | Create a product | Enter a valid name, price and quantity and submit | Product appears in the product catalog | NOT RUN |
| TC-PRD-002 | Products | Reject invalid product values | Enter an empty name, zero price or negative quantity | Product submission is prevented | NOT RUN |
| TC-PRD-003 | Products | Update a product | Select Edit, change price or quantity and submit | Product displays updated information | NOT RUN |
| TC-KFK-001 | Kafka | Verify product-created synchronization | Create a product, then open Inventory and Kafka Events | Inventory record and PRODUCT_CREATED event appear | NOT RUN |
| TC-KFK-002 | Kafka | Verify product-updated synchronization | Update a product, then refresh Inventory and Events | Inventory is updated and PRODUCT_UPDATED appears | NOT RUN |
| TC-KFK-003 | Kafka | Verify product-deleted synchronization | Delete a product, then refresh Inventory and Events | Inventory record is removed and PRODUCT_DELETED appears | NOT RUN |

## Status Definitions

- **PASS:** Actual result matches the expected result.
- **FAIL:** Actual result differs from the expected result.
- **BLOCKED:** Testing cannot continue because of another problem.
- **NOT RUN:** Test case has not been executed.