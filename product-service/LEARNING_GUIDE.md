# Product Service Learning Guide

Use this guide with the code open in IntelliJ. Learn one layer at a time instead of trying to memorize the whole project.

## 1. What changed from the first version?

The first version stored products in a `HashMap`:

```text
Controller -> Service -> HashMap
```

This version stores products in an H2 relational database:

```text
Controller -> Service -> Repository -> H2 database
```

The REST URLs remain almost the same. The storage implementation changed.

## 2. Read the files in this order

### `ProductServiceApplication.java`

This is the entry point. `@SpringBootApplication` starts Spring, component scanning, auto-configuration, and the embedded web server.

### `Product.java`

This is a JPA entity. `@Entity` tells JPA that Product objects can be stored as database rows. `@Id` marks the primary key, and `@GeneratedValue` lets the database generate IDs.

JPA entities use a class rather than the earlier record because JPA needs a no-argument constructor and managed mutable fields.

### `ProductRequest.java`

This record represents JSON sent by a client. Its annotations reject blank names, invalid prices, and negative quantities.

### `ProductResponse.java`

This record represents JSON returned by the API. Keeping request, entity, and response types separate prevents database details from leaking into the API contract.

### `ProductRepository.java`

This interface extends `JpaRepository<Product, Long>`. Spring creates its implementation automatically. It provides methods including `findAll`, `findById`, `save`, and `delete`.

### `ProductService.java`

This class contains business logic. Spring injects `ProductRepository` through its constructor.

- `@Transactional(readOnly = true)` is used for database reads.
- `@Transactional` is used for create, update, and delete operations.
- Streams sort, filter, and convert entities into response records.

### `ProductController.java`

This class maps HTTP requests to service methods:

- `@GetMapping` reads data.
- `@PostMapping` creates data.
- `@PutMapping` updates data.
- `@DeleteMapping` deletes data.
- `@Valid` runs the validation rules from `ProductRequest`.

### `GlobalExceptionHandler.java`

This component converts Java exceptions into consistent JSON error responses. A missing product becomes HTTP `404`; invalid input becomes HTTP `400`.

### `DataInitializer.java`

This configuration creates a `CommandLineRunner` bean. It inserts three sample products after Spring starts and the database is ready.

## 3. What is H2?

H2 is a small relational database that runs inside this application. It is useful for learning and tests because no separate database installation is required.

This project intentionally recreates the H2 database after every restart. PostgreSQL will be introduced after the JPA flow is understood.

## 4. Create request flow

```text
POST /api/products
    -> ProductController validates ProductRequest
    -> ProductService creates a Product entity
    -> ProductRepository saves the entity
    -> @Transactional commits the database change
    -> ProductResponse is returned with HTTP 201
```

## 5. Missing-product flow

```text
GET /api/products/100
    -> ProductRepository cannot find ID 100
    -> ProductService throws ProductNotFoundException
    -> GlobalExceptionHandler catches it
    -> API returns clean JSON with HTTP 404
```

## 6. Beginner definitions

| Term | Meaning in this project |
| --- | --- |
| Entity | Java object mapped to a database table |
| Repository | Database access layer |
| Service | Business logic layer |
| Controller | HTTP/API layer |
| DTO | Request or response data object |
| Transaction | Group of database work that succeeds or rolls back as one unit |
| Validation | Rules that reject incorrect client input |
| Dependency injection | Spring supplies the repository to the service and the service to the controller |

## 7. First practice sequence

1. Run the application.
2. Call `GET /api/products`.
3. Open the H2 console and view the `PRODUCTS` table.
4. Create a product with POST.
5. Refresh the H2 table and observe the new row.
6. Send invalid product JSON and observe HTTP 400.
7. Request product 100 and observe HTTP 404.
8. Run `ProductServiceTest` and study the three test cases.

## 8. Interview explanation

> I built a Product microservice using Java 21 and Spring Boot. The controller exposes RESTful CRUD endpoints, the service contains business logic and transaction boundaries, and a Spring Data JPA repository handles persistence in an H2 database. I used request and response records as DTOs, Bean Validation for input checks, Streams for filtering and sorting, centralized exception handling for consistent API errors, and JUnit with Mockito for service-level tests.
