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
1) Run the app
    # from project root
./mvnw spring-boot:run
App starts on http://localhost:8080.

H2 in-memory DB is used (fresh on each run). Optional console: http://localhost:8080/h2-console (if enabled).

2) Create user (public)
   POST http://localhost:8080/api/user/create
   Body (JSON):
   {
  "username": "alice",
  "password": "secret123",
  "confirmPassword": "secret123"
}

3) Login (get JWT)
   POST http://localhost:8080/login
   Body (JSON):
   {
  "username": "alice",
  "password": "secret123"
}

Response body includes:
{ "token": "Bearer eyJhbGciOi..." }


4) <img width="1461" height="201" alt="image" src="https://github.com/user-attachments/assets/6089488a-094c-4e4d-beac-73c4f6eecb1e" />


