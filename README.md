# Sales Application

Sales application with Java Spring Boot As Backend API and Angular in FrontEnd.

## Overview

This is a full-stack web application for managing customers, products, and orders. The application consists of:

- **Backend**: RESTful API built with Java Spring Boot 3.2
- **Frontend**: Single Page Application built with Angular 17

## Features

- **Customer Management**: Create, read, update, and delete customers
- **Product Management**: Create, read, update, and delete products with stock control
- **Order Management**: Create orders with multiple items, track order status, and manage order lifecycle
- **Stock Control**: Automatic stock deduction when orders are created and restoration when cancelled
- **API Documentation**: Swagger UI for interactive API documentation

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- H2 Database (development)
- PostgreSQL (production ready)
- Lombok
- SpringDoc OpenAPI (Swagger)
- JUnit 5 & Mockito (testing)

### Frontend
- Angular 17
- TypeScript
- Bootstrap 5
- RxJS
- Jasmine & Karma (testing)

## Project Structure

```
Sales/
├── backend/                    # Spring Boot API
│   ├── src/main/java/com/sales/api/
│   │   ├── controller/         # REST Controllers
│   │   ├── service/            # Business Logic
│   │   ├── repository/         # Data Access
│   │   ├── entity/             # JPA Entities
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── exception/          # Exception Handling
│   │   └── config/             # Configuration
│   ├── src/main/resources/
│   │   └── application.yml
│   └── src/test/java/          # Tests
├── frontend/                   # Angular App
│   ├── src/app/
│   │   ├── components/         # UI Components
│   │   ├── services/           # HTTP Services
│   │   └── models/             # TypeScript Interfaces
│   └── src/environments/
└── README.md
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Node.js 18+ and npm
- Angular CLI (`npm install -g @angular/cli`)

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run the development server:
   ```bash
   ng serve
   ```

The application will be available at `http://localhost:4200`

## API Endpoints

### Customers
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/customers` | Get all customers |
| GET | `/api/customers/{id}` | Get customer by ID |
| POST | `/api/customers` | Create a new customer |
| PUT | `/api/customers/{id}` | Update a customer |
| DELETE | `/api/customers/{id}` | Delete a customer |

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/search?name=` | Search products by name |
| GET | `/api/products/in-stock` | Get products in stock |
| POST | `/api/products` | Create a new product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/customer/{customerId}` | Get orders by customer |
| GET | `/api/orders/status/{status}` | Get orders by status |
| POST | `/api/orders` | Create a new order |
| PUT | `/api/orders/{id}/status` | Update order status |
| DELETE | `/api/orders/{id}` | Delete an order (pending only) |

### Order Statuses
- `PENDING` - Order created, awaiting confirmation
- `CONFIRMED` - Order confirmed
- `PROCESSING` - Order being processed
- `SHIPPED` - Order shipped
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled

## API Documentation

When the backend is running, you can access:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

## Running Tests

### Backend Tests

```bash
cd backend
mvn test
```

This will run:
- Unit tests for services
- Integration tests for controllers

### Frontend Tests

```bash
cd frontend
ng test
```

For CI/CD (headless):
```bash
ng test --no-watch --no-progress --browsers=ChromeHeadless
```

## Database

The application uses H2 in-memory database by default for development. The database console is available at `http://localhost:8080/h2-console` with:
- JDBC URL: `jdbc:h2:mem:salesdb`
- Username: `sa`
- Password: (empty)

For production, configure PostgreSQL in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/salesdb
    username: your_username
    password: your_password
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

## License

This project is open source and available under the MIT License.
