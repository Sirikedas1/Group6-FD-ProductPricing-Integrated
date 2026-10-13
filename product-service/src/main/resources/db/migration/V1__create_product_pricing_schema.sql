CREATE TABLE customer_category (
 category_code VARCHAR(20) PRIMARY KEY, category_name VARCHAR(50) NOT NULL, description VARCHAR(200),
 aud_eff_date DATE NOT NULL, aud_crud CHAR(1) NOT NULL, aud_user_id VARCHAR(100) NOT NULL, aud_ws_id VARCHAR(100) NOT NULL,
 aud_prgm_id VARCHAR(100) NOT NULL, aud_host_ts TIMESTAMP(6) NOT NULL, aud_local_ts TIMESTAMP(6) NOT NULL,
 aud_acpt_ts TIMESTAMP(6) NOT NULL, aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL, aud_uuid VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE products (
 product_code VARCHAR(20) PRIMARY KEY, product_id VARCHAR(20) NOT NULL UNIQUE, product_name VARCHAR(100) NOT NULL,
 market_name VARCHAR(100) NOT NULL, product_line VARCHAR(30) NOT NULL, product_type VARCHAR(30) NOT NULL,
 product_subtype VARCHAR(30), product_group VARCHAR(30), currency VARCHAR(3) NOT NULL,
 min_term_months INT NOT NULL, max_term_months INT NOT NULL, min_rate DECIMAL(9,6) NOT NULL, max_rate DECIMAL(9,6) NOT NULL,
 effective_date DATE NOT NULL, expiry_date DATE, status VARCHAR(30) NOT NULL, approval_status VARCHAR(20) NOT NULL,
 created_at TIMESTAMP(6) NOT NULL, updated_at TIMESTAMP(6) NOT NULL,
 aud_eff_date DATE NOT NULL, aud_crud CHAR(1) NOT NULL, aud_user_id VARCHAR(100) NOT NULL, aud_ws_id VARCHAR(100) NOT NULL,
 aud_prgm_id VARCHAR(100) NOT NULL, aud_host_ts TIMESTAMP(6) NOT NULL, aud_local_ts TIMESTAMP(6) NOT NULL,
 aud_acpt_ts TIMESTAMP(6) NOT NULL, aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL, aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT chk_term_range CHECK (min_term_months <= max_term_months), CONSTRAINT chk_rate_range CHECK (min_rate <= max_rate)
);
CREATE INDEX idx_product_search ON products(product_type,effective_date,status);

CREATE TABLE product_role (
 role_id VARCHAR(20) PRIMARY KEY, product_code VARCHAR(20) NOT NULL, role_type VARCHAR(30) NOT NULL, is_mandatory BOOLEAN NOT NULL,
 aud_eff_date DATE NOT NULL,aud_crud CHAR(1) NOT NULL,aud_user_id VARCHAR(100) NOT NULL,aud_ws_id VARCHAR(100) NOT NULL,aud_prgm_id VARCHAR(100) NOT NULL,aud_host_ts TIMESTAMP(6) NOT NULL,aud_local_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL,aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT fk_role_product FOREIGN KEY(product_code) REFERENCES products(product_code) ON DELETE CASCADE, UNIQUE(product_code,role_type)
);
CREATE TABLE product_balance_type (
 balance_type_id VARCHAR(20) PRIMARY KEY,product_code VARCHAR(20) NOT NULL,balance_code VARCHAR(20) NOT NULL,classification VARCHAR(30) NOT NULL,
 aud_eff_date DATE NOT NULL,aud_crud CHAR(1) NOT NULL,aud_user_id VARCHAR(100) NOT NULL,aud_ws_id VARCHAR(100) NOT NULL,aud_prgm_id VARCHAR(100) NOT NULL,aud_host_ts TIMESTAMP(6) NOT NULL,aud_local_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL,aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT fk_balance_product FOREIGN KEY(product_code) REFERENCES products(product_code) ON DELETE CASCADE,UNIQUE(product_code,balance_code)
);
CREATE TABLE product_txn_type (
 txn_type_id VARCHAR(20) PRIMARY KEY,product_code VARCHAR(20) NOT NULL,txn_code VARCHAR(20) NOT NULL,txn_name VARCHAR(50) NOT NULL,dc_indicator CHAR(1) NOT NULL,
 aud_eff_date DATE NOT NULL,aud_crud CHAR(1) NOT NULL,aud_user_id VARCHAR(100) NOT NULL,aud_ws_id VARCHAR(100) NOT NULL,aud_prgm_id VARCHAR(100) NOT NULL,aud_host_ts TIMESTAMP(6) NOT NULL,aud_local_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL,aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT fk_txn_product FOREIGN KEY(product_code) REFERENCES products(product_code) ON DELETE CASCADE,CONSTRAINT chk_dc CHECK(dc_indicator IN('D','C')),UNIQUE(product_code,txn_code)
);
CREATE TABLE product_fee_rate_matrix (
 fee_rate_id VARCHAR(20) PRIMARY KEY,product_code VARCHAR(20) NOT NULL,category_code VARCHAR(20) NOT NULL,fee_type VARCHAR(30) NOT NULL,fee_name VARCHAR(50) NOT NULL,
 fee_amount DECIMAL(15,2),fee_percent DECIMAL(9,6),trigger_event VARCHAR(50),additional_rate_percent DECIMAL(9,6),penalty_percent DECIMAL(9,6),effective_date DATE NOT NULL,expiry_date DATE,
 aud_eff_date DATE NOT NULL,aud_crud CHAR(1) NOT NULL,aud_user_id VARCHAR(100) NOT NULL,aud_ws_id VARCHAR(100) NOT NULL,aud_prgm_id VARCHAR(100) NOT NULL,aud_host_ts TIMESTAMP(6) NOT NULL,aud_local_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL,aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT fk_fee_product FOREIGN KEY(product_code) REFERENCES products(product_code) ON DELETE CASCADE,CONSTRAINT fk_fee_category FOREIGN KEY(category_code) REFERENCES customer_category(category_code),INDEX idx_fee_lookup(product_code,category_code,effective_date,expiry_date)
);
CREATE TABLE product_term_rule (
 term_rule_id VARCHAR(20) PRIMARY KEY,product_code VARCHAR(20) NOT NULL,duration_unit VARCHAR(10) NOT NULL,min_value INT NOT NULL,max_value INT NOT NULL,default_value INT NOT NULL,
 aud_eff_date DATE NOT NULL,aud_crud CHAR(1) NOT NULL,aud_user_id VARCHAR(100) NOT NULL,aud_ws_id VARCHAR(100) NOT NULL,aud_prgm_id VARCHAR(100) NOT NULL,aud_host_ts TIMESTAMP(6) NOT NULL,aud_local_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL,aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT fk_term_product FOREIGN KEY(product_code) REFERENCES products(product_code) ON DELETE CASCADE,UNIQUE(product_code,duration_unit),CONSTRAINT chk_term_values CHECK(min_value<=default_value AND default_value<=max_value)
);
CREATE TABLE product_business_rule (
 rule_id VARCHAR(20) PRIMARY KEY,product_code VARCHAR(20) NOT NULL,rule_code VARCHAR(40) NOT NULL,rule_value VARCHAR(200) NOT NULL,enabled BOOLEAN NOT NULL,description VARCHAR(200),
 aud_eff_date DATE NOT NULL,aud_crud CHAR(1) NOT NULL,aud_user_id VARCHAR(100) NOT NULL,aud_ws_id VARCHAR(100) NOT NULL,aud_prgm_id VARCHAR(100) NOT NULL,aud_host_ts TIMESTAMP(6) NOT NULL,aud_local_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL,aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT fk_rule_product FOREIGN KEY(product_code) REFERENCES products(product_code) ON DELETE CASCADE,UNIQUE(product_code,rule_code)
);
CREATE TABLE customer_communication (
 communication_id VARCHAR(20) PRIMARY KEY,product_code VARCHAR(20) NOT NULL,category_code VARCHAR(20) NOT NULL,communication_type VARCHAR(30) NOT NULL,channel VARCHAR(20) NOT NULL,frequency VARCHAR(20) NOT NULL,template_name VARCHAR(50) NOT NULL,is_active BOOLEAN NOT NULL,
 aud_eff_date DATE NOT NULL,aud_crud CHAR(1) NOT NULL,aud_user_id VARCHAR(100) NOT NULL,aud_ws_id VARCHAR(100) NOT NULL,aud_prgm_id VARCHAR(100) NOT NULL,aud_host_ts TIMESTAMP(6) NOT NULL,aud_local_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts TIMESTAMP(6) NOT NULL,aud_acpt_ts_ofst TIMESTAMP(6) NOT NULL,aud_uuid VARCHAR(100) NOT NULL UNIQUE,
 CONSTRAINT fk_comm_product FOREIGN KEY(product_code) REFERENCES products(product_code) ON DELETE CASCADE,CONSTRAINT fk_comm_category FOREIGN KEY(category_code) REFERENCES customer_category(category_code)
);
