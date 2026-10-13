INSERT INTO currency (currency_code,currency_name,currency_symbol,decimal_places,is_active,
 aud_eff_date,aud_crud,aud_user_id,aud_ws_id,aud_prgm_id,aud_host_ts,aud_local_ts,
 aud_acpt_ts,aud_acpt_ts_ofst,aud_uuid)
SELECT 'JPY','Japanese Yen','¥',0,TRUE,CURRENT_DATE,'I','SYSTEM','MIGRATION','V9',
 CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID()
WHERE NOT EXISTS (SELECT 1 FROM currency WHERE currency_code='JPY');

INSERT INTO currency (currency_code,currency_name,currency_symbol,decimal_places,is_active,
 aud_eff_date,aud_crud,aud_user_id,aud_ws_id,aud_prgm_id,aud_host_ts,aud_local_ts,
 aud_acpt_ts,aud_acpt_ts_ofst,aud_uuid)
SELECT 'KWD','Kuwaiti Dinar','KD',3,TRUE,CURRENT_DATE,'I','SYSTEM','MIGRATION','V9',
 CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID()
WHERE NOT EXISTS (SELECT 1 FROM currency WHERE currency_code='KWD');

INSERT INTO customer_category (category_code,category_name,description,aud_eff_date,aud_crud,
 aud_user_id,aud_ws_id,aud_prgm_id,aud_host_ts,aud_local_ts,aud_acpt_ts,aud_acpt_ts_ofst,aud_uuid)
SELECT 'SUPER_SENIOR','Super Senior Citizen','Eligible super senior citizen',CURRENT_DATE,'I',
 'SYSTEM','MIGRATION','V9',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID()
WHERE NOT EXISTS (SELECT 1 FROM customer_category WHERE category_code='SUPER_SENIOR');
