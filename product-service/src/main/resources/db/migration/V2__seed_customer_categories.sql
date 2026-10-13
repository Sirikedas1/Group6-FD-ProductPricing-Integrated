INSERT INTO customer_category(category_code,category_name,description,aud_eff_date,aud_crud,aud_user_id,aud_ws_id,aud_prgm_id,aud_host_ts,aud_local_ts,aud_acpt_ts,aud_acpt_ts_ofst,aud_uuid) VALUES
('GENERAL','General','Standard retail customer',CURRENT_DATE,'C','SYSTEM','MIGRATION','flyway',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID()),
('SENIOR_CITIZEN','Senior Citizen','Eligible senior citizen',CURRENT_DATE,'C','SYSTEM','MIGRATION','flyway',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID()),
('EMPLOYEE','Bank Employee','Eligible bank employee',CURRENT_DATE,'C','SYSTEM','MIGRATION','flyway',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID()),
('MILITARY','Military','Eligible military customer',CURRENT_DATE,'C','SYSTEM','MIGRATION','flyway',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,UUID());
