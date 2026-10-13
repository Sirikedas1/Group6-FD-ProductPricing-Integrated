INSERT INTO products (
 product_code, product_id, product_name, market_name, product_line, product_type, currency,
 min_term_months, max_term_months, min_rate, max_rate, effective_date, status, approval_status,
 min_amount, max_amount, min_age, default_compounding_frequency, default_payout_frequency,
 supported_frequencies, default_maturity_instruction, tds_rate, tds_exemption_threshold,
 created_at, updated_at, aud_eff_date, aud_crud, aud_user_id, aud_ws_id, aud_prgm_id,
 aud_host_ts, aud_local_ts, aud_acpt_ts, aud_acpt_ts_ofst, aud_uuid
) VALUES (
 'FD001', '10001', 'Fixed Deposit - Standard', 'Standard FD', 'TERM_DEPOSIT', 'FD', 'INR',
 6, 60, 5.000000, 7.500000, CURRENT_DATE, 'DRAFT', 'DRAFT',
 1000.00, 10000000.00, 18, 'MONTHLY', 'MATURITY', 'MONTHLY,QUARTERLY,MATURITY',
 'RENEW_PRINCIPAL', 10.000000, 40000.00,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_DATE, 'I', 'SYSTEM', 'MIGRATION', 'seed-product',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, UUID()
);
