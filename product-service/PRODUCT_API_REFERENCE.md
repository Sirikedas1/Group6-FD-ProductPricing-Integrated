# Product-service API reference

This reference follows the first-submission OpenAPI and product-pricing ERD. The resource paths and JSON property names below are frozen for integration.

## Product resource

| Method | Path | Access |
|---|---|---|
| GET | `/products?limit=50&offset=0` | Authenticated |
| POST | `/products` | `BANK_OFFICER`, `ADMIN` |
| GET | `/products/{PRODUCT_CODE}` | Authenticated |
| PUT | `/products/{PRODUCT_CODE}` | `BANK_OFFICER`, `ADMIN` |
| DELETE | `/products/{PRODUCT_CODE}` | `ADMIN` |

The product body uses the first-submission names: `PRODUCT_CODE`, `PRODUCT_ID`, `PRODUCT_NAME`, `PRODUCT_TYPE`, `CURRENCY`, `MIN_AMOUNT`, `MAX_AMOUNT`, `MIN_AGE`, `MIN_RATE`, `MAX_RATE`, `DEFAULT_COMPOUNDING_FREQUENCY`, `DEFAULT_PAYOUT_FREQUENCY`, `SUPPORTED_FREQUENCIES`, `DEFAULT_MATURITY_INSTRUCTION`, `TDS_RATE`, `TDS_EXEMPTION_THRESHOLD`, `EFFECTIVE_DATE`, and `STATUS`.

List responses contain `items`, `limit`, `offset`, and `total`. Product item properties remain uppercase as submitted.

## Product-role resource

| Method | Path | Access |
|---|---|---|
| GET | `/api/product-role?limit=50&offset=0` | `BANK_OFFICER`, `ADMIN` |
| POST | `/api/product-role` | `BANK_OFFICER`, `ADMIN` |
| GET | `/api/product-role/{ROLE_ID}` | `BANK_OFFICER`, `ADMIN` |
| PUT | `/api/product-role/{ROLE_ID}` | `BANK_OFFICER`, `ADMIN` |
| DELETE | `/api/product-role/{ROLE_ID}` | `BANK_OFFICER`, `ADMIN` |

Role bodies use `ROLE_ID`, `PRODUCT_CODE`, `ROLE_TYPE`, and `IS_MANDATORY`. A role type is unique within a product.

## Lifecycle extension

The assigned lifecycle work adds:

- `PATCH /products/{PRODUCT_CODE}/status?STATUS=ACTIVE&REMARKS=Approved`
- `GET /products/{PRODUCT_CODE}/status-history`

The primary lifecycle path is `DRAFT -> ACTIVE -> WITHDRAWN`. A rate row is required before activation. Invalid reverse transitions return `400` and do not create history rows. Product create records the initial `DRAFT` state. Updates preserve status and reject a changed `STATUS`; use the lifecycle path instead.

## Product-rate matrix

| Method | Path | Access |
|---|---|---|
| GET | `/api/product-rate?limit=50&offset=0` | Authenticated |
| POST | `/api/product-rate` | `BANK_OFFICER`, `ADMIN` |
| GET/PUT/DELETE | `/api/product-rate/{RATE_ID}` | Authenticated / `BANK_OFFICER`, `ADMIN` / `ADMIN` |

Rate rows use `RATE_ID`, `PRODUCT_CODE`, `CATEGORY_CODE`, `MIN_TENURE_MONTHS`, `MAX_TENURE_MONTHS`, `MIN_AMOUNT`, `MAX_AMOUNT`, `BASE_RATE`, `ADDITIONAL_RATE_PERCENT`, `EFFECTIVE_RATE`, `EFFECTIVE_DATE`, and `EXPIRY_DATE`. The product and category foreign keys are checked before a row is saved.

Rows for the same product/category cannot overlap across tenure, amount, and effective dates. `EFFECTIVE_RATE` is calculated as base plus additional rate and cannot exceed the product's `MAX_RATE` when one is set.

## Remaining submitted resources

| Resource | Collection path | Item path | Write access |
|---|---|---|---|
| Currency | `/currencies` | `/currencies/{CURRENCY_CODE}` | `BANK_OFFICER`, `ADMIN`; delete `ADMIN` |
| Customer category | `/api/customer-category` | `/api/customer-category/{CATEGORY_CODE}` | `BANK_OFFICER`, `ADMIN`; delete `ADMIN` |
| Product fee | `/api/product-fee` | `/api/product-fee/{FEE_RATE_ID}` | `BANK_OFFICER`, `ADMIN`; delete `ADMIN` |
| Customer communication | `/api/customer-communication` | `/api/customer-communication/{COMMUNICATION_ID}` | `BANK_OFFICER`, `ADMIN`; delete `ADMIN` |

Each collection supports `GET ?limit=50&offset=0` and `POST`; each item supports `GET`, `PUT`, and `DELETE`. Collection responses contain `items`, `limit`, `offset`, and `total`. Writes take the uppercase fields from the first-submission YAML and return direct resource bodies. Deletes return `204`.

All submitted resource responses include the ERD audit fields from `AUD_EFF_DATE` through `AUD_UUID`.

Product fee rows use `FEE_AMOUNT` or `FEE_PERCENT`, with `CATEGORY_CODE` optional for a general fee. `EFFECTIVE_DATE` defaults to today. Customer communications link a product and category to a channel, frequency, and template.

## Pricing lookup extension

`GET /products/{PRODUCT_CODE}/pricing?CATEGORY_CODE=SENIOR_CITIZEN&AMOUNT=5000&TENURE_MONTHS=12&AS_OF=2026-10-09`

The same endpoint is available under `/api/product`. It requires an active product and matching amount, tenure, category, and date bands. The response contains uppercase `RATE_ID`, `BASE_RATE`, `ADDITIONAL_RATE_PERCENT`, `EFFECTIVE_RATE`, and `FEES` fields. A category-specific rate wins over a general rate. It returns `404` when no rate matches.

## Error codes

`400` validation or invalid transition, `401` missing/invalid JWT, `403` insufficient role, `404` missing resource or applicable rate, `409` duplicate code or overlapping rate. Error bodies contain `code`, `message`, `details`, and `traceId`.
