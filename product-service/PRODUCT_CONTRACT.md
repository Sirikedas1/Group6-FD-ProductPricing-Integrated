# Product-service contract baseline

All new product-service work must follow the first-submission package:
`Submission_1_Final/productpricingservice (1).yaml` and the submitted product-pricing ERD.

## Product-role contract

- `POST /api/product-role`
- `GET /api/product-role/{ROLE_ID}`
- `PUT /api/product-role/{ROLE_ID}`
- `DELETE /api/product-role/{ROLE_ID}`
- Allowed roles: `BANK_OFFICER`, `ADMIN`
- JSON fields: `ROLE_ID`, `PRODUCT_CODE`, `ROLE_TYPE`, `IS_MANDATORY`
- `ROLE_ID` and `PRODUCT_CODE` reference the submitted ERD keys.

The first-submission API uses direct resource responses for this contract and `204 No Content` for delete. Do not introduce nested role paths or rename these fields without updating the first-submission contract.
