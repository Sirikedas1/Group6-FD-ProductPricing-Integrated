-- Preserve existing fee rows while adopting the submitted v6 table name.
ALTER TABLE product_fee_rate_matrix RENAME TO product_fee_matrix;
ALTER TABLE product_rate RENAME TO product_rate_matrix;
ALTER TABLE product_fee_matrix MODIFY category_code VARCHAR(20) NULL;

CREATE TABLE currency (
 currency_code VARCHAR(3) PRIMARY KEY,
 currency_name VARCHAR(50) NOT NULL,
 currency_symbol VARCHAR(5),
 decimal_places INT NOT NULL,
 is_active BOOLEAN NOT NULL,
 aud_eff_date DATE NOT NULL,
 aud_crud CHAR(1) NOT NULL,
 aud_user_id VARCHAR(100) NOT NULL,
 aud_ws_id VARCHAR(100) NOT NULL,
 aud_prgm_id VARCHAR(100) NOT NULL,
 aud_host_ts TIMESTAMP(6) NOT NULL,
 aud_local_ts TIMESTAMP(6) NOT NULL,
 aud_acpt_ts TIMESTAMP(6) NOT NULL,
 aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL,
 aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT chk_currency_decimal_places CHECK (decimal_places BETWEEN 0 AND 4)
);

-- Existing product currencies must be represented before adding the FK.
INSERT INTO currency (currency_code,currency_name,currency_symbol,decimal_places,is_active,
 aud_eff_date,aud_crud,aud_user_id,aud_ws_id,aud_prgm_id,aud_host_ts,aud_local_ts,
 aud_acpt_ts,aud_acpt_ts_ofst,aud_uuid)
SELECT DISTINCT p.currency,p.currency,p.currency,2,TRUE,
 CURRENT_DATE,'I','SYSTEM','MIGRATION','V6',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,
 CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID()
FROM products p;

INSERT INTO currency (currency_code,currency_name,currency_symbol,decimal_places,is_active,
 aud_eff_date,aud_crud,aud_user_id,aud_ws_id,aud_prgm_id,aud_host_ts,aud_local_ts,
 aud_acpt_ts,aud_acpt_ts_ofst,aud_uuid)
SELECT 'USD','US Dollar','$',2,TRUE,CURRENT_DATE,'I','SYSTEM','MIGRATION','V6',
 CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID()
WHERE NOT EXISTS (SELECT 1 FROM currency WHERE currency_code='USD');

UPDATE currency SET currency_name='Indian Rupee',currency_symbol='₹',decimal_places=2
WHERE currency_code='INR';
ALTER TABLE products ADD CONSTRAINT fk_product_currency
 FOREIGN KEY (currency) REFERENCES currency(currency_code);
