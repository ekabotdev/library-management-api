# Library Management API

A RESTful Library Management API built with Java 21 and Spring Boot. The application provides functionality for managing authors, books, physical book copies, library members, and book loans, with JWT-based authentication and role-based authorization.

## Overview

The Library Management API is designed to support the core operations of a library system.

It separates books from their physical copies, allowing a single book record to have multiple copies that can be managed and loaned independently.
The system enforces borrowing rules by checking member membership status, book-copy availability, and physical condition before creating a loan

The application uses MySQL for persistence, Spring Data JPA for database access, and Flyway for database schema versioning.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 3.5.4 | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Database access and ORM |
| MySQL | Relational database |
| Spring Security | Authentication and authorization |
| JSON Web Tokens (JWT) | Stateless authentication |
| BCrypt | Password hashing |
| Flyway | Database migrations |
| Jakarta Bean Validation | Request validation |
| Springdoc OpenAPI | API documentation |
| Maven | Build and dependency management |
| JUnit 5, Mockito, AssertJ | Testing |

## Features

### Authentication and Authorization
- User registration and login.
- JWT-based authentication.
- BCrypt password hashing.
- Role-based access control with MEMBER and ADMIN roles.
- Protected API endpoints.

### Author Management
- Create, retrieve, update, and delete authors.
- Paginated author listing.
- Prevent deletion of authors who still have associated books.

### Book Management
- Create, retrieve, update, and delete books.
- Associate books with authors.
- Search books by title, ISBN, or author name.
- Paginated book listing.
- Prevent deletion of books that still have associated copies.

### Book Copy Management
- Manage individual physical copies of books.
- Track copy availability and borrowing status.
- Track physical copy condition.
- Update copy condition.
- Support maintenance status for copies that cannot currently be borrowed.

### Member Management
- Retrieve member records.
- View individual member details.
- Update membership status.
- Restrict member-management operations to administrators.

### Loan Management
- Borrow available book copies.
- Return borrowed copies.
- View personal loan history.
- Retrieve all loans as an administrator.
- Filter loans by status.
- Enforce borrowing rules based on membership status and book-copy condition.

## Security

The application uses Spring Security with stateless JWT authentication.

- Public endpoints: registration and login.
- Authenticated users: access protected library operations according to their permissions.
- Administrators: manage authors, books, book copies, and members.
- Passwords are stored as BCrypt hashes rather than plain text.

## Project Structure

```text
src/
├── main/
│   ├── java/com/ekabotdev/library/
│   │   ├── auth/
│   │   ├── author/
│   │   ├── book/
│   │   ├── bookcopy/
│   │   ├── config/
│   │   ├── common/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── loan/
│   │   ├── member/
│   │   ├── security/
│   │   └── user/
│   └── resources/
│       ├── db/migration/
│       └── application.yml
└── test/
    └── java/com/ekabotdev/library/
        ├── integration/
        └── unit/
```

## Prerequisites

Before running the application, make sure you have:

- Java 21.
- MySQL.
- Git.
- A terminal or IDE such as IntelliJ IDEA.

## Installation and Setup

### 1. Clone the repository

```bash
git clone https://github.com/ekabotdev/library-management-api.git
cd library-management-api
```

### 2. Create a MySQL database

Create a database in MySQL for the application.

```sql
CREATE DATABASE library_db;
```

### 3. Configure environment variables

The application reads its database and JWT configuration from environment variables.

Configure the following variables:

| Variable | Description |
|---|---|
| `DB_URL` | MySQL JDBC connection URL |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Secret used for JWT signing |
| `JWT_EXPIRATION` | JWT expiration configuration |

For example, the database URL can follow this format:

```text
jdbc:mysql://localhost:3306/library_db
```

Use your own database credentials and a securely generated JWT secret. Never commit real credentials or secrets to the repository.

### 4. Configure the development administrator

The development profile includes an administrator seeder.

To use it, configure:

| Variable | Description |
|---|---|
| `ADMIN_EMAIL` | Development administrator email |
| `ADMIN_INITIAL_PASSWORD` | Initial administrator password |

Activate the `dev` Spring profile when running the application if you want this development administrator to be created.

### 5. Run the application

On Windows:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

On Linux or macOS:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Flyway applies the available database migrations during application startup.

## API Documentation

The project uses Springdoc OpenAPI to generate interactive API documentation.

Once the application is running, visit:

```text
http://localhost:8080/swagger-ui/index.html
```

The API documentation includes the available endpoints and supports JWT bearer-token authentication.

## Main API Endpoints

| Resource | Method | Endpoint | Access |
|---|---|---|---|
| Register | POST | `/api/auth/register` | Public |
| Login | POST | `/api/auth/login` | Public |
| Authors | GET | `/api/authors` | Authenticated |
| Authors | POST | `/api/authors` | Admin |
| Books | GET | `/api/books` | Authenticated |
| Books | POST | `/api/books` | Admin |
| Book copies | GET | `/api/book_copies` | Authenticated |
| Book copies | POST | `/api/book_copies` | Admin |
| Members | GET | `/api/members` | Admin |
| Member status | PATCH | `/api/members/{id}/status` | Admin |
| Create loan | POST | `/api/loans` | Authenticated |
| Return loan | POST | `/api/loans/{id}/return` | Authenticated |
| My loans | GET | `/api/loans/my` | Authenticated |
| All loans | GET | `/api/loans` | Admin |
| Author by ID |	GET	| `/api/authors/{id}` |	Authenticated |
| Update author |	PUT |	`/api/authors/{id}` |	Admin |
| Delete author | 	DELETE |	`/api/authors/{id}` |	Admin |
| Book by ID |	GET | 	`/api/books/{id}` |	Authenticated |
| Update book |	PUT | 	`/api/books/{id}`	 | Admin |
| Delete book | 	DELETE | 	`/api/books/{id}` |	Admin |
| Book copy by ID | 	GET |	`/api/book_copies/{id}` |	Authenticated |
| Update copy condition |	PATCH |	`/api/book_copies/{id}/condition` |	Admin |
| Delete book copy |	DELETE |	`/api/book_copies/{id}` |	Admin |
| Member by ID | 	GET | 	`/api/members/{id}` |	Admin |


Other endpoints support retrieving, updating, and deleting individual resources. Refer to Swagger UI for the complete API specification.

## Testing

The project includes:

- Unit tests for author services.
- Unit tests for book services.
- Unit tests for book-copy services.
- Unit tests for loan services.
- Integration tests for authentication workflows.

To use it, configure:

| Variable | Description |
|---|---|
| `TEST_DB_URL` | Test database JDBC URL |
| `TEST_DB_USERNAME` | Test database username |
| `TEST_DB_PASSWORD` | Test database password |
| `TEST_JWT_SECRET` | Test JWT signing secret |
| `TEST_JWT_EXPIRATION` | Test JWT expiration configuration |

The test database should be configured separately from the development database.
Integration tests use a separate test database and test-specific JWT configuration. Do not point the test database configuration at your development or production database.

Run the test suite using:

```bash
mvnw.cmd test
```

On Linux or macOS:

```bash
./mvnw test
```

Configure the required test database and test JWT environment variables before running integration tests.

## Database Migrations

Flyway manages database schema changes through versioned SQL migration files located in:

```text
src/main/resources/db/migration/
```

Hibernate is configured with `ddl-auto: validate`, allowing the application to validate the database schema without automatically generating or modifying it.

## Author

**EkabotDev**

GitHub: https://github.com/ekabotdev

