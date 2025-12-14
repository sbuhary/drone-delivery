# Drone Delivery

Minimal Spring Boot 4 / Java 25 backend for a drone delivery system, structured as a modular monolith (auth, security, orders, drones, jobs, admin, common) to ease future service splits. Data is in-memory H2; security is stateless JWT.

## Stack
- Java 25 (build and Docker runtime)
- Spring Boot 4.0 (webmvc, security, data-jpa, validation)
- H2 in-memory DB
- JJWT, SpringDoc OpenAPI

## Run locally
```bash
mvn spring-boot:run
```
Important props (see `src/main/resources/application.properties`):
- `app.jwt.secret` (required)
- `app.jwt.expiration-seconds` (default 3600)

H2 console: `/h2-console` (dev only). Swagger UI: `/swagger-ui.html`.

## Tests
```bash
mvn test
```

## Docker
Build and run:
```bash
docker build -t drone-delivery .
docker run -p 8080:8080 -e APP_JWT_SECRET=change-me drone-delivery
```

## Docker Compose
```bash
docker compose up --build
```
Exposes port 8080 and sets JWT env vars; uses the in-memory H2 database.

## API Basics (sequential first-run flow)
Base path: `/api/v1`. Postman collection: `postman/drone-delivery.postman_collection.json`.

1) Get tokens  
   - `POST /auth/token` with `{ "name": "admin1", "role": "ADMIN" }` → capture token.  
   - `POST /auth/token` with `{ "name": "alice", "role": "ENDUSER" }` → capture token.  
   - `POST /auth/token` with `{ "name": "drone-1", "role": "DRONE" }` → capture token (creates the drone if missing).
2) End-user creates an order  
   - `POST /endusers/me/orders` (Bearer ENDUSER) with origin/destination → store `orderId`.  
   - Optional status check: `GET /endusers/me/orders/{orderId}`.
3) Drone picks it up  
   - `POST /drones/me/jobs/reserve` (Bearer DRONE) → returns job for the order.  
   - `POST /drones/me/orders/grab` (Bearer DRONE) → order becomes `IN_TRANSIT`.
4) Complete or fail delivery  
   - `POST /drones/me/orders/{orderId}/complete` with `{ "result": "DELIVERED" | "FAILED" }`.  
   - Or `POST /drones/me/broken` to trigger handoff job and set order to `AWAITING_HANDOFF`.
5) Admin views/patches  
   - `GET /admin/orders`, `PATCH /admin/orders/{orderId}`.  
   - `GET /admin/drones`, `POST /admin/drones/{id}/broken`, `POST /admin/drones/{id}/fixed`.
