#!/usr/bin/env python3
"""
seed_product_data.py - loads FD Calculator test data into product-service.

DEVELOPMENT TEST VALUES ONLY - not confirmed production rates.
Confirm the real rates with the Product & Pricing team (Lavanya / Ananya).

Everything is created through product-service's own REST API (no database
access, no changes to their code), using an ADMIN token made by their own
scripts/demo_token.py with the shared JWT secret.

Creates (skips anything that already exists, so it is safe to run twice):
  - customer category PREMIUM_USER (missing from their seed data)
  - product FDLAB1 (MAX_RATE 8.50) - the seeded FD001 is NOT touched
  - one rate-matrix row per category, 0-120 months, base 6.50:
        GENERAL +0.00, SENIOR_CITIZEN +1.00, EMPLOYEE +1.00,
        PREMIUM_USER +2.00, SUPER_SENIOR +1.50
  - moves FDLAB1 from DRAFT to ACTIVE

With MySQL (normal mode) run it ONCE - the data is saved in product_db_dev.
Running it again is safe: existing records are reported as "exists" and are
never overwritten; an existing FDLAB1 rate with different values is reported
as a WARNING so you can check it by hand.
With the demo database (start-product-service.cmd demo) run it after every start:
    python integration\\seed_product_data.py
"""
import json
import os
import subprocess
import sys
import time
import urllib.error
import urllib.request

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BASE_URL = os.environ.get("PRODUCT_SERVICE_URL", "http://localhost:8083")
# Local, untracked secrets file (same JWT secret as calculator-service and product-service)
SECRETS = os.path.join(ROOT, "local-config", "dev-secrets.properties")
TOKEN_SCRIPT = os.path.join(ROOT, "product-service", "scripts", "demo_token.py")

PRODUCT_CODE = "FDLAB1"
PRODUCT = {
    "PRODUCT_CODE": PRODUCT_CODE,
    "PRODUCT_ID": "LAB1001",
    "PRODUCT_NAME": "Fixed Deposit - Lab Test",
    "PRODUCT_TYPE": "FD",
    "CURRENCY": "INR",
    "MIN_AMOUNT": 1000.00,
    "MAX_AMOUNT": 100000000.00,
    "MIN_RATE": 5.00,
    "MAX_RATE": 8.50,
    "DEFAULT_COMPOUNDING_FREQUENCY": "QUARTERLY",
    "EFFECTIVE_DATE": "2026-01-01",
    "STATUS": "DRAFT",
}
BASE_RATE = 6.50
# (RATE_ID, CATEGORY_CODE, ADDITIONAL_RATE_PERCENT) - development test values
RATES = [
    ("LAB1-GENERAL", "GENERAL", 0.00),
    ("LAB1-SENIOR", "SENIOR_CITIZEN", 1.00),
    ("LAB1-EMPLOYEE", "EMPLOYEE", 1.00),
    ("LAB1-PREMIUM", "PREMIUM_USER", 2.00),
    ("LAB1-SUPERSR", "SUPER_SENIOR", 1.50),
]


def jwt_secret():
    if not os.path.exists(SECRETS):
        raise SystemExit("Secrets file not found: " + SECRETS)
    with open(SECRETS, encoding="utf-8") as f:
        for line in f:
            if line.startswith("calculator.jwt.secret="):
                return line.split("=", 1)[1].strip()
    raise SystemExit("calculator.jwt.secret not found in " + SECRETS)


def admin_token(secret):
    env = dict(os.environ, JWT_SECRET=secret)
    result = subprocess.run([sys.executable, "-I", TOKEN_SCRIPT, "ADMIN"],
                            env=env, capture_output=True, text=True, check=True)
    return result.stdout.strip()


def call(method, path, token, body=None):
    data = None if body is None else json.dumps(body).encode("utf-8")
    req = urllib.request.Request(BASE_URL + path, data=data, method=method)
    req.add_header("Authorization", "Bearer " + token)
    if data is not None:
        req.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(req, timeout=10) as res:
            text = res.read().decode("utf-8")
            return res.status, (json.loads(text) if text else None)
    except urllib.error.HTTPError as e:
        text = e.read().decode("utf-8")
        try:
            return e.code, json.loads(text)
        except ValueError:
            return e.code, text


def wait_until_up():
    for _ in range(60):
        try:
            with urllib.request.urlopen(BASE_URL + "/actuator/health", timeout=3) as res:
                if res.status == 200:
                    return
        except (urllib.error.URLError, OSError):
            pass
        time.sleep(2)
    raise SystemExit("product-service is not running at " + BASE_URL + ". Start it with start-product-service.cmd")


def ensure(label, get_path, post_path, body, token):
    status, _ = call("GET", get_path, token)
    if status == 200:
        print(f"  exists   {label}")
        return
    status, res = call("POST", post_path, token, body)
    if status != 201:
        raise SystemExit(f"  FAILED   {label}: HTTP {status} {res}")
    print(f"  created  {label}")


def main():
    wait_until_up()
    token = admin_token(jwt_secret())
    print(f"Seeding development test data into {BASE_URL}")

    ensure("category PREMIUM_USER", "/api/customer-category/PREMIUM_USER", "/api/customer-category",
           {"CATEGORY_CODE": "PREMIUM_USER", "CATEGORY_NAME": "Premium User",
            "DESCRIPTION": "Premium customer (FD Calculator lab test category)"}, token)

    ensure(f"product {PRODUCT_CODE}", f"/products/{PRODUCT_CODE}", "/products", PRODUCT, token)

    warnings = 0
    for rate_id, category, addon in RATES:
        ensure(f"rate {rate_id} ({category} {BASE_RATE:.2f} + {addon:.2f})",
               f"/api/product-rate/{rate_id}", "/api/product-rate",
               {"RATE_ID": rate_id, "PRODUCT_CODE": PRODUCT_CODE, "CATEGORY_CODE": category,
                "MIN_TENURE_MONTHS": 0, "MAX_TENURE_MONTHS": 120,
                "BASE_RATE": BASE_RATE, "ADDITIONAL_RATE_PERCENT": addon,
                "EFFECTIVE_DATE": "2026-01-01"}, token)
        # Existing rows are never overwritten - only reported if they differ from the test values
        _, row = call("GET", f"/api/product-rate/{rate_id}", token)
        if (row.get("CATEGORY_CODE") != category
                or abs(float(row.get("BASE_RATE", -1)) - BASE_RATE) > 1e-9
                or abs(float(row.get("ADDITIONAL_RATE_PERCENT") or 0) - addon) > 1e-9):
            warnings += 1
            print(f"  WARNING  {rate_id} differs from the test values: {row.get('CATEGORY_CODE')} "
                  f"{row.get('BASE_RATE')} + {row.get('ADDITIONAL_RATE_PERCENT')} (left unchanged)")

    status, product = call("GET", f"/products/{PRODUCT_CODE}", token)
    if product.get("STATUS") == "ACTIVE":
        print(f"  exists   {PRODUCT_CODE} is ACTIVE")
    else:
        status, res = call("PATCH", f"/products/{PRODUCT_CODE}/status?STATUS=ACTIVE&REMARKS=FD%20Calculator%20lab%20test",
                           token)
        if status != 200:
            raise SystemExit(f"  FAILED   activate {PRODUCT_CODE}: HTTP {status} {res}")
        print(f"  updated  {PRODUCT_CODE} DRAFT -> ACTIVE")
    if warnings:
        print(f"Done with {warnings} warning(s): check the rows above.")
    else:
        print("Done. FDLAB1 is ready for the FD Calculator.")


if __name__ == "__main__":
    main()
