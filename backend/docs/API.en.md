🌐 **Язык | Language:** [🇷🇺](API.md) | 🇬🇧

# Backend API Documentation

![API Version](https://img.shields.io/badge/API-v1.0-blue)

REST API for Financial Control App
Version: 1.0
Updated: 03.02.2026


---

# 📋 General Information

## Authentication

The API uses **session-based authentication** via HTTP cookies:

- After successful login/registration, the server sets a `JSESSIONID` cookie
- All subsequent requests must include this cookie
- When using `rememberMe=true`, an additional cookie is set for 7 days
- Cookies are automatically sent by browsers; other clients need to pass them manually

## Data Format

- **Content-Type:** `application/json`
- **Date:** ISO 8601 with timezone (`2025-01-28T12:00:00+03:00`)
- **Amount:** `BigDecimal` (number, can have up to 2 decimal places, e.g.: `1500.65`)

## Swagger UI

Interactive API documentation is available at:

```html
/swagger-ui.html
/scalar
```

---

# 🔐 Authentication (Session based)

All authentication endpoints are under the `/api/auth` prefix.

## 🔑 Login

Authenticate an existing user in the system.

**Method:** `POST`

**URL:** `/api/auth/login`

**Content-Type:** `application/json`

**Authentication required:** ❌ No

### Request Parameters

| Field        | Type      | Required | Description                                                                   |
|--------------|-----------|----------|-------------------------------------------------------------------------------|
| `username`   | `string`  | ✅ Yes    | Username                                                                      |
| `password`   | `string`  | ✅ Yes    | Password                                                                      |
| `rememberMe` | `boolean` | ❌ No     | Remember user for 7 days (default: `false`)                                   |
| `email`      | `string`  | ❌ No     | Email (optional, valid format if provided) (currently not processed)         |

### Request Example

```json
{
  "username": "alex",
  "password": "1234",
  "rememberMe": true
}
```

### Response Example (200 OK)

**Headers:**

```html
Set-Cookie: JSESSIONID=E95A8213A8D714C9A07CF615C62379E2; Path=/; HttpOnly;
Set-Cookie: remember-me=YWxleDoxNzUwNjcxMTk2MzM4OlNIQTI1NjplNjMzZDVhNDc2ZmUwNTE1YWRhMzNjYzk3NTZlN2JjZDI3NTE1YWFkNzI0ZTBkMGIwZDVhYTcyNWJlNWRhZWM1; Path=/; HttpOnly; Expires=Mon, 23 Jun 2025 09:33:16 GMT;
```

**Body:** Empty (or `{}`)

### Possible Errors

| Status             | Description           | Response Example (JSON)                                                                              |
|--------------------|-----------------------|------------------------------------------------------------------------------------------------------|
| `400 Bad Request`  | Validation error      | `{"msg":"Validation failed","errors":{"username":"must not be null","password":"must not be null"}}` |
| `401 Unauthorized` | Authentication error  | `{"msg":"Invalid credentials","cause":"Bad credentials"}`                                            |

> ⚠️ All subsequent API requests must include the session cookie JSESSIONID set by the server (remember-me is optional). Cookies can be cleared via logout.

## 🔓 Logout

Log out of the system and delete the session cookie.

**Method:** `POST`

**URL:** `/api/auth/logout`

**Authentication required:** ✅ Yes

### Response Example (200 OK)

**Headers:**

```
Set-Cookie: JSESSIONID=; Secure; HttpOnly; Path=/; Max-Age=0
Set-Cookie: remember-me=; Secure; HttpOnly; Path=/; Max-Age=0
```

**Body:** Empty

### Possible Errors

| Status             | Description                         | Response Example (JSON)                                                                              |
|--------------------|-------------------------------------|------------------------------------------------------------------------------------------------------|
| `401 Unauthorized` | No active session / not authorized  | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |

> 💡 After logging out, all requests using the previous cookie will receive 401 Unauthorized.

## 👤 Registration

Create a new user in the system. After successful registration, the user is automatically logged in.

**Method:** `POST`

**URL:** `/api/auth/signup`

**Content-Type:** `application/json`

**Authentication required:** ❌ No

### Request Parameters

| Field             | Type     | Required | Description                                          |
|-------------------|----------|----------|------------------------------------------------------|
| `username`        | `string` | ✅ Yes    | Username (unique)                                    |
| `password`        | `string` | ✅ Yes    | Password                                             |
| `confirmPassword` | `string` | ✅ Yes    | Password confirmation (must match `password`)        |
| `email`           | `string` | ❌ No     | Email address (valid email format)                   |

### Request Example

```json
{
  "username": "alex",
  "password": "1234",
  "confirmPassword": "1234",
  "email": "example@gmail.com"
}
```

### Response Example (201 Created)

**Body:** Empty (or `{}`)

**Headers:**

```
Set-Cookie: JSESSIONID=...; Path=/; HttpOnly;
```

### Possible Errors

| Status            | Description            | Response Example (JSON)                                                                                                            |
|-------------------|------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `400 Bad Request` | Validation errors      | `{"msg":"Validation failed","errors":{"username":"must not be null","password":"must not be null","confirmPassword":"must not be null"}}` |
| `400 Bad Request` | Passwords don't match  | `{"msg":"Wrong data","cause":"Passwords don't match"}`                                                                            |
| `409 Conflict`    | User already exists    | `{"msg":"User already exists","cause":"User with username 'alex' already exists"}`                                                |

---

# 💸 CRUD: Expenses

All expense endpoints are under the `/api/expenses` prefix.

> ⚠️ Authentication required: All expense endpoints require a valid session cookie.

## ➕ Create Expense

Create a new expense record for the current authenticated user.

**Method:** `POST`

**URL:** `/api/expenses`

**Content-Type:** `application/json`

**Authentication required:** ✅ Yes

### Request Parameters

| Field         | Type                  | Required | Description                                              |
|---------------|-----------------------|----------|----------------------------------------------------------|
| `amount`      | `number` (BigDecimal) | ✅ Yes    | Expense amount (e.g.: `1500.23` = 1500.23₽)              |
| `category`    | `string`              | ✅ Yes    | Expense category (max 128 characters)                    |
| `description` | `string`              | ❌ No     | Expense description                                      |
| `createDate`  | `string` (ISO 8601)   | ✅ Yes    | Creation date                                            |

### Request Example

```json
{
  "amount": 1500.25,
  "category": "Food",
  "createDate": "2025-05-28T12:00:00Z",
  "description": "Lunch at cafe"
}
```

### Response Example (201 Created)

```json
{
    "id": 1,
    "amount": 1500.25,
    "category": "Food",
    "createDate": "2025-01-28T12:00:00+00:00",
    "description": "Lunch at cafe"
}
```

### Possible Errors

| Status             | Description            | Response Example (JSON)                                                                              |
|--------------------|------------------------|------------------------------------------------------------------------------------------------------|
| `400 Bad Request`  | Validation error       | `{"msg":"Validation failed","errors":{"amount":"must not be null"}}`                                 |
| `401 Unauthorized` | User not authorized    | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |

## 📥 Get All Expenses

Get a list of all expenses for the current authenticated user.

**Method:** `GET`

**URL:** `/api/expenses`

**Authentication required:** ✅ Yes

### Response Example (200 OK)

```json
[
    {
        "id": 1,
        "amount": 150000.00,
        "category": "Food",
        "createDate": "2025-01-28T12:00:00+00:00",
        "description": "Lunch at cafe"
    },
    {
        "id": 2,
        "amount": 50000.00,
        "category": "Transport",
        "createDate": "2025-01-28T13:30:00+00:00",
        "description": "Taxi"
    }
]
```

### Possible Errors

| Status             | Description         | Response Example (JSON)                                                                              |
|--------------------|---------------------|------------------------------------------------------------------------------------------------------|
| `401 Unauthorized` | User not authorized | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |

> 💡 Note: Only expenses of the current user are returned. Other users' expenses are not accessible.

## 📄 Get Expense by ID

Get information about a specific expense by its identifier.

**Method:** `GET`

**URL:** `/api/expenses/{id}`

### Path Parameters

| Parameter | Type            | Required | Description   |
|-----------|-----------------|----------|---------------|
| `id`      | `number` (Long) | ✅ Yes    | Expense ID    |

### Response Example (200 OK)

```json
{
    "id": 1,
    "amount": 150000.00,
    "category": "Food",
    "createDate": "2025-01-28T12:00:00+00:00",
    "description": "Lunch at cafe"
}
```

### Possible Errors

| Status             | Description                                       | Response Example (JSON)                                                                              |
|--------------------|---------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `401 Unauthorized` | User not authorized                               | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |
| `403 Forbidden`    | Access denied (expense belongs to another user)   | `{"msg":"Access denied","cause":"Access to this record is not allowed for current user"}`           |
| `404 Not Found`    | Expense not found                                 | `{"msg":"Element not found","cause":"ExpenseEntity with id: 999 is not found!"}`                     |

> 💡 Note: Only expenses of the current user are returned. Other users' expenses are not accessible.

## ♻️ Update Expense

Update an existing expense. You can only update your own expenses.

**Method:** `PUT`

**URL:** `/api/expenses/{id}`

**Authentication required:** ✅ Yes

### Path Parameters

| Parameter | Type            | Required | Description   |
|-----------|-----------------|----------|---------------|
| `id`      | `number` (Long) | ✅ Yes    | Expense ID    |

### Request Parameters

| Field         | Type                  | Required | Description         |
|---------------|-----------------------|----------|---------------------|
| `amount`      | `number` (BigDecimal) | ❌ No     | Expense amount      |
| `category`    | `string`              | ❌ No     | Expense category    |
| `description` | `string`              | ❌ No     | Expense description |
| `createDate`  | `string` (ISO 8601)   | ❌ No     | Expense date        |

### Request Example

```json
{
    "id": 1,
    "amount": 200000,
    "category": "Food",
    "createDate": "2025-01-28T12:00:00+00:00",
    "description": "Dinner at restaurant"
}
```

### Response Example (200 OK)

```json
{
    "id": 1,
    "amount": 200000.00,
    "category": "Food",
    "createDate": "2025-01-28T12:00:00+00:00",
    "description": "Dinner at restaurant"
}
```

### Possible Errors

| Status             | Description                                       | Response Example (JSON)                                                                              |
|--------------------|---------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `400 Bad Request`  | Validation error                                  | `{"msg":"Validation failed","errors":{"amount":"must not be null"}}`                                 |
| `401 Unauthorized` | User not authorized                               | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |
| `403 Forbidden`    | Access denied (expense belongs to another user)   | `{"msg":"Access denied","cause":"Access to this record is not allowed for current user"}`           |
| `404 Not Found`    | Expense not found                                 | `{"msg":"Element not found","cause":"ExpenseEntity with id: 999 is not found!"}`                     |

> 💡 Note: Only expenses of the current user can be modified. Other users' expenses are not accessible.

## ❌ Delete Expense

Delete an expense by its identifier.

**Method:** `DELETE`

**URL:** `/api/expenses/{id}`

**Authentication required:** ✅ Yes

### Path Parameters

| Parameter | Type            | Required | Description   |
|-----------|-----------------|----------|---------------|
| `id`      | `number` (Long) | ✅ Yes    | Expense ID    |

### Response Example (204 No Content)

**Body:** Empty

### Possible Errors

| Status             | Description                                       | Response Example (JSON)                                                                              |
|--------------------|---------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `401 Unauthorized` | User not authorized                               | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |
| `403 Forbidden`    | Access denied (expense belongs to another user)   | `{"msg":"Access denied","cause":"Access to this record is not allowed for current user"}`           |
| `404 Not Found`    | Expense not found                                 | `{"msg":"Element not found","cause":"ExpenseEntity with id: 999 is not found!"}`                     |

> 💡 Note: Only expenses of the current user can be deleted. Other users' expenses are not accessible.

---

# 💰 CRUD: Incomes

All income endpoints are under the `/api/incomes` prefix.

> ⚠️ Authentication required: All income endpoints require a valid session cookie.

## ➕ Create Income

Create a new income record for the current authenticated user.

**Method:** `POST`

**URL:** `/api/incomes`

**Content-Type:** `application/json`

**Authentication required:** ✅ Yes

### Request Parameters

| Field         | Type                  | Required | Description                                            |
|---------------|-----------------------|----------|--------------------------------------------------------|
| `amount`      | `number` (BigDecimal) | ✅ Yes    | Income amount (e.g.: `354443.65` = 354443.65₽)         |
| `category`    | `string`              | ✅ Yes    | Income category (max 128 characters)                   |
| `description` | `string`              | ❌ No     | Income description                                     |
| `createDate`  | `string` (ISO 8601)   | ✅ Yes    | Creation date                                          |

### Request Example

```json
{
  "amount": 3000.12,
  "category": "Salary",
  "createDate": "2025-05-28T12:00:00Z",
  "description": "Monthly salary"
}
```

### Response Example (201 Created)

```json
{
    "id": 1,
    "amount": 3000.12,
    "category": "Salary",
    "createDate": "2025-01-28T12:00:00+00:00",
    "description": "Monthly salary"
}
```

### Possible Errors

| Status             | Description            | Response Example (JSON)                                                                              |
|--------------------|------------------------|------------------------------------------------------------------------------------------------------|
| `400 Bad Request`  | Validation error       | `{"msg":"Validation failed","errors":{"amount":"must not be null"}}`                                 |
| `401 Unauthorized` | User not authorized    | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |

## 📥 Get All Incomes

Get a list of all incomes for the current authenticated user.

**Method:** `GET`

**URL:** `/api/incomes`

**Authentication required:** ✅ Yes

### Response Example (200 OK)

```json
[
    {
        "id": 1,
        "amount": 3000.12,
        "category": "Salary",
        "createDate": "2025-01-28T12:00:00+00:00",
        "description": "Monthly salary"
    },
    {
        "id": 2,
        "amount": 5000.65,
        "category": "Freelance",
        "createDate": "2025-01-28T14:00:00+00:00",
        "description": "Project payment"
    }
]
```

### Possible Errors

| Status             | Description         | Response Example (JSON)                                                                              |
|--------------------|---------------------|------------------------------------------------------------------------------------------------------|
| `401 Unauthorized` | User not authorized | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |

> 💡 Note: Only incomes of the current user are returned. Other users' incomes are not accessible.

## 📄 Get Income by ID

Get information about a specific income by its identifier.

**Method:** `GET`

**URL:** `/api/incomes/{id}`

**Authentication required:** ✅ Yes

### Path Parameters

| Parameter | Type            | Required | Description |
|-----------|-----------------|----------|-------------|
| `id`      | `number` (Long) | ✅ Yes    | Income ID   |

### Response Example (200 OK)

```json
{
    "id": 1,
    "amount": 3000.00,
    "category": "Salary",
    "createDate": "2025-01-28T12:00:00+00:00",
    "description": "Monthly salary"
}
```

### Possible Errors

| Status             | Description                                     | Response Example (JSON)                                                                              |
|--------------------|-------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `401 Unauthorized` | User not authorized                             | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |
| `403 Forbidden`    | Access denied (income belongs to another user)  | `{"msg":"Access denied","cause":"Access to this record is not allowed for current user"}`           |
| `404 Not Found`    | Income not found                                | `{"msg":"Element not found","cause":"IncomeEntity with id: 999 is not found!"}`                      |

> 💡 Note: Only incomes of the current user are returned. Other users' incomes are not accessible.

## ♻️ Update Income by ID

Update an existing income. You can only update your own incomes.

**Method:** `PUT`

**URL:** `/api/incomes/{id}`

**Authentication required:** ✅ Yes

### Path Parameters

| Parameter | Type            | Required | Description |
|-----------|-----------------|----------|-------------|
| `id`      | `number` (Long) | ✅ Yes    | Income ID   |

### Request Parameters

| Field         | Type                  | Required | Description        |
|---------------|-----------------------|----------|--------------------|
| `amount`      | `number` (BigDecimal) | ❌ No     | Income amount      |
| `category`    | `string`              | ❌ No     | Income category    |
| `description` | `string`              | ❌ No     | Income description |
| `createDate`  | `string` (ISO 8601)   | ❌ No     | Income date        |

### Request Example

```json
{
    "id": 1,
    "amount": 3500000,
    "category": "Salary",
    "createDate": "2025-01-28T12:00:00+00:00",
    "description": "Monthly salary with bonus"
}
```

### Response Example (200 OK)

```json
{
    "id": 1,
    "amount": 3500000.00,
    "category": "Salary",
    "createDate": "2025-01-28T12:00:00+00:00",
    "description": "Monthly salary with bonus"
}
```

### Possible Errors

| Status             | Description                                     | Response Example (JSON)                                                                              |
|--------------------|-------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `400 Bad Request`  | Validation error                                | `{"msg":"Validation failed","errors":{"amount":"must not be null"}}`                                 |
| `401 Unauthorized` | User not authorized                             | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |
| `403 Forbidden`    | Access denied (income belongs to another user)  | `{"msg":"Access denied","cause":"Access to this record is not allowed for current user"}`           |
| `404 Not Found`    | Income not found                                | `{"msg":"Element not found","cause":"IncomeEntity with id: 999 is not found!"}`                      |

> 💡 Note: Only incomes of the current user can be modified. Other users' incomes are not accessible.

## ❌ Delete Income by ID

Delete an income by its identifier.

**Method:** `DELETE`

**URL:** `/api/incomes/{id}`

### Path Parameters

| Parameter | Type            | Required | Description |
|-----------|-----------------|----------|-------------|
| `id`      | `number` (Long) | ✅ Yes    | Income ID   |

### Response Example (204 No Content)

**Body:** Empty

### Possible Errors

| Status             | Description                                     | Response Example (JSON)                                                                              |
|--------------------|-------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `401 Unauthorized` | User not authorized                             | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |
| `403 Forbidden`    | Access denied (income belongs to another user)  | `{"msg":"Access denied","cause":"Access to this record is not allowed for current user"}`           |
| `404 Not Found`    | Income not found                                | `{"msg":"Element not found","cause":"IncomeEntity with id: 999 is not found!"}`                      |

> 💡 Note: Only incomes of the current user can be deleted. Other users' incomes are not accessible.

---

# 👤 Profile Management

All user information endpoints are under the `/api/users` prefix.

> ⚠️ Authentication required: All profile endpoints require a valid session cookie.

## 🖋️ Get User Information

Get detailed information about the current authenticated user.

**Method:** `GET`

**URL:** `/api/users/me`

**Content-Type:** `application/json`

**Authentication required:** ✅ Yes

### Response Example (200 OK)

```json
{
  "id": 456,
  "username": "johndoe",
  "displayName": "John Doe",
  "email": "john.doe@example.com"
}
```

### Possible Errors

| Status                      | Description                                                     | Response Example (JSON)                                                                              |
|-----------------------------|-----------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `401 Unauthorized`          | User not authorized                                             | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |
| `403 Forbidden`             | Access denied (attempting to request user outside current session) | `{"msg":"Access denied","cause":"Access to this record is not allowed for current user"}`         |
| `405 Method Not Allowed`    | No implementation for requested method                          | `{"msg":"There is no such http method","cause":"Request method 'POST' is not supported"}`            |
| `500 Internal Server Error` | Internal server error                                           | `{"msg":"Something went wrong","cause":"Detailed error cause"}`                                      |

## 🐥 Create User

Not implemented.

## 📄 Update User Information

Update user information.

**Method:** `PATCH`

**URL:** `/api/users/me`

**Authentication required:** ✅ Yes

### Request Parameters

| Field             | Type     | Required | Description                                                     |
|-------------------|----------|----------|-----------------------------------------------------------------|
| `password`        | `string` | ❌ No     | New password                                                    |
| `confirmPassword` | `string` | ❌ No     | Password confirmation (validated only when sent with `password`) |
| `displayName`     | `string` | ❌ No     | Display name                                                    |
| `email`           | `string` | ❌ No     | Email                                                           |

### Request Example

```json
{
  "password": "newpassword123",
  "confirmPassword": "newpassword123",
  "displayName": "Updated Name",
  "email": "newemail@example.com"
}
```

### Response Example (200 OK)

```json
{
  "id": 456,
  "username": "johndoe",
  "displayName": "Updated Name",
  "email": "newemail@example.com"
}
```

### Possible Errors

| Status                      | Description                                                      | Response Example (JSON)                                                                              |
|-----------------------------|------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `400 Bad Request`           | Validation errors                                                | `{"msg":"Validation failed","errors":{"password":"must not be null"}}`                               |
| `401 Unauthorized`          | User not authorized                                              | `{"msg":"Insufficient authentication","cause":"Full authentication is required to access this resource"}` |
| `403 Forbidden`             | Access denied (attempting to update user outside current session) | `{"msg":"Access denied","cause":"Access to this record is not allowed for current user"}`          |
| `405 Method Not Allowed`    | No implementation for requested method                           | `{"msg":"There is no such http method","cause":"Request method 'POST' is not supported"}`            |
| `500 Internal Server Error` | Internal server error                                            | `{"msg":"Something went wrong","cause":"Detailed error cause"}`                                      |

## ❌ Delete User

Not implemented.

---

# ⚠️ Error Handling

## Common Error Response Formats

### ValidationExceptionJson (400 Bad Request)

Used for input validation errors.

**Structure:**

```json
{
    "msg": "Validation failed",
    "errors": {
        "fieldName": "error message",
        "username": "must not be null",
        "email": "must be a well-formed email address"
    }
}
```

### CommonExceptionJson (401, 403, 404, 405, 409, 500)

Used for other error types.

**Structure:**

```json
{
    "msg": "Error message",
    "cause": "Detailed error cause"
}
```

## HTTP Status Codes

| Code  | Name                  | Description          | When it occurs                                                 |
|-------|-----------------------|----------------------|----------------------------------------------------------------|
| `200` | OK                    | Successful request   | Operation completed successfully                               |
| `400` | Bad Request           | Invalid request      | Validation errors, incorrect login/password, password mismatch |
| `401` | Unauthorized          | Not authorized       | Missing or invalid session                                     |
| `403` | Forbidden             | Access denied        | Attempting to update another user's record                     |
| `404` | Not Found             | Not found            | Requested resource does not exist                              |
| `405` | Method Not Allowed    | Method not available | No implementation for this operation                           |
| `409` | Conflict              | Data conflict        | User already exists, data integrity violation                  |
| `500` | Internal Server Error | Server error         | Unexpected error, unhandled exceptions                         |

---

# 🎯 Usage Scenarios

## Scenario 1: First Application Launch

**Goal:** Register a new user and create the first transaction

**Steps:**

1. **User Registration**

    ```
    POST /api/auth/signup
    Content-Type: application/json

    {
      "username": "ivan",
      "password": "secure123",
      "confirmPassword": "secure123",
      "email": "ivan@example.com"
    }
    ```

   → Receive `JSESSIONID` cookie in response headers

2. **Create First Income (salary)**

    ```
    POST /api/incomes
    Cookie: JSESSIONID=...
    Content-Type: application/json

    {
      "amount": 100000.00,
      "category": "Salary",
      "description": "January salary"
    }
    ```

3. **Add First Expense**

    ```
    POST /api/expenses
    Cookie: JSESSIONID=...
    Content-Type: application/json

    {
      "amount": 5000.00,
      "category": "Food",
      "description": "Weekly groceries"
    }
    ```

4. **Get Balance**

    ```
    GET /api/incomes
    Cookie: JSESSIONID=...

    GET /api/expenses
    Cookie: JSESSIONID=...
    ```

   → Calculate balance on client: sum of incomes - sum of expenses


## Scenario 2: Daily Usage

**Goal:** Quickly add several expenses during the day

**Steps:**

1. **Login (if cookie expired)**

    ```
    POST /api/auth/login
    Content-Type: application/json

    {
      "username": "ivan",
      "password": "secure123",
      "rememberMe": true
    }
    ```

   → If `rememberMe=true`, cookie is valid for 7 days

2. **Add Expenses Throughout the Day**

    ```
    POST /api/expenses
    Cookie: JSESSIONID=...
    Content-Type: application/json

    {
      "amount": 500.00,
      "category": "Transport",
      "description": "Taxi to office"
    }

    POST /api/expenses
    Cookie: JSESSIONID=...
    Content-Type: application/json

    {
      "amount": 3000.00,
      "category": "Food",
      "description": "Lunch at restaurant"
    }
    ```

3. **View Daily Statistics**

    ```
    GET /api/expenses
    Cookie: JSESSIONID=...
    ```

   → Filter by date on client and group by categories


## Scenario 3: Fixing an Error

**Goal:** Update expense amount if a mistake was made

**Steps:**

1. **Get Expense by ID**

    ```
    GET /api/expenses/5
    Cookie: JSESSIONID=...
    ```

   → Receive: `{ "id": 5, "amount": 1000.00, "category": "Food", ... }`

2. **Update Amount**

    ```
    PUT /api/expenses/5
    Cookie: JSESSIONID=...
    Content-Type: application/json

    {
      "amount": 1500.00,
      "category": "Food",
      "description": "Lunch"
    }
    ```

   → Fix amount from 1000₽ to 1500₽


## Scenario 4: Expense Analysis by Category

**Goal:** Get all expenses and group by categories for analysis

**Steps:**

1. **Get All Expenses**

    ```
    GET /api/expenses
    Cookie: JSESSIONID=...
    ```

2. **Response:**

    ```json
    [
        {
            "id": 1,
            "amount": 1500.00,
            "category": "Food"
        },
        {
            "id": 2,
            "amount": 500.00,
            "category": "Transport"
        },
        {
            "id": 3,
            "amount": 2000.00,
            "category": "Food"
        }
    ]
    ```

3. **Client-side Grouping:**
    - Food: 3500₽
    - Transport: 500₽

## Scenario 5: Deleting an Erroneous Record

**Goal:** Delete an accidentally created transaction

**Steps:**

1. **Delete Expense**

    ```
    DELETE /api/expenses/10
    Cookie: JSESSIONID=...
    ```

   → `200 OK`

2. **Verify Deletion**

    ```
    GET /api/expenses/10
    Cookie: JSESSIONID=...
    ```

   → `404 Not Found`
