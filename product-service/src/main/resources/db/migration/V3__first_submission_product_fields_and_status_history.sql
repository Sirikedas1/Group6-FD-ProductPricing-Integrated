ALTER TABLE products ADD COLUMN min_amount DECIMAL(15,2) NULL;
ALTER TABLE products ADD COLUMN max_amount DECIMAL(15,2) NULL;
ALTER TABLE products ADD COLUMN min_age INT NULL;
ALTER TABLE products ADD COLUMN default_compounding_frequency VARCHAR(20) NULL;
ALTER TABLE products ADD COLUMN default_payout_frequency VARCHAR(20) NULL;
ALTER TABLE products ADD COLUMN supported_frequencies VARCHAR(200) NULL;
ALTER TABLE products ADD COLUMN default_maturity_instruction VARCHAR(30) NULL;
ALTER TABLE products ADD COLUMN tds_rate DECIMAL(9,6) NULL;
ALTER TABLE products ADD COLUMN tds_exemption_threshold DECIMAL(15,2) NULL;

CREATE TABLE product_status_history (
 history_id VARCHAR(36) PRIMARY KEY,
 product_code VARCHAR(20) NOT NULL,
 previous_status VARCHAR(30),
 new_status VARCHAR(30) NOT NULL,
 changed_by VARCHAR(100) NOT NULL,
 changed_at TIMESTAMP(6) NOT NULL,
 CONSTRAINT fk_status_history_product FOREIGN KEY(product_code) REFERENCES products(product_code) ON DELETE CASCADE
);
CREATE INDEX idx_status_history_product_time ON product_status_history(product_code, changed_at);
