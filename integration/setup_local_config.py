#!/usr/bin/env python3
"""
setup_local_config.py - creates YOUR private settings for this laptop.

Run once, from the project folder:
    python integration\\setup_local_config.py

It creates two files in the local-config folder (never share or commit them):

  local-config\\dev-secrets.properties
      - a new random JWT secret (shared by calculator-service, product-service
        and the seed script on THIS laptop)
      - the MySQL connection for product-service (database product_db_dev,
        user product_dev) with a new random password

  local-config\\create_product_db_dev.sql
      - the one-time MySQL Workbench script that creates the empty database
        product_db_dev and the restricted user product_dev with that password.
        Run it as root in MySQL Workbench, then you may delete it.

It never overwrites an existing dev-secrets.properties.
Options:
    --db-name NAME   use another database name (default product_db_dev)
    --port PORT      MySQL port (default 3306)
"""
import argparse
import os
import secrets
import string
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONFIG_DIR = os.path.join(ROOT, "local-config")
SECRETS_FILE = os.path.join(CONFIG_DIR, "dev-secrets.properties")
SQL_FILE = os.path.join(CONFIG_DIR, "create_product_db_dev.sql")

# Letters and digits only: safe inside .properties, cmd scripts, JDBC URLs and SQL
ALPHABET = string.ascii_letters + string.digits


def random_text(length):
    return "".join(secrets.choice(ALPHABET) for _ in range(length))


def main():
    parser = argparse.ArgumentParser(description="Create local-config\\dev-secrets.properties for this laptop.")
    parser.add_argument("--db-name", default="product_db_dev")
    parser.add_argument("--port", default="3306")
    args = parser.parse_args()

    if os.path.exists(SECRETS_FILE):
        print("local-config\\dev-secrets.properties already exists - nothing was changed.")
        print("Delete or rename it first if you really want new secrets.")
        return 1

    os.makedirs(CONFIG_DIR, exist_ok=True)
    jwt_secret = random_text(64)
    db_password = random_text(32)
    db_user = "product_dev"

    with open(SECRETS_FILE, "w", encoding="utf-8", newline="\n") as f:
        f.write(
            "# ============================================================\n"
            "#  LOCAL DEVELOPMENT SECRETS - DO NOT SHARE, DO NOT COMMIT\n"
            "#  Created by integration/setup_local_config.py for this laptop only.\n"
            "#  Read by: calculator-service, start-product-service.cmd,\n"
            "#           integration/seed_product_data.py\n"
            "# ============================================================\n\n"
            "# Shared JWT secret: calculator-service signs tokens, product-service checks them\n"
            f"calculator.jwt.secret={jwt_secret}\n\n"
            "# product-service MySQL connection (development database only)\n"
            f"product.db.url=jdbc:mysql://127.0.0.1:{args.port}/{args.db_name}"
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC\n"
            f"product.db.username={db_user}\n"
            f"product.db.password={db_password}\n")

    with open(SQL_FILE, "w", encoding="utf-8", newline="\n") as f:
        f.write(
            "-- ============================================================\n"
            "--  ONE-TIME SETUP (run as root in MySQL Workbench).\n"
            f"--  Creates ONLY a new empty database {args.db_name} and a new user {db_user}.\n"
            "--  Does not read, change or delete any other database.\n"
            "--  No \"IF NOT EXISTS\": if a name already exists MySQL stops with an error\n"
            "--  instead of reusing or overwriting anything.\n"
            "--  The tables are created later by product-service's Flyway migrations.\n"
            "--  Contains your password - delete this file after it has run.\n"
            "-- ============================================================\n"
            f"CREATE DATABASE {args.db_name} CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;\n"
            f"CREATE USER '{db_user}'@'localhost' IDENTIFIED BY '{db_password}';\n"
            f"GRANT ALL PRIVILEGES ON {args.db_name}.* TO '{db_user}'@'localhost';\n"
            f"SHOW GRANTS FOR '{db_user}'@'localhost';\n")

    print("Created (keep these private):")
    print("  local-config\\dev-secrets.properties")
    print("  local-config\\create_product_db_dev.sql")
    print()
    print("Next: open MySQL Workbench as root, File > Open SQL Script >")
    print("      local-config\\create_product_db_dev.sql, then click the lightning bolt.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
