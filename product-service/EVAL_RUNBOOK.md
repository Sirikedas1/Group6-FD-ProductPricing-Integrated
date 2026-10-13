# Postman + MySQL Workbench evaluation

> **Group 6 package note:** the original evaluation password in this file was replaced with `CHOOSE_YOUR_OWN_PASSWORD` before sharing. For the integrated setup, follow `README_INTEGRATION.md` (database `product_db_dev`, user `product_dev`).

These steps use the MySQL server already installed on this Mac at `localhost:3306`. Type the JSON into Postman yourself, then rerun the SELECT statements in MySQL Workbench to show the saved rows. The normal MySQL service is used; do not start the H2 `demo` profile.

## 1. One-time MySQL setup in Workbench

Open **MySQL Workbench** and connect to your existing local MySQL connection with its usual admin password. Open a new SQL tab, paste this, and click the lightning bolt:

```sql
CREATE DATABASE IF NOT EXISTS product_db;
CREATE USER IF NOT EXISTS 'product_eval'@'localhost' IDENTIFIED BY 'CHOOSE_YOUR_OWN_PASSWORD';
ALTER USER 'product_eval'@'localhost' IDENTIFIED BY 'CHOOSE_YOUR_OWN_PASSWORD';
GRANT ALL PRIVILEGES ON product_db.* TO 'product_eval'@'localhost';
```

This creates a separate local database user for the service. It does not change your MySQL admin password.

## 2. Start the backend in Terminal window 1

If you extracted the source ZIP on another laptop, first open Terminal in its `product-service` folder and run `mvn clean package`. Then set the database variables below and start the built JAR. On this Mac, the path shown here is the existing working copy; adjust it to the extracted folder on another laptop.

Paste these lines and leave this Terminal window open:

```bash
cd /Users/ananyashriram/Desktop/sem5/banking_tech__lab/product-service
export DB_URL='jdbc:mysql://127.0.0.1:3306/product_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME=product_eval
export DB_PASSWORD=CHOOSE_YOUR_OWN_PASSWORD
mvn clean package
java -jar target/product-service-1.0.0.jar
```

Wait for `Started ProductServiceApplication`. On the first start, Flyway creates the tables and seeds `INR` and the reference categories. It also seeds `FD001`, so the requests below use the separate `FDPROF1` ID.

## 3. Check the backend and copy a token in Terminal window 2

```bash
cd /Users/ananyashriram/Desktop/sem5/banking_tech__lab/product-service
curl -s http://localhost:8083/actuator/health | jq
python3 scripts/demo_token.py | pbcopy
open -a Postman
open -a MySQLWorkbench
```

Health should say `"status": "UP"`. The token is now on the clipboard. This is a local four-hour `ADMIN` token for the evaluation; the regular system can instead use a token issued by its auth service.

In Postman, create a collection named **Product Pricing Eval**. Open the collection's **Authorization** tab, choose **Bearer Token**, paste the token, and save. Add each request below to this collection; each request should use **Inherit auth from parent**. For POST and PUT, choose **Body → raw → JSON** and paste the shown JSON. A request without a body should use **Body → none**.

## 4. Open the database view in Workbench

Click the refresh icon beside **SCHEMAS**, expand `product_db` → **Tables**, and open a SQL tab. Run this after the backend has started:

```sql
SHOW TABLES FROM product_db;

SELECT product_code, product_name, status, currency, min_amount, max_amount,
       aud_crud, aud_user_id
FROM product_db.products WHERE product_code = 'FDPROF1';
```

Initially the SELECT for `FDPROF1` is empty. After each Postman write, click the lightning bolt again to refresh the result.

## 5. Type these requests in Postman

### A. Create product

**POST** `http://localhost:8083/products` → **Body → raw → JSON**:

```json
{
  "PRODUCT_CODE": "FDPROF1",
  "PRODUCT_ID": "PROF1001",
  "PRODUCT_NAME": "Fixed Deposit Demo",
  "PRODUCT_TYPE": "FD",
  "CURRENCY": "INR",
  "MIN_AMOUNT": 1000,
  "MAX_AMOUNT": 1000000,
  "MIN_RATE": 5,
  "MAX_RATE": 8,
  "EFFECTIVE_DATE": "2026-01-01",
  "STATUS": "DRAFT"
}
```

Expected **201**. Rerun the `products` SELECT in Workbench: `FDPROF1` appears with `DRAFT`.

### B. Update product

**PUT** `http://localhost:8083/products/FDPROF1` → **Body → raw → JSON**:

```json
{
  "PRODUCT_CODE": "FDPROF1",
  "PRODUCT_ID": "PROF1001",
  "PRODUCT_NAME": "Fixed Deposit Demo Updated",
  "PRODUCT_TYPE": "FD",
  "CURRENCY": "INR",
  "MIN_AMOUNT": 1000,
  "MAX_AMOUNT": 1000000,
  "MIN_RATE": 5,
  "MAX_RATE": 8,
  "EFFECTIVE_DATE": "2026-01-01",
  "STATUS": "DRAFT"
}
```

Expected **200**. Rerun the Workbench query: the name now ends in `Updated` and `aud_crud` is `U`.

### C. Add a product role

**POST** `http://localhost:8083/api/product-role` → **Body → raw → JSON**:

```json
{
  "ROLE_ID": "ROLEPROF1",
  "PRODUCT_CODE": "FDPROF1",
  "ROLE_TYPE": "OWNER",
  "IS_MANDATORY": true
}
```

Expected **201**. In Workbench:

```sql
SELECT role_id, product_code, role_type, is_mandatory
FROM product_db.product_role WHERE product_code = 'FDPROF1';
```

### D. Add the rate rule

**POST** `http://localhost:8083/api/product-rate` → **Body → raw → JSON**:

```json
{
  "RATE_ID": "RATEPROF1",
  "PRODUCT_CODE": "FDPROF1",
  "MIN_TENURE_MONTHS": 12,
  "MAX_TENURE_MONTHS": 36,
  "MIN_AMOUNT": 1000,
  "MAX_AMOUNT": 1000000,
  "BASE_RATE": 6.5,
  "ADDITIONAL_RATE_PERCENT": 0.5,
  "EFFECTIVE_DATE": "2026-01-01"
}
```

Expected **201**. In Workbench:

```sql
SELECT rate_id, product_code, min_tenure_months, max_tenure_months,
       base_rate, additional_rate_percent, effective_rate
FROM product_db.product_rate_matrix WHERE product_code = 'FDPROF1';
```

Point out that `6.5 + 0.5 = 7.0` is stored as the effective rate.

### E. Add the fee rule

**POST** `http://localhost:8083/api/product-fee` → **Body → raw → JSON**:

```json
{
  "FEE_RATE_ID": "FEEPROF1",
  "PRODUCT_CODE": "FDPROF1",
  "FEE_TYPE": "PENALTY",
  "FEE_NAME": "Premature withdrawal",
  "FEE_PERCENT": 1,
  "TRIGGER_EVENT": "PREMATURE_WITHDRAWAL",
  "EFFECTIVE_DATE": "2026-01-01"
}
```

Expected **201**. In Workbench:

```sql
SELECT fee_rate_id, product_code, fee_type, fee_percent, trigger_event
FROM product_db.product_fee_matrix WHERE product_code = 'FDPROF1';
```

### F. Activate the product

**PATCH** `http://localhost:8083/products/FDPROF1/status?STATUS=ACTIVE&REMARKS=Approved` → **Body → none**.

Expected **200**. Rerun the `products` SELECT: status is now `ACTIVE`. In Workbench, also run:

```sql
SELECT product_code, previous_status, new_status, changed_by, remarks, changed_at
FROM product_db.product_status_history
WHERE product_code = 'FDPROF1' ORDER BY changed_at;
```

Show the `DRAFT → ACTIVE` history row with the approval remark.

### G. Get a price quote

**GET** `http://localhost:8083/products/FDPROF1/pricing?AMOUNT=10000&TENURE_MONTHS=12` → **Body → none**.

Expected **200** with `RATE_ID: RATEPROF1`, `EFFECTIVE_RATE: 7.0`, and `FEEPROF1` in `FEES`. The fee is a configured premature-withdrawal rule, not a charge applied at opening.

## If you rehearse more than once

MySQL keeps the rows when the service stops. Repeating **Create product** with `FDPROF1` gives `409`. For the next run, change the suffix consistently, for example `FDPROF2`, `PROF1002`, `ROLEPROF2`, `RATEPROF2`, and `FEEPROF2`. Do not delete an existing database just to reset this demo.
