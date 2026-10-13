# Product & Pricing Service

Independent Spring Boot microservice for the Banking Technology Lab. The public resource names and uppercase JSON fields follow `Submission_1_Final.zip` (`productpricingservice (1).yaml` and `product_pricing_erd_v6_ALLCAPS.png`).

## Runtime contract

- Service: `product-service`
- Port: `8083`
- Database: MySQL schema `product_db` (owned only by this service)
- Submitted resource paths: `/products`, `/currencies`, `/api/customer-category`, `/api/product-role`, `/api/product-fee`, `/api/product-rate`, `/api/customer-communication`
- Product integration alias: `/api/product`
- Java 21, Spring Boot 3, Spring Data JPA, Flyway, JWT/RBAC, OpenAPI, Actuator
- Money/rates use `BigDecimal` and SQL `DECIMAL`; dates use ISO `yyyy-MM-dd`

## APIs

See `PRODUCT_API_REFERENCE.md` for request fields, permissions, and the full path contract. Resource responses use uppercase field names; list responses use `items`, `limit`, `offset`, and `total`.

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET/POST | `/products` | Authenticated / BANK_OFFICER or ADMIN | List and create product master records |
| GET/PUT/DELETE | `/products/{PRODUCT_CODE}` | Authenticated / BANK_OFFICER or ADMIN / ADMIN | Read, maintain, or delete draft products |
| PATCH | `/products/{PRODUCT_CODE}/status?STATUS=ACTIVE&REMARKS=Approved` | BANK_OFFICER, ADMIN | Govern status transitions and write history |
| GET | `/products/{PRODUCT_CODE}/pricing?CATEGORY_CODE=SENIOR_CITIZEN&AMOUNT=5000&TENURE_MONTHS=12` | Authenticated | Resolve an active rate row and applicable fees |
| GET/POST/GET by ID/PUT/DELETE | `/currencies` | Authenticated reads; BANK_OFFICER or ADMIN writes; ADMIN deletes | Currency reference data |
| GET/POST/GET by ID/PUT/DELETE | `/api/customer-category` | Same as currency | Customer category reference data |
| GET/POST/GET by ID/PUT/DELETE | `/api/product-role` | BANK_OFFICER, ADMIN | Product roles |
| GET/POST/GET by ID/PUT/DELETE | `/api/product-fee` | Authenticated reads; BANK_OFFICER or ADMIN writes; ADMIN deletes | Fee matrix |
| GET/POST/GET by ID/PUT/DELETE | `/api/product-rate` | Authenticated reads; BANK_OFFICER or ADMIN writes; ADMIN deletes | Rate matrix |
| GET/POST/GET by ID/PUT/DELETE | `/api/customer-communication` | Authenticated reads; BANK_OFFICER or ADMIN writes; ADMIN deletes | Communication templates |

Swagger is at `http://localhost:8083/swagger-ui.html`; health is at `/actuator/health`.

`/api/product` is an alias for the submitted product resource. The earlier camelCase aggregate API remains at `/legacy/api/product` for manual-only transaction, balance, term, and business-rule configuration.

## Run

1. Copy `.env.example` values into your environment. `JWT_SECRET` must exactly match `auth-service`.
2. Run `docker compose up --build`.
3. Obtain an ADMIN JWT from auth-service and call the API with `Authorization: Bearer <token>`.
4. Use `examples/create-fd-product.json` as the first POST body.

Without Docker: create `product_db`, set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and run `mvn spring-boot:run`.

## Data model

The submitted v6 ERD tables are `currency`, `products`, `customer_category`, `product_role`, `product_fee_matrix`, `product_rate_matrix`, and `customer_communication`. Flyway V6 renames the earlier fee and rate tables without discarding their rows. Legacy fee columns remain for compatibility. The manual also calls for these extensions:

- `product_balance_type` and `product_txn_type`: permitted balance and transaction types.
- `product_term_rule`: DAYS/WEEKS/MONTHS min, max, and default values.
- `product_business_rule`: rules such as premature withdrawal, auto-close, unfunded duration, collection/payment behavior, and rate-addition cap.
- `product_status_history`: product status audit with actor, timestamp, and remarks.

Submitted entities carry the `aud_*` fields in the ERD. New products start as `DRAFT`; status changes use the lifecycle endpoint. A product needs at least one rate before activation. Products in `DRAFT` or the legacy `UNDER_ESTABLISHMENT` state may be deleted.

## Verify

Run `mvn clean package` to compile the service and run its tests. The tests cover the submitted tables and routes, product creation and deletion, rate overlap rules, lifecycle history, pricing quotes, and role based access. The Postman collection in `examples/product-service.postman_collection.json` runs a product, role, rate, fee, activation, and quote sequence.

For a laptop presentation with manual Postman requests and visible MySQL Workbench rows, use `EVAL_RUNBOOK.md`. The shared ZIP contains source code, not a compiled JAR: run `mvn clean package` in `product-service`, then run `target/product-service-1.0.0.jar`. The `demo` profile is an optional in-memory H2 fallback; the evaluation runbook uses the regular MySQL profile.

When sharing through Git, commit the `product-service` source folder. `target/` and `dist/` are generated binaries and are ignored. A fresh clone can build the JAR with `mvn clean package`, then run `target/product-service-1.0.0.jar` using the same database settings in `EVAL_RUNBOOK.md`.

## Integration notes

- `calculator-service` and `fd-service` should call `GET /api/product/{PRODUCT_CODE}` and the `/pricing` endpoint. They must not read `product_db` directly.
- Pricing lookup requires `AMOUNT` and `TENURE_MONTHS`; it chooses a category-specific rate before a general rate, checks date and band limits, and rejects inactive products.
- Product-role endpoints follow the first-submission OpenAPI contract: request and response fields are uppercase (`ROLE_ID`, `PRODUCT_CODE`, `ROLE_TYPE`, `IS_MANDATORY`) and the role resource is addressed by `ROLE_ID`.
- JWT claims accepted: `sub` for username and either `role` or `roles`; values may be `ADMIN` or `ROLE_ADMIN`.
- A gateway must route the submitted resource prefixes as well as `/api/product/**` to port 8083. The separate Credexa ZIP currently uses a different product service contract on port 8084.
