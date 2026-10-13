-- ============================================================
--  MANUAL ALTERNATIVE to integration\setup_local_config.py
--  (that script writes this file for you, with your password filled in,
--   as local-config\create_product_db_dev.sql)
--
--  Run as root in MySQL Workbench. Replace REPLACE_WITH_YOUR_PRODUCT_DEV_PASSWORD
--  with the SAME password you put in local-config\dev-secrets.properties
--  (letters and digits only). Do not save the edited copy in this folder.
--
--  Creates ONLY a new empty database and a new restricted user.
--  No "IF NOT EXISTS": if a name already exists, MySQL stops with an error.
--  The tables are created by product-service's own 9 Flyway migrations.
-- ============================================================
CREATE DATABASE product_db_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER 'product_dev'@'localhost' IDENTIFIED BY 'REPLACE_WITH_YOUR_PRODUCT_DEV_PASSWORD';
GRANT ALL PRIVILEGES ON product_db_dev.* TO 'product_dev'@'localhost';
SHOW GRANTS FOR 'product_dev'@'localhost';
