🌐 **Язык | Language:** [🇷🇺](README.md) | 🇬🇧

## Backend API Documentation

The **backend module** is a Spring Boot 3 REST API that powers the Financial Control App.  
It exposes HTTP endpoints for authentication, user profiles, and per-user income/expense management over a PostgreSQL database with Liquibase migrations.

```text
+--------------------+       HTTP(S)        +----------------------+       JDBC      +-------------------------+
|  Next.js Frontend  |  <---------------->  |  Spring Boot Backend |  <-----------> |  PostgreSQL + Liquibase |
+--------------------+                      +----------------------+                 +-------------------------+
                                                     |
                                                     | Spring Security, Validation,
                                                     | Global Error Handling (JSON)
```

### 🧩 Backend Overview

- **Responsibilities**
  - Session-based authentication with optional remember-me
  - User profile + per-user incomes and expenses (CRUD)
  - Unified validation and JSON error responses

- **Environments & Migrations**
  - Runs in multiple environments (local, CI, prod) via Docker/Spring profiles
  - Schema changes managed by Liquibase migrations, applied automatically or via pipeline

This `docs` directory contains the complete HTTP API reference for this backend.

### 📚 Documentation

- **[API.en.md](docs/API.en.md)** – Comprehensive REST API documentation:
  - Authentication
  - Expense management (CRUD)
  - Income management (CRUD)
  - User profile
  - Error handling
  - Usage scenarios

### 🔗 Endpoints Overview

#### 🔐 Authentication (`/api/auth`)

| Method | Path               | Description                        |
|--------|--------------------|------------------------------------|
| POST   | `/api/auth/login`  | User login (session + remember-me) |
| POST   | `/api/auth/signup` | User registration + auto login     |
| POST   | `/api/auth/logout` | Logout and invalidate session      |

#### 💸 Expenses (`/api/expenses`)

| Method | Path                 | Description                         |
|--------|----------------------|-------------------------------------|
| POST   | `/api/expenses`      | Create expense                      |
| GET    | `/api/expenses`      | Get all expenses (current user)     |
| GET    | `/api/expenses/{id}` | Get expense by id                   |
| PUT    | `/api/expenses/{id}` | Update expense by id                |
| DELETE | `/api/expenses/{id}` | Delete expense by id                |

#### 💰 Incomes (`/api/incomes`)

| Method | Path                | Description                          |
|--------|---------------------|--------------------------------------|
| POST   | `/api/incomes`      | Create income                        |
| GET    | `/api/incomes`      | Get all incomes (current user)       |
| GET    | `/api/incomes/{id}` | Get income by id                     |
| PUT    | `/api/incomes/{id}` | Update income by id                  |
| DELETE | `/api/incomes/{id}` | Delete income by id                  |

#### 👤 User Profile (`/api/users`)

| Method | Path            | Description              |
|--------|-----------------|--------------------------|
| GET    | `/api/users/me` | Get current user profile |
| PATCH  | `/api/users/me` | Update current user      |

### 📖 Navigation & Quick Access

- **Project root**: [Main Project README](../README.en.md)
- **Full API reference**: [API.en.md](docs/API.en.md)
- **Swagger UI**: `http://localhost:8484/swagger-ui.html`
- **Scalar**: `http://localhost:8484/scalar`
