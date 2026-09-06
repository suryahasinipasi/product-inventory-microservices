# Surya Store API Test Cases

**Tester:** Surya Hasini Pasi  
**Environment:** Local development  
**Gateway URL:** http://localhost:4200

## Product API Tests

| ID | Test scenario | Request | Expected result | Status |
|---|---|---|---|---|
| API-PRD-001 | Retrieve all products | GET `/api/products` | HTTP 200 with a JSON array | NOT RUN |
| API-PRD-002 | Retrieve an existing product | GET `/api/products/{id}` | HTTP 200 with the correct product | NOT RUN |
| API-PRD-003 | Retrieve a nonexistent product | GET `/api/products/999999` | HTTP 404 | NOT RUN |
| API-PRD-004 | Create a valid product | POST valid name, price and quantity | HTTP 201 with generated product ID | NOT RUN |
| API-PRD-005 | Create product with blank name | POST with an empty name | HTTP 400 validation error | NOT RUN |
| API-PRD-006 | Create product with zero price | POST with price `0` | HTTP 400 validation error | NOT RUN |
| API-PRD-007 | Create product with negative quantity | POST with quantity `-1` | HTTP 400 validation error | NOT RUN |
| API-PRD-008 | Update an existing product | PUT valid values to `/api/products/{id}` | HTTP 200 with updated values | NOT RUN |
| API-PRD-009 | Update a nonexistent product | PUT to `/api/products/999999` | HTTP 404 | NOT RUN |
| API-PRD-010 | Delete an existing product | DELETE `/api/products/{id}` | HTTP 204 and product is removed | NOT RUN |

## Inventory API Tests

| ID | Test scenario | Request | Expected result | Status |
|---|---|---|---|---|
| API-INV-001 | Retrieve inventory | GET `/api/inventory` | HTTP 200 with a JSON array | NOT RUN |
| API-INV-002 | Retrieve synchronized product | GET `/api/inventory/{productId}` | HTTP 200 with matching product data | NOT RUN |
| API-INV-003 | Retrieve missing inventory | GET `/api/inventory/999999` | HTTP 404 | NOT RUN |

## Kafka Event API Tests

| ID | Test scenario | Request | Expected result | Status |
|---|---|---|---|---|
| API-EVT-001 | Retrieve event history | GET `/api/events` | HTTP 200 with a JSON array | NOT RUN |
| API-EVT-002 | Verify created event | Create a product and GET `/api/events` | PRODUCT_CREATED event appears | NOT RUN |
| API-EVT-003 | Verify updated event | Update a product and GET `/api/events` | PRODUCT_UPDATED event appears | NOT RUN |
| API-EVT-004 | Verify deleted event | Delete a product and GET `/api/events` | PRODUCT_DELETED event appears | NOT RUN |

## Order API Tests

| ID | Test scenario | Request | Expected result | Status |
|---|---|---|---|---|
| API-ORD-001 | Retrieve order history | GET `/api/orders` | HTTP 200 with a JSON array | NOT RUN |
| API-ORD-002 | Create a valid order | POST customer and item information | HTTP 201 with order ID and CREATED status | NOT RUN |
| API-ORD-003 | Verify calculated total | POST multiple items | Total equals sum of price multiplied by quantity | NOT RUN |
| API-ORD-004 | Verify saved order | Create an order and GET `/api/orders` | Created order appears in history | NOT RUN |

## Execution Notes

- Use a temporary product named `QA Test Product`.
- Record the generated product ID.
- Reuse that ID for update, inventory and delete testing.
- Delete the temporary product when testing is complete.
- Replace `NOT RUN` with `PASS`, `FAIL` or `BLOCKED`.
- Save response screenshots or JSON files inside `qa/evidence`.