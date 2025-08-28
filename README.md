# Secure E-Commerce API (Spring Boot + JWT)

A Spring Boot REST API that secures an e-commerce backend with JWT authentication, BCrypt password hashing, and request filtering via Spring Security. Includes user signup, login, carts, items, and orders.

## Tech Stack

- Java 21, Spring Boot 3
- Spring Security, JWT (jjwt)
- Spring Data JPA (Hibernate), H2 in-memory DB
- JUnit/Mockito (tests)

---

## Features
- User signup with validation (username, password, confirm password)
- Passwords hashed with BCrypt (never returned to clients)
- Per-user random salt field stored (BCrypt also salts internally)
- Login returns a JWT in the Authorization header (Bearer <token>)
- All endpoints except signup are protected by JWT
- Clean security filters: authentication (login) + authorization (verify token)
  
## Quick Start
### 1) Run the app
   From project root
./mvnw spring-boot:run
App starts at: http://localhost:8080.

H2 in-memory DB is used (fresh on each run). Optional console: http://localhost:8080/h2-console (if enabled).

### 2) Create user (public)
   POST http://localhost:8080/api/user/create
   Body (JSON):
   {
  "username": "alice",
  "password": "secret123",
  "confirmPassword": "secret123"
}

Result: <img width="1065" height="661" alt="image" src="https://github.com/user-attachments/assets/a06dd319-d213-491b-9a9e-7cde168fb009" />


### 3) Login (get JWT)
   POST http://localhost:8080/login
   Body (JSON):
   {
  "username": "alice",
  "password": "secret123"
}

Response body includes:
{ "token": "Bearer eyJhbGciOi..." }

Result: <img width="1073" height="647" alt="image" src="https://github.com/user-attachments/assets/0b3962ad-77da-4b9d-850b-b61b6009d565" />

### 4) Access protected endpoint
    GET http://localhost:8080/api/user/alice
    Add Header:
        Authorization: Bearer <token>

Result: <img width="1067" height="650" alt="image" src="https://github.com/user-attachments/assets/9d871732-f552-437d-8de4-fb3ac017810e" />



