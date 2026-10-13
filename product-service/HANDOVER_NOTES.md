# Product-service handover notes

## Ownership

Product Master & Lifecycle owns the `products` and `product_role` resources, status changes, and status history. The service also implements all other first-submission resources: currency, customer category, fee matrix, rate matrix, and customer communication. Product and role references use `PRODUCT_CODE` and `ROLE_ID` from the first-submission ERD.

## Integration notes

- Calculator and FD services should call `GET /products/{PRODUCT_CODE}` for the first-submission resource shape.
- Product-role lookups use `GET /api/product-role/{ROLE_ID}` or the product resource's product code contract.
- No service should join another service's database schema.
- Product lifecycle changes are recorded in `product_status_history`.
- Rate-matrix rows are stored in `product_rate_matrix`; fees are stored in `product_fee_matrix`. Flyway V6 renames the earlier tables while retaining existing rows.
- The shared JWT filter accepts `role` or `roles` claims and maps them to Spring `ROLE_*` authorities.

## Local run

1. Start MySQL with `docker compose up --build` from `product-service`.
2. Set `DB_PASSWORD` and `JWT_SECRET` in your environment or a local `.env` file. The same database password is supplied to MySQL and the service.
3. Obtain a JWT containing `ADMIN` or `BANK_OFFICER`.
4. Run the first-submission requests from `examples/product-service.postman_collection.json`.
5. Verify `/actuator/health` and `/swagger-ui.html`.

## Known boundary

The rate-matrix contract is implemented at `/api/product-rate`; the joint checkpoint should verify a product, category, rate, and fee round trip with the same uppercase field names as the first submission. The pricing endpoint also requires `AMOUNT` and `TENURE_MONTHS` so it can select an applicable row.

`Submission_1_Final.zip` is the contract source. The separate `Credexa-Application-main.zip` uses port 8084, `/api/products`, camelCase fields, and numeric product IDs; its gateway, UI, and consumers must be adapted before switching to this service on port 8083.
