# Product & Pricing demo (about 6 minutes)

## Before the presentation

1. Follow `EVAL_RUNBOOK.md` to prepare the local MySQL database and start the packaged JAR.
2. Confirm `http://localhost:8083/actuator/health` returns `UP`.
3. Generate the local `ADMIN` token, then type the request bodies from `EVAL_RUNBOOK.md` in Postman and show the saved rows in MySQL Workbench.
4. Use a new suffix for a second run because MySQL keeps the data. The sample IDs are distinct from the migration's seeded `FD001` product.

## Show this sequence

1. **Design (30 seconds):** Show `Submission_1_Final.zip`'s Product & Pricing ERD and OpenAPI. Point out `PRODUCTS` as the master record and the linked role, rate, and fee matrices. The implementation also exposes currency, customer category, and customer communication resources.
2. **Create a product (1 minute):** Send `POST /products` in Postman. Show HTTP `201`, `PRODUCT_CODE: FDPROF1`, `STATUS: DRAFT`, and the `AUD_*` fields. Say: “The first-submission names are preserved in the API and database model.”
3. **Update a product (1 minute):** Send `PUT /products/FDPROF1`, then rerun the `products` SELECT in Workbench. Show the changed product name and `aud_crud = U`.
4. **Configure it (1 minute):** Send `POST /api/product-role`, `POST /api/product-rate`, and `POST /api/product-fee`. Explain that `ROLEPROF1` allows the owner role; `RATEPROF1` applies to 12–36 months and gives `6.5 + 0.5 = 7.0%`; `FEEPROF1` is a 1% premature-withdrawal fee rule.
5. **Activate it (1 minute):** Send the status `PATCH`, then query `product_status_history` in Workbench. Show `DRAFT -> ACTIVE` and the actor/time. Mention that activation requires a rate row; status cannot be changed through ordinary product update.
6. **Resolve pricing (1 minute):** Send the pricing `GET` for `AMOUNT=10000` and `TENURE_MONTHS=12`. Show `RATE_ID: RATEPROF1`, `EFFECTIVE_RATE: 7.0`, and `FEES` containing `FEEPROF1`. The fee is a schedule entry, not a charge applied at account opening.
7. **Show control rules (1 minute):** If time permits, attempt `PATCH /products/FDPROF1/status?STATUS=DRAFT` and show `400`. Attempt a second rate with the same product/category and overlapping bands and dates to show `409`. A write made with a `CUSTOMER` token returns `403`.

## Close

Show `README.md`, `PRODUCT_API_REFERENCE.md`, and the passing `mvn clean package` test result. Explain that Product Master & Lifecycle is the assigned ownership area, and the full service now supports the first-submission Product & Pricing flow. The separate Credexa gateway and consumers need adaptation before this service can replace their existing product API.
