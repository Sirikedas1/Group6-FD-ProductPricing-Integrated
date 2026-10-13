-- ============================================================================
--  Group 6 - Product & Pricing database export (product_db_dev)
--
--  *** DEVELOPMENT TEST DATA ONLY - NOT REAL BANKING DATA OR FINAL RATES ***
--
--  Contents: the 12 tables created by product-service's ORIGINAL 9 Flyway
--  migrations (V1-V9), Flyway's own history table (flyway_schema_history,
--  at version 9), the teammates' seed data (FD001 DRAFT, categories,
--  currencies) and the FD Calculator test product FDLAB1 with 5 test rates
--  (base 6.50 + GENERAL 0 / SENIOR_CITIZEN 1 / EMPLOYEE 1 / PREMIUM_USER 2 /
--  SUPER_SENIOR 1.5). No MySQL users, passwords, customers or secrets.
--
--  HOW TO USE (see README_INTEGRATION.md, "Option B"):
--    Import ONLY into an EMPTY database (no tables). Do NOT run
--    integration\seed_product_data.py before importing.
--    After import, product-service starts with "Schema is up to date. No
--    migration necessary" because flyway_schema_history is included.
--    Running the seed script afterwards is safe: it reports "exists".
--  This file has no CREATE DATABASE / USE line: choose the target database
--  when you import it.
-- ============================================================================
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: product_db_dev
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `currency`
--

DROP TABLE IF EXISTS `currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `currency` (
  `currency_code` varchar(3) NOT NULL,
  `currency_name` varchar(50) NOT NULL,
  `currency_symbol` varchar(5) DEFAULT NULL,
  `decimal_places` int NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`currency_code`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  CONSTRAINT `chk_currency_decimal_places` CHECK ((`decimal_places` between 0 and 4))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency`
--

LOCK TABLES `currency` WRITE;
/*!40000 ALTER TABLE `currency` DISABLE KEYS */;
INSERT INTO `currency` VALUES ('INR','Indian Rupee','₹',2,1,'2026-10-13','I','SYSTEM','MIGRATION','V6','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','215bf9ee-c66d-11f1-964d-dc4546c9cc45'),('JPY','Japanese Yen','¥',0,1,'2026-10-13','I','SYSTEM','MIGRATION','V9','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','21bdc8c2-c66d-11f1-964d-dc4546c9cc45'),('KWD','Kuwaiti Dinar','KD',3,1,'2026-10-13','I','SYSTEM','MIGRATION','V9','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','21be1db0-c66d-11f1-964d-dc4546c9cc45'),('USD','US Dollar','$',2,1,'2026-10-13','I','SYSTEM','MIGRATION','V6','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','215c723d-c66d-11f1-964d-dc4546c9cc45');
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_category`
--

DROP TABLE IF EXISTS `customer_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_category` (
  `category_code` varchar(20) NOT NULL,
  `category_name` varchar(50) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`category_code`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_category`
--

LOCK TABLES `customer_category` WRITE;
/*!40000 ALTER TABLE `customer_category` DISABLE KEYS */;
INSERT INTO `customer_category` VALUES ('EMPLOYEE','Bank Employee','Eligible bank employee','2026-10-13','C','SYSTEM','MIGRATION','flyway','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','207b9df0-c66d-11f1-964d-dc4546c9cc45'),('GENERAL','General','Standard retail customer','2026-10-13','C','SYSTEM','MIGRATION','flyway','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','207b58ef-c66d-11f1-964d-dc4546c9cc45'),('MILITARY','Military','Eligible military customer','2026-10-13','C','SYSTEM','MIGRATION','flyway','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','207ba196-c66d-11f1-964d-dc4546c9cc45'),('PREMIUM_USER','Premium User','Premium customer (FD Calculator lab test category)','2026-10-13','C','demo-professor','API','product-service','2026-10-12 13:16:20.182504','2026-10-12 13:16:20.182504','2026-10-12 13:16:20.182504','2026-10-12 13:16:20.182504','f202bdee-b301-4874-8c16-6b1203887a4c'),('SENIOR_CITIZEN','Senior Citizen','Eligible senior citizen','2026-10-13','C','SYSTEM','MIGRATION','flyway','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','2026-10-12 18:45:42.000000','207b7bf8-c66d-11f1-964d-dc4546c9cc45'),('SUPER_SENIOR','Super Senior Citizen','Eligible super senior citizen','2026-10-13','I','SYSTEM','MIGRATION','V9','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','2026-10-12 18:45:44.000000','21be932c-c66d-11f1-964d-dc4546c9cc45');
/*!40000 ALTER TABLE `customer_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_communication`
--

DROP TABLE IF EXISTS `customer_communication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_communication` (
  `communication_id` varchar(20) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `category_code` varchar(20) NOT NULL,
  `communication_type` varchar(30) NOT NULL,
  `channel` varchar(20) NOT NULL,
  `frequency` varchar(20) NOT NULL,
  `template_name` varchar(50) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`communication_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  KEY `fk_comm_product` (`product_code`),
  KEY `fk_comm_category` (`category_code`),
  CONSTRAINT `fk_comm_category` FOREIGN KEY (`category_code`) REFERENCES `customer_category` (`category_code`),
  CONSTRAINT `fk_comm_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_communication`
--

LOCK TABLES `customer_communication` WRITE;
/*!40000 ALTER TABLE `customer_communication` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_communication` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create product pricing schema','SQL','V1__create_product_pricing_schema.sql',1396306788,'product_dev','2026-10-12 18:45:42',418,1),(2,'2','seed customer categories','SQL','V2__seed_customer_categories.sql',-1606224258,'product_dev','2026-10-12 18:45:42',53,1),(3,'3','first submission product fields and status history','SQL','V3__first_submission_product_fields_and_status_history.sql',-1343254762,'product_dev','2026-10-12 18:45:43',1062,1),(4,'4','seed sample product','SQL','V4__seed_sample_product.sql',-50803748,'product_dev','2026-10-12 18:45:43',10,1),(5,'5','create product rate matrix','SQL','V5__create_product_rate_matrix.sql',210227118,'product_dev','2026-10-12 18:45:43',90,1),(6,'6','align first submission model','SQL','V6__align_first_submission_model.sql',422369934,'product_dev','2026-10-12 18:45:44',321,1),(7,'7','status change remarks','SQL','V7__status_change_remarks.sql',264667852,'product_dev','2026-10-12 18:45:44',64,1),(8,'8','optional product rate limits','SQL','V8__optional_product_rate_limits.sql',-171507292,'product_dev','2026-10-12 18:45:44',327,1),(9,'9','seed submitted reference data','SQL','V9__seed_submitted_reference_data.sql',1672567564,'product_dev','2026-10-12 18:45:44',14,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_balance_type`
--

DROP TABLE IF EXISTS `product_balance_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_balance_type` (
  `balance_type_id` varchar(20) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `balance_code` varchar(20) NOT NULL,
  `classification` varchar(30) NOT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`balance_type_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  UNIQUE KEY `product_code` (`product_code`,`balance_code`),
  CONSTRAINT `fk_balance_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_balance_type`
--

LOCK TABLES `product_balance_type` WRITE;
/*!40000 ALTER TABLE `product_balance_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_balance_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_business_rule`
--

DROP TABLE IF EXISTS `product_business_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_business_rule` (
  `rule_id` varchar(20) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `rule_code` varchar(40) NOT NULL,
  `rule_value` varchar(200) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`rule_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  UNIQUE KEY `product_code` (`product_code`,`rule_code`),
  CONSTRAINT `fk_rule_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_business_rule`
--

LOCK TABLES `product_business_rule` WRITE;
/*!40000 ALTER TABLE `product_business_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_business_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_fee_matrix`
--

DROP TABLE IF EXISTS `product_fee_matrix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_fee_matrix` (
  `fee_rate_id` varchar(20) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `category_code` varchar(20) DEFAULT NULL,
  `fee_type` varchar(30) NOT NULL,
  `fee_name` varchar(50) NOT NULL,
  `fee_amount` decimal(15,2) DEFAULT NULL,
  `fee_percent` decimal(9,6) DEFAULT NULL,
  `trigger_event` varchar(50) DEFAULT NULL,
  `additional_rate_percent` decimal(9,6) DEFAULT NULL,
  `penalty_percent` decimal(9,6) DEFAULT NULL,
  `effective_date` date NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`fee_rate_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  KEY `fk_fee_category` (`category_code`),
  KEY `idx_fee_lookup` (`product_code`,`category_code`,`effective_date`,`expiry_date`),
  CONSTRAINT `fk_fee_category` FOREIGN KEY (`category_code`) REFERENCES `customer_category` (`category_code`),
  CONSTRAINT `fk_fee_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_fee_matrix`
--

LOCK TABLES `product_fee_matrix` WRITE;
/*!40000 ALTER TABLE `product_fee_matrix` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_fee_matrix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_rate_matrix`
--

DROP TABLE IF EXISTS `product_rate_matrix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_rate_matrix` (
  `rate_id` varchar(20) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `category_code` varchar(20) DEFAULT NULL,
  `min_tenure_months` int DEFAULT NULL,
  `max_tenure_months` int DEFAULT NULL,
  `min_amount` decimal(15,2) DEFAULT NULL,
  `max_amount` decimal(15,2) DEFAULT NULL,
  `base_rate` decimal(9,6) NOT NULL,
  `additional_rate_percent` decimal(9,6) DEFAULT NULL,
  `effective_rate` decimal(9,6) DEFAULT NULL,
  `effective_date` date DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`rate_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  KEY `fk_rate_category` (`category_code`),
  KEY `idx_product_rate_lookup` (`product_code`,`category_code`,`effective_date`,`expiry_date`),
  CONSTRAINT `fk_rate_category` FOREIGN KEY (`category_code`) REFERENCES `customer_category` (`category_code`),
  CONSTRAINT `fk_rate_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`),
  CONSTRAINT `chk_rate_amount` CHECK (((`min_amount` is null) or (`max_amount` is null) or (`min_amount` <= `max_amount`))),
  CONSTRAINT `chk_rate_tenure` CHECK (((`min_tenure_months` is null) or (`max_tenure_months` is null) or (`min_tenure_months` <= `max_tenure_months`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_rate_matrix`
--

LOCK TABLES `product_rate_matrix` WRITE;
/*!40000 ALTER TABLE `product_rate_matrix` DISABLE KEYS */;
INSERT INTO `product_rate_matrix` VALUES ('LAB1-EMPLOYEE','FDLAB1','EMPLOYEE',0,120,NULL,NULL,6.500000,1.000000,7.500000,'2026-01-01',NULL,'2026-10-13','C','demo-professor','API','product-service','2026-10-12 13:16:20.899595','2026-10-12 13:16:20.899595','2026-10-12 13:16:20.899595','2026-10-12 13:16:20.899595','88601cd6-2e28-4cd3-8917-27c0f702c6f9'),('LAB1-GENERAL','FDLAB1','GENERAL',0,120,NULL,NULL,6.500000,0.000000,6.500000,'2026-01-01',NULL,'2026-10-13','C','demo-professor','API','product-service','2026-10-12 13:16:20.686563','2026-10-12 13:16:20.686563','2026-10-12 13:16:20.686563','2026-10-12 13:16:20.686563','d2a9544e-96cc-4b16-b105-32cec03ce866'),('LAB1-PREMIUM','FDLAB1','PREMIUM_USER',0,120,NULL,NULL,6.500000,2.000000,8.500000,'2026-01-01',NULL,'2026-10-13','C','demo-professor','API','product-service','2026-10-12 13:16:21.001068','2026-10-12 13:16:21.001068','2026-10-12 13:16:21.001068','2026-10-12 13:16:21.001068','ba02d113-e630-47ff-8a17-ed007805bb39'),('LAB1-SENIOR','FDLAB1','SENIOR_CITIZEN',0,120,NULL,NULL,6.500000,1.000000,7.500000,'2026-01-01',NULL,'2026-10-13','C','demo-professor','API','product-service','2026-10-12 13:16:20.814012','2026-10-12 13:16:20.814012','2026-10-12 13:16:20.814012','2026-10-12 13:16:20.814012','d9ab082a-95fd-4a1d-af1f-1ad4c0d70e15'),('LAB1-SUPERSR','FDLAB1','SUPER_SENIOR',0,120,NULL,NULL,6.500000,1.500000,8.000000,'2026-01-01',NULL,'2026-10-13','C','demo-professor','API','product-service','2026-10-12 13:16:21.148341','2026-10-12 13:16:21.148341','2026-10-12 13:16:21.148341','2026-10-12 13:16:21.148341','972174b0-57ba-4919-846a-728ff0650ce1');
/*!40000 ALTER TABLE `product_rate_matrix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_role`
--

DROP TABLE IF EXISTS `product_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_role` (
  `role_id` varchar(20) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `role_type` varchar(30) NOT NULL,
  `is_mandatory` tinyint(1) NOT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  UNIQUE KEY `product_code` (`product_code`,`role_type`),
  CONSTRAINT `fk_role_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_role`
--

LOCK TABLES `product_role` WRITE;
/*!40000 ALTER TABLE `product_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_status_history`
--

DROP TABLE IF EXISTS `product_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_status_history` (
  `history_id` varchar(36) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `previous_status` varchar(30) DEFAULT NULL,
  `new_status` varchar(30) NOT NULL,
  `changed_by` varchar(100) NOT NULL,
  `changed_at` timestamp(6) NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  KEY `idx_status_history_product_time` (`product_code`,`changed_at`),
  CONSTRAINT `fk_status_history_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_status_history`
--

LOCK TABLES `product_status_history` WRITE;
/*!40000 ALTER TABLE `product_status_history` DISABLE KEYS */;
INSERT INTO `product_status_history` VALUES ('806128f8-92cc-495b-9d7c-b9d22615d3c2','FDLAB1','DRAFT','ACTIVE','demo-professor','2026-10-12 13:16:21.235621','FD Calculator lab test'),('9a124eaf-6cd8-4442-8be4-4566635ba5d4','FDLAB1',NULL,'DRAFT','demo-professor','2026-10-12 13:16:20.527184','Product created');
/*!40000 ALTER TABLE `product_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_term_rule`
--

DROP TABLE IF EXISTS `product_term_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_term_rule` (
  `term_rule_id` varchar(20) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `duration_unit` varchar(10) NOT NULL,
  `min_value` int NOT NULL,
  `max_value` int NOT NULL,
  `default_value` int NOT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`term_rule_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  UNIQUE KEY `product_code` (`product_code`,`duration_unit`),
  CONSTRAINT `fk_term_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`) ON DELETE CASCADE,
  CONSTRAINT `chk_term_values` CHECK (((`min_value` <= `default_value`) and (`default_value` <= `max_value`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_term_rule`
--

LOCK TABLES `product_term_rule` WRITE;
/*!40000 ALTER TABLE `product_term_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_term_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_txn_type`
--

DROP TABLE IF EXISTS `product_txn_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_txn_type` (
  `txn_type_id` varchar(20) NOT NULL,
  `product_code` varchar(20) NOT NULL,
  `txn_code` varchar(20) NOT NULL,
  `txn_name` varchar(50) NOT NULL,
  `dc_indicator` char(1) NOT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  PRIMARY KEY (`txn_type_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  UNIQUE KEY `product_code` (`product_code`,`txn_code`),
  CONSTRAINT `fk_txn_product` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`) ON DELETE CASCADE,
  CONSTRAINT `chk_dc` CHECK ((`dc_indicator` in (_utf8mb4'D',_utf8mb4'C')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_txn_type`
--

LOCK TABLES `product_txn_type` WRITE;
/*!40000 ALTER TABLE `product_txn_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_txn_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_code` varchar(20) NOT NULL,
  `product_id` varchar(20) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `market_name` varchar(100) NOT NULL,
  `product_line` varchar(30) NOT NULL,
  `product_type` varchar(30) NOT NULL,
  `product_subtype` varchar(30) DEFAULT NULL,
  `product_group` varchar(30) DEFAULT NULL,
  `currency` varchar(3) NOT NULL,
  `min_term_months` int NOT NULL,
  `max_term_months` int NOT NULL,
  `min_rate` decimal(9,6) DEFAULT NULL,
  `max_rate` decimal(9,6) DEFAULT NULL,
  `effective_date` date NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `approval_status` varchar(20) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `updated_at` timestamp(6) NOT NULL,
  `aud_eff_date` date NOT NULL,
  `aud_crud` char(1) NOT NULL,
  `aud_user_id` varchar(100) NOT NULL,
  `aud_ws_id` varchar(100) NOT NULL,
  `aud_prgm_id` varchar(100) NOT NULL,
  `aud_host_ts` timestamp(6) NOT NULL,
  `aud_local_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts` timestamp(6) NOT NULL,
  `aud_acpt_ts_ofst` timestamp(6) NOT NULL,
  `aud_uuid` varchar(100) NOT NULL,
  `min_amount` decimal(15,2) DEFAULT NULL,
  `max_amount` decimal(15,2) DEFAULT NULL,
  `min_age` int DEFAULT NULL,
  `default_compounding_frequency` varchar(20) DEFAULT NULL,
  `default_payout_frequency` varchar(20) DEFAULT NULL,
  `supported_frequencies` varchar(200) DEFAULT NULL,
  `default_maturity_instruction` varchar(30) DEFAULT NULL,
  `tds_rate` decimal(9,6) DEFAULT NULL,
  `tds_exemption_threshold` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`product_code`),
  UNIQUE KEY `product_id` (`product_id`),
  UNIQUE KEY `aud_uuid` (`aud_uuid`),
  KEY `idx_product_search` (`product_type`,`effective_date`,`status`),
  KEY `fk_product_currency` (`currency`),
  CONSTRAINT `fk_product_currency` FOREIGN KEY (`currency`) REFERENCES `currency` (`currency_code`),
  CONSTRAINT `chk_rate_range` CHECK ((`min_rate` <= `max_rate`)),
  CONSTRAINT `chk_term_range` CHECK ((`min_term_months` <= `max_term_months`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES ('FD001','10001','Fixed Deposit - Standard','Standard FD','TERM_DEPOSIT','FD',NULL,NULL,'INR',6,60,5.000000,7.500000,'2026-10-13',NULL,'DRAFT','DRAFT','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','2026-10-13','I','SYSTEM','MIGRATION','seed-product','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','2026-10-12 18:45:43.000000','212950e0-c66d-11f1-964d-dc4546c9cc45',1000.00,10000000.00,18,'MONTHLY','MATURITY','MONTHLY,QUARTERLY,MATURITY','RENEW_PRINCIPAL',10.000000,40000.00),('FDLAB1','LAB1001','Fixed Deposit - Lab Test','Fixed Deposit - Lab Test','FD','FD',NULL,NULL,'INR',0,0,5.000000,8.500000,'2026-01-01',NULL,'ACTIVE','DRAFT','2026-10-12 13:16:20.518822','2026-10-12 13:16:21.239392','2026-10-13','U','demo-professor','API','product-service','2026-10-12 13:16:21.239392','2026-10-12 13:16:21.239392','2026-10-12 13:16:20.518822','2026-10-12 13:16:20.518822','13bbf2a2-d625-4c9e-8cbf-bfac9353e4af',1000.00,100000000.00,NULL,'QUARTERLY',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed
