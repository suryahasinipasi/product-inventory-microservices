# Product Service

This is the first microservice in an event-driven e-commerce learning project. It provides REST APIs for creating, reading, updating, deleting, sorting, and filtering products.

## What this stage teaches

- Java classes, records, methods, constructors, collections, and Streams
- Spring components and constructor dependency injection
- REST controllers and CRUD HTTP methods
- Input validation and centralized exception handling
- H2 relational database, JPA entities, and Spring Data repositories
- Read-only and write transactions with `@Transactional`
- Unit tests with JUnit 5 and Mockito

## Application flow

```text
HTTP request
    -> ProductController
    -> ProductService
    -> ProductRepository
    -> H2 database
    -> JSON response
```

## Run in IntelliJ

1. Open the `product-service` folder.
2. Allow IntelliJ to load the Maven project.
3. Open `ProductServiceApplication.java`.
4. Run its `main` method.
5. Open `http://localhost:8080/api/products`.

Java 21 is required. The project uses stable Spring Boot 4.1.1.

## API endpoints

| Operation | Method | URL |
| --- | --- | --- |
| List products | GET | `/api/products` |
| Find product | GET | `/api/products/{id}` |
| Filter by price | GET | `/api/products/filter?maxPrice=700` |
| Create product | POST | `/api/products` |
| Update product | PUT | `/api/products/{id}` |
| Delete product | DELETE | `/api/products/{id}` |

### Create a product

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Keyboard","price":89.99,"quantity":15}'
```

A successful create returns `201 Created`.

### Update a product

```bash
curl -i -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Gaming Laptop","price":1299.99,"quantity":8}'
```

### Delete a product

```bash
curl -i -X DELETE http://localhost:8080/api/products/1
```

A successful delete returns `204 No Content`.

## Validation examples

The API rejects blank names, prices below `0.01`, and negative quantities:

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"","price":0,"quantity":-1}'
```

## H2 database console

While the application is running, open `http://localhost:8080/h2-console` and use:

- JDBC URL: `jdbc:h2:mem:productdb`
- User name: `sa`
- Password: leave blank

The H2 database is in memory. It is recreated and seeded with three products whenever the application starts.

## Run tests

```bash
./mvnw test
```

## Next stages

1. PostgreSQL and database migrations
2. Order, inventory, payment, and notification microservices
3. Kafka events and idempotent consumers
4. Multithreading and `CompletableFuture`
5. Design patterns, security, Docker, and integration testing
