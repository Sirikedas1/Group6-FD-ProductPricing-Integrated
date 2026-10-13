-- ============================================================
--  READ-ONLY checks for product_db_dev (changes nothing).
--  Run in MySQL Workbench after product-service has started once
--  and the FDLAB1 test data has been loaded.
-- ============================================================
USE product_db_dev;

-- 1. Flyway: expect 9 rows, versions 1-9, success = 1
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;

-- 2. Tables: expect 13 (12 product tables + flyway_schema_history)
SELECT COUNT(*) AS table_count FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'product_db_dev';

-- 3. Products: FD001 (teammates' seed, DRAFT) and FDLAB1 (DEVELOPMENT TEST product, ACTIVE)
SELECT product_code, product_name, status, min_amount, max_amount, max_rate FROM products ORDER BY product_code;

-- 4. Categories: EMPLOYEE, GENERAL, MILITARY, PREMIUM_USER, SENIOR_CITIZEN, SUPER_SENIOR
SELECT category_code, category_name FROM customer_category ORDER BY category_code;

-- 5. FDLAB1 DEVELOPMENT TEST rates: 5 rows, base 6.50, add-ons 0 / 1 / 1 / 2 / 1.5
SELECT rate_id, category_code, min_tenure_months, max_tenure_months,
       base_rate, additional_rate_percent, effective_rate, effective_date
FROM product_rate_matrix WHERE product_code = 'FDLAB1' ORDER BY rate_id;

-- 6. Currencies: INR, JPY, KWD, USD
SELECT currency_code, currency_name FROM currency ORDER BY currency_code;
