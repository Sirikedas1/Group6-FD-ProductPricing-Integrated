# Group 6 — FD Calculator + Product & Pricing (Integrated)

**Banking Technology Lab (CSF 3142) · Group 6 · Windows setup guide**

This package contains three programs that work together:

| Program | What it does | Port |
|---|---|---|
| **product-service** (Product & Pricing) | Stores FD products, customer categories and the interest-rate matrix in **MySQL** | **8083** |
| **calculator-service** (FD Calculator backend) | Login (development stand-in), JWT security, FD maths, Python CSV reports. **No database of its own.** | **8082** |
| **banking-frontend** (Angular) | The website: login page and FD Calculator page | **4200** |

> **⚠ DEVELOPMENT STAGE — please read**
> - **FDLAB1 and its rates are DEVELOPMENT TEST DATA**, not confirmed banking rates.
> - The login uses **4 built-in demo users** (a "dev login stand-in"). **Group 10's real login service is NOT integrated yet** (see section 18).
> - No real customer data is included anywhere.

---

## Contents

0. [Check that your package is complete](#0-check-that-your-package-is-complete)
1. [Project overview and architecture](#1-project-overview-and-architecture)
2. [Required software](#2-required-software)
3. [Folder structure](#3-folder-structure)
4. [MySQL installation](#4-mysql-installation)
5. [Create your private settings, database and user](#5-create-your-private-settings-database-and-user)
6. [Flyway migrations (automatic)](#6-flyway-migrations-automatic)
7. [Load the development test data](#7-load-the-development-test-data)
8. [Local credentials and JWT configuration](#8-local-credentials-and-jwt-configuration)
9. [Start Product & Pricing (port 8083)](#9-start-product--pricing-port-8083)
10. [Start the FD Calculator backend (port 8082)](#10-start-the-fd-calculator-backend-port-8082)
11. [Start the Angular website (port 4200)](#11-start-the-angular-website-port-4200)
12. [Log in with the development accounts](#12-log-in-with-the-development-accounts)
13. [Test the calculator](#13-test-the-calculator)
14. [CSV reports](#14-csv-reports)
15. [Run the automatic (JUnit) tests](#15-run-the-automatic-junit-tests)
16. [Swagger and Postman](#16-swagger-and-postman)
17. [How the calculator talks to Product & Pricing](#17-how-the-calculator-talks-to-product--pricing)
18. [Later: switch to Group 10's real login](#18-later-switch-to-group-10s-real-login)
19. [Later: replace FDLAB1 with final rates](#19-later-replace-fdlab1-with-final-rates)
20. [Troubleshooting](#20-troubleshooting)
- [Appendix A — Database export and import](#appendix-a--database-export-and-import)
- [Appendix B — Daily start / stop cheat sheet](#appendix-b--daily-start--stop-cheat-sheet)

---

## 0. Check that your package is complete

Do this first, right after extracting (or cloning). It takes 10 seconds.

> **Got the project from GitHub?** Skip step a). Clone it with
> `git clone https://github.com/<owner>/Group6-FD-ProductPricing-Integrated.git`
> (or **Code → Download ZIP** on the GitHub page), open the folder in VS Code, and do step b).
> After a clone, the check may note that `calculator-service/mvnw.cmd` "differs only in line endings" — that is normal.

**a) Is it the right ZIP file?** In PowerShell, in the folder that contains the ZIP:

```
Get-FileHash .\Group6_FD_ProductPricing_Integrated_FIXED.zip -Algorithm SHA256
```

The `Hash` must equal the SHA-256 the sender gives you with the ZIP. If it differs, you received a different or damaged file — ask for it again (do not continue).

**b) Did every file arrive?** Open the extracted folder in VS Code, open a terminal (**Terminal → New Terminal**) and run:

```
python integration\check_package.py
```

It checks every file against `PACKAGE_MANIFEST.txt` (name + SHA-256 of each file). Expected end of the output:

```
  MISSING: 0
  CHANGED: 0
...
  calculator-service/src/main/java (*.java)                      35
  calculator-service/src/test/java (*.java)                      2
  product-service/src/main/java (*.java)                         65
  product-service/src/test/java (*.java)                         9
  product-service/src/main/resources/db/migration (*.sql)        9
  banking-frontend/src                                           22
  integration (*.py)                                             3
  database (*.sql)                                               3

RESULT: COMPLETE - every packaged file is present and unchanged.
```

(`calculator-service/src/test/java` has 2 files that contain 23 tests.) If it says **INCOMPLETE**, the copy you have is broken — get a fresh copy of the ZIP. Extract with Windows Explorer (**right-click → Extract All…**) or 7-Zip; do not open files directly inside the ZIP.

---

## 1. Project overview and architecture

```
  Browser
    │  http://localhost:4200
    ▼
  banking-frontend (Angular, port 4200)
    │  every /api/... call goes through the dev proxy (proxy.conf.json)
    ▼
  calculator-service (Spring Boot, port 8082)          ── no database
    │  • POST /api/auth/login  → dev login stand-in, issues a JWT token
    │  • /api/fd/**            → CUSTOMER role only (401 / 403 otherwise)
    │  • runs scripts/generate_fd_report.py for CSV reports
    │
    │  REST calls with the user's own token (Authorization: Bearer ...)
    ▼
  product-service (Spring Boot, port 8083)
    │  GET /products, /api/product-rate, /api/customer-category
    ▼
  MySQL 8 — database product_db_dev (user product_dev)
    tables created by product-service's own 9 Flyway migrations
```

**One calculation, step by step**

1. You log in as a customer → calculator-service returns a signed **JWT token**.
2. You choose the product **FDLAB1** and enter amount, term and interest type.
3. calculator-service asks product-service for FDLAB1's **base rate** and the **add-on** for each of your categories (from the rate matrix in MySQL).
4. It adds the add-ons, **caps the combined add-on at 2%**, applies the simple or compound formula with exact decimals (`BigDecimal`), and returns the maturity amount.

**Who owns what**

| Part | Owner |
|---|---|
| FD Calculator (calculator-service, Angular calculator page, CSV report) | Group 6 |
| Product & Pricing (product-service, its database schema and 9 migrations) | Group 6 teammates (Product & Pricing) — included **unchanged** |
| Real login / customer profiles | Group 10 — **pending** |

---

## 2. Required software

Install these once. Accept the installer defaults unless noted. **Restart VS Code after installing**, so it sees the new programs.

| Software | Version | Where | Check with |
|---|---|---|---|
| Java JDK | **17 or 21** (Temurin) | https://adoptium.net — on Windows tick "Set JAVA_HOME" and "Add to PATH" | `java -version` |
| Node.js | LTS (20.19+, 22 or 24) | https://nodejs.org | `node -v` and `npm -v` |
| Python | 3.12 or newer | https://www.python.org — tick **"Add python.exe to PATH"** | `python --version` |
| MySQL Server + MySQL Workbench | **8.0 or newer** | https://dev.mysql.com/downloads/installer/ | see section 4 |
| VS Code | latest | https://code.visualstudio.com | — |

You do **not** need to install Maven or the Angular CLI: the project brings its own Maven (`calculator-service\mvnw.cmd`) and uses the Angular CLI from `banking-frontend\node_modules`.

The **first** build downloads libraries from the internet (a few minutes). College Wi-Fi sometimes blocks this; a mobile hotspot usually works.

> product-service's own `pom.xml` asks for Java 21. Its code does not need Java 21, so the start script builds it with `-Djava.version=17`. This works on Java 17 **and** 21. All 17 of its tests pass this way.

---

## 3. Folder structure

```
Group6_FD_ProductPricing_Integrated/
├── README_INTEGRATION.md            ← this guide
├── PACKAGE_MANIFEST.txt             ← SHA-256 of every file (used by integration\check_package.py)
├── start-product-service.cmd        ← starts product-service (8083), MySQL by default
├── start-calculator-service.cmd     ← starts calculator-service (8082)
├── .gitignore                       ← keeps secrets / build output out of Git
├── local-config/
│   └── dev-secrets.properties.example   ← TEMPLATE only (no real secrets)
├── integration/
│   ├── check_package.py             ← confirms the extracted package is complete (section 0)
│   ├── setup_local_config.py        ← creates YOUR private settings + MySQL setup script
│   └── seed_product_data.py         ← loads FDLAB1 DEVELOPMENT TEST data (safe to rerun)
├── database/
│   ├── 01_create_database_and_user.TEMPLATE.sql   ← manual alternative to setup_local_config.py
│   ├── 02_verify_database.sql                     ← read-only checks
│   └── product_db_dev_schema_and_devdata.sql      ← optional full export (Appendix A)
├── docs/
│   └── FD_Calculator_Guide.md       ← the original step-by-step build guide (Labs L6, L10, L11, L14)
├── calculator-service/              ← FD Calculator backend (Spring Boot, Java)
│   ├── mvnw, mvnw.cmd, .mvn/        ← Maven wrapper (used for BOTH backends)
│   ├── pom.xml
│   ├── scripts/generate_fd_report.py ← Lab L11 Python CSV report
│   └── src/main, src/test           ← 35 Java files + 23 JUnit tests
├── product-service/                 ← Product & Pricing (teammates' module, unchanged*)
│   ├── pom.xml, README.md, PRODUCT_API_REFERENCE.md, EVAL_RUNBOOK.md, ...
│   ├── examples/product-service.postman_collection.json
│   ├── scripts/demo_token.py
│   └── src/main/resources/db/migration/V1__...sql … V9__...sql   ← the 9 Flyway migrations
└── banking-frontend/                ← Angular website
    ├── package.json, package-lock.json, angular.json, proxy.conf.json
    └── src/app/ (calculator/, auth/, home/)
```

\* The only change to product-service is in `EVAL_RUNBOOK.md`: a real evaluation password was replaced by `CHOOSE_YOUR_OWN_PASSWORD` before sharing. No code, configuration or migration was changed.

Folders that are **not** in the ZIP and are created automatically: `node_modules/` (by `npm install`), `target/` (by Maven), `calculator-service/reports/` (CSV reports), and your private `local-config/dev-secrets.properties`.

---

## 4. MySQL installation

If MySQL 8 and MySQL Workbench are already installed, skip to step 4.

1. Download the **MySQL Installer for Windows** from https://dev.mysql.com/downloads/installer/ (the larger "mysql-installer-community" file). If asked to log in, click **"No thanks, just start my download"**.
2. Run it and choose **Custom** (or **Developer Default**). Make sure **MySQL Server 8.x** and **MySQL Workbench 8.x** are selected.
3. Accept the defaults: **port 3306**, "Use Strong Password Encryption", **Configure MySQL Server as a Windows Service** ✔, **Start at System Startup** ✔. Choose a **root password** and write it down somewhere safe — you need it once in section 5.
4. Check that MySQL is running: press the **Windows key**, type `Services`, open it, find **MySQL80** — Status must be **Running** (if not: right-click → **Start**).
5. Open **MySQL Workbench** → under **MySQL Connections** click **Local instance MySQL80** → type the root password → **OK**. If a query tab opens, MySQL works.

**Optional safety check (read-only).** In the Workbench query tab, paste and run (lightning-bolt icon):

```sql
SELECT
  (SELECT COUNT(*) FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = 'product_db_dev') AS db_exists,
  (SELECT COUNT(*) FROM mysql.user WHERE user = 'product_dev') AS user_exists,
  VERSION() AS mysql_version, @@port AS port;
```

Both `db_exists` and `user_exists` should be **0**. If either is 1, stop and ask the team before continuing.

---

## 5. Create your private settings, database and user

Every teammate creates **their own** secrets. Nobody shares passwords.

### 5.1 Open the project

1. Extract the ZIP to a short path **without spaces**, for example `C:\Users\<you>\Documents\Group6_FD_ProductPricing_Integrated`. (From GitHub: `cd C:\Users\<you>\Documents` then `git clone https://github.com/<owner>/Group6-FD-ProductPricing-Integrated.git` — the folder is then called `Group6-FD-ProductPricing-Integrated`.)
2. In VS Code: **File → Open Folder…** → select that folder → **Select Folder** → if asked, **Yes, I trust the authors**.
3. Open a terminal: **Terminal → New Terminal**. The prompt ends with `...Group6_FD_ProductPricing_Integrated>`.

### 5.2 Create your private settings file

```
python integration\setup_local_config.py
```

Expected:

```
Created (keep these private):
  local-config\dev-secrets.properties
  local-config\create_product_db_dev.sql
```

- `dev-secrets.properties` holds a new random **JWT secret** and a new random **MySQL password** for user `product_dev`.
- `create_product_db_dev.sql` is the one-time MySQL script with that password already filled in.

### 5.3 Create the database and user (MySQL Workbench, ~2 minutes)

1. Open **MySQL Workbench** → click **Local instance MySQL80** → enter the **root** password.
2. Menu **File → Open SQL Script…** → go to your project folder → **local-config** → select **create_product_db_dev.sql** → **Open**.
3. Do not edit anything. Click the **lightning-bolt** icon (Execute).
4. The **Output** panel at the bottom should show **4 green ticks ✔**. The result grid shows:
   - `GRANT USAGE ON *.* TO product_dev@localhost` — can log in, nothing else
   - `GRANT ALL PRIVILEGES ON product_db_dev.* TO product_dev@localhost` — full access to its own database only
5. Close the script tab (**Don't Save** if asked). Then delete `local-config\create_product_db_dev.sql` (it contains the password; it is no longer needed).

The new database is **empty** — that is correct. The tables are created in the next step.

> **Manual alternative:** copy `local-config\dev-secrets.properties.example` to `local-config\dev-secrets.properties`, invent your own values (letters and digits only), and run `database\01_create_database_and_user.TEMPLATE.sql` in Workbench after replacing the placeholder password with the same value.

---

## 6. Flyway migrations (automatic)

You never run the migration SQL by hand. **Flyway** (a library inside product-service) does it:

- On start-up it connects to `product_db_dev`, looks at its table `flyway_schema_history`, and runs any of the 9 scripts in `product-service\src\main\resources\db\migration\` that have not run yet — in order V1 → V9.
- **First start** (empty database) → creates 12 tables and the teammates' seed data:
  - products: **FD001** "Fixed Deposit - Standard" (status **DRAFT**, no rates)
  - categories: GENERAL, SENIOR_CITIZEN, EMPLOYEE, MILITARY, SUPER_SENIOR
  - currencies: INR, USD, JPY, KWD
- **Every later start** → `Schema product_db_dev is up to date. No migration necessary.` Your data stays.

The 12 tables: `products`, `product_rate_matrix`, `customer_category`, `currency`, `product_fee_matrix`, `product_role`, `customer_communication`, `product_status_history`, `product_term_rule`, `product_business_rule`, `product_balance_type`, `product_txn_type` (+ `flyway_schema_history`).

**Never edit a migration file that has already run** — Flyway checks each file's checksum and refuses to start if one changed. New changes go into a new file (`V10__...sql`), written by the Product & Pricing team.

---

## 7. Load the development test data

> **DEVELOPMENT TEST DATA — not final banking rates.**

Start product-service once (section 9) so the tables exist. Then, in a **second** terminal:

```
python integration\seed_product_data.py
```

Expected:

```
Seeding development test data into http://localhost:8083
  created  category PREMIUM_USER
  created  product FDLAB1
  created  rate LAB1-GENERAL (GENERAL 6.50 + 0.00)
  created  rate LAB1-SENIOR (SENIOR_CITIZEN 6.50 + 1.00)
  created  rate LAB1-EMPLOYEE (EMPLOYEE 6.50 + 1.00)
  created  rate LAB1-PREMIUM (PREMIUM_USER 6.50 + 2.00)
  created  rate LAB1-SUPERSR (SUPER_SENIOR 6.50 + 1.50)
  updated  FDLAB1 DRAFT -> ACTIVE
Done. FDLAB1 is ready for the FD Calculator.
```

What it creates — **through product-service's own API**, never by editing tables directly:

| Item | Value (TEST) |
|---|---|
| Product **FDLAB1** "Fixed Deposit - Lab Test" | ACTIVE, ₹1,000 – ₹10 crore, MIN_RATE 5.00, **MAX_RATE 8.50** |
| Rate rows (0–120 months, from 2026-01-01) | base **6.50** + GENERAL 0.00 / SENIOR_CITIZEN 1.00 / EMPLOYEE 1.00 / PREMIUM_USER 2.00 / SUPER_SENIOR 1.50 |
| New category | PREMIUM_USER "Premium User" |
| Combined add-on cap | **2.00%** — this is a calculator setting (`calculator.rates.add-on-cap`), not a database value |

**Run it once.** Running it again is safe: everything is reported as `exists` and nothing is duplicated or overwritten. If an existing FDLAB1 rate has different values, it prints a **WARNING** and leaves it unchanged. **FD001 and the teammates' categories/currencies are never modified.**

**Verify in Workbench (read-only):** File → Open SQL Script → `database\02_verify_database.sql` → lightning bolt. Each result tab matches the comments in the file. Or: left panel **SCHEMAS** → refresh icon → `product_db_dev` → **Tables** → right-click `product_rate_matrix` → **Select Rows - Limit 1000**.

---

## 8. Local credentials and JWT configuration

All secrets live in **one private file**: `local-config\dev-secrets.properties`.

| Key | Used by | Meaning |
|---|---|---|
| `calculator.jwt.secret` | calculator-service **and** product-service **and** the seed script | Signs and checks login tokens. Must be identical for both services — that is why they read it from the same file. |
| `product.db.url` | product-service | `jdbc:mysql://127.0.0.1:3306/product_db_dev?...` |
| `product.db.username` / `product.db.password` | product-service | The restricted MySQL user from section 5 |

How each program reads it:

- **calculator-service**: `application.properties` contains `spring.config.import=optional:file:../local-config/dev-secrets.properties`. Values there override the **test-only placeholder** secret in `application.properties` (the placeholder only exists so the JUnit tests run on a fresh copy).
- **product-service**: `start-product-service.cmd` reads the file and passes the values as the environment variables product-service already supports (`JWT_SECRET`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`). Its own configuration files are unchanged.
- **seed script**: reads `calculator.jwt.secret` to create a short-lived ADMIN token with product-service's own `scripts\demo_token.py`.

Rules: never share this file, never commit it (`.gitignore` already excludes it), never paste its contents into chat. If you think a secret leaked: delete the file, run `python integration\setup_local_config.py` again, and in Workbench run `ALTER USER 'product_dev'@'localhost' IDENTIFIED BY '<new password from the file>';` (as root).

---

## 9. Start Product & Pricing (port 8083)

**Terminal 1** (in the project folder):

```
.\start-product-service.cmd
```

- **First time** it builds product-service (1–3 minutes, downloads libraries).
- Wait for: `Started ProductServiceApplication`.
- First start also shows `Successfully applied 9 migrations to schema product_db_dev`; later starts show `Schema product_db_dev is up to date`.
- Check: open http://localhost:8083/actuator/health → `{"status":"UP"}`.
- Leave this terminal open. **Stop** with `Ctrl + C` (answer `Y` if asked "Terminate batch job").

Other modes:

| Command | Use |
|---|---|
| `.\start-product-service.cmd rebuild` | after changing product-service code |
| `.\start-product-service.cmd demo` | **fallback without MySQL**: in-memory database, data lost on stop → run the seed script after each start |

---

## 10. Start the FD Calculator backend (port 8082)

**Terminal 2** — click the **+** icon at the top-right of the terminal panel:

```
.\start-calculator-service.cmd
```

- Wait for: `Started CalculatorServiceApplication`.
- Check: http://localhost:8082/actuator/health → `{"status":"UP"}`.
- Start **product-service first** — the calculator gets every rate from it.

---

## 11. Start the Angular website (port 4200)

**Terminal 3**. The first time only, install the website's libraries:

```
cd banking-frontend
npm install
```

(1–4 minutes; ends with `added ... packages`. Warnings are normal.)

Then, every time:

```
cd banking-frontend
npm start
```

Wait for `Local: http://localhost:4200/` and open **http://localhost:4200** in your browser.

Always use `npm start` (not `ng serve`): it switches on the proxy that forwards `/api/...` calls to port 8082.

---

## 12. Log in with the development accounts

These are **demo users of the dev login stand-in** (passwords stored as BCrypt hashes inside calculator-service). They are not real customers.

| Username | Password | Role | Categories (from "profile") | What it shows |
|---|---|---|---|---|
| `alice123` | `Alice@123` | CUSTOMER | Senior + Employee | +2% add-on → 8.50% |
| `ravi` | `Ravi@1234` | CUSTOMER | Employee + Premium User | 3% requested → **capped at 2%** |
| `bob` | `Bob@12345` | CUSTOMER | General | no add-on → 6.50% |
| `officer1` | `Officer@123` | BANK_OFFICER | – | **Access denied (403)** — calculator is CUSTOMER-only (Lab L10) |

Logged-in users stay logged in after a page refresh (token in the browser's localStorage) until it expires (10 hours) or they click **Log out**.

---

## 13. Test the calculator

1. Open http://localhost:4200 → **Log in** → `alice123` / `Alice@123`. You land on the FD Calculator; the top bar shows **alice123 CUSTOMER**.
2. **FD product**: choose **Fixed Deposit - Lab Test (FDLAB1)**. (The base-rate box is replaced by "The base rate comes from the selected product".)
3. Deposit `100000`, Term `12` **Months**, Interest type **Simple** → **Calculate**.

Expected results (DEVELOPMENT TEST rates):

| User | Inputs | Expected |
|---|---|---|
| alice123 | 100000, 12 months, Simple | **₹1,08,500.00**, rate 8.50%, Rate source `PRODUCT:FDLAB1` |
| ravi | same | **₹1,08,500.00** + yellow notice "add-ons total +3.00% … +2.00% was applied" |
| bob | same | **₹1,06,500.00**, rate 6.50% |
| bob | 100000, 12 months, Compound, Quarterly | **₹1,06,660.16**, APY 6.66% |
| alice123 | 50000, 24 months, Compound, Monthly | **₹59,229.74** |
| any | choose nothing as product | "Please choose an FD product." |
| officer1 | open /fd-calculator | sent to Home with "Access denied" |

Invalid inputs are rejected with clear messages (amount below ₹1,000, more than 2 decimals, term outside 7 days – 10 years, etc.).

---

## 14. CSV reports

After a calculation, click **Add to report (CSV)** → green box, e.g.
`Row 1 added to fd_report_alice123.csv: alice123,100000,12M,6.5,2.0,8.5,108500`
(exactly the Lab L11 format). Click **Download my report** to download it.

- Java runs `calculator-service\scripts\generate_fd_report.py` with Python for every row (Lab L11 "executed via Spring Boot").
- Files are saved in `calculator-service\reports\fd_report_<username>.csv` — one per user. They are generated output and are **not** shared in the ZIP.
- Columns: `Username,Principal,Term,BaseRate,CategoryAddOn,FinalRate,Maturity`.

---

## 15. Run the automatic (JUnit) tests

Neither test suite needs MySQL or running services.

**FD Calculator — 23 tests** (Lab L11):

```
cd calculator-service
.\mvnw.cmd test
cd ..
```

Expected: `Tests run: 23, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`.

**Product & Pricing — 17 tests** (they use their own in-memory H2 test database):

```
.\calculator-service\mvnw.cmd -f product-service\pom.xml -Djava.version=17 test
```

Expected: `Tests run: 17, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`.

**Website build check:**

```
cd banking-frontend
npx ng build --configuration development
cd ..
```

Expected: `Application bundle generation complete.`

---

## 16. Swagger and Postman

### FD Calculator Swagger — http://localhost:8082/swagger-ui.html

1. Open **Dev login stand-in → POST /api/auth/login → Try it out**, body `{"username":"alice123","password":"Alice@123"}` → **Execute**. Copy the `token` value (without quotes).
2. Click the green **Authorize** button, paste the token → **Authorize** → **Close**.
3. **POST /api/fd/calculate → Try it out**, body:
   ```json
   { "principal": 100000, "term": { "value": 12, "unit": "MONTHS" },
     "categories": [], "interestType": "SIMPLE", "productCode": "FDLAB1" }
   ```
   → **Execute** → `200`, `"maturityAmount": 108500.00`, `"rateSource": "PRODUCT:FDLAB1"`.
4. Without a token → `401`; with `officer1`'s token → `403`.

### Product & Pricing Swagger — http://localhost:8083/swagger-ui.html

Reading works with any logged-in user's token (e.g. alice123's from above). **Creating or changing** products/rates needs an **ADMIN** or **BANK_OFFICER** token. Make a development ADMIN token (valid 4 hours) in PowerShell, from the project folder:

```
$env:JWT_SECRET = ((Get-Content local-config\dev-secrets.properties | Select-String '^calculator.jwt.secret=').Line -split '=',2)[1]
python product-service\scripts\demo_token.py ADMIN
Remove-Item Env:JWT_SECRET
```

Copy the printed token into **Authorize**. Useful calls: `GET /products`, `GET /api/product-rate/product/FDLAB1`, `GET /products/FDLAB1/pricing?CATEGORY_CODE=SENIOR_CITIZEN&AMOUNT=100000&TENURE_MONTHS=12`.

### Postman

**Import** `product-service\examples\product-service.postman_collection.json`. In the collection's **Variables** set `baseUrl` = `http://localhost:8083` and `token` = the ADMIN token above. The collection creates its own sample product `FDDEMO1` — this adds data to your development database (that is fine; it is a dev database).

---

## 17. How the calculator talks to Product & Pricing

Settings in `calculator-service\src\main\resources\application.properties`:

```
calculator.rates.source=PRODUCT                 # LOCAL = built-in table, PRODUCT = product-service
calculator.product.base-url=http://localhost:8083
calculator.product.products-path=/products
calculator.product.rates-path=/api/product-rate
calculator.product.categories-path=/api/customer-category
calculator.product.timeout-ms=3000
calculator.rates.add-on-cap=2.00
```

- `GET /products` → the **FD product** dropdown shows only **ACTIVE** products of type FD (so FD001, still DRAFT, is hidden).
- `GET /api/product-rate` → rows of the chosen product whose date window, tenure band (in whole months) and amount band fit the deposit. The **GENERAL** row gives the base rate; each of the customer's categories gives its `ADDITIONAL_RATE_PERCENT`.
- The calculator sums the add-ons, caps the total at 2%, and also caps the final rate at the product's `MAX_RATE`.
- Category names are matched across modules: product-service's `SENIOR_CITIZEN` = calculator's `SENIOR`.
- Every call forwards the logged-in user's own token; product-service allows any logged-in user to **read**.
- If product-service is down, the calculator answers **503 "Product service is not reachable…"** after 3 seconds. It never invents a rate.
- The calculator stores nothing — no database. **Back to the built-in rate table** (no product-service needed): set `calculator.rates.source=LOCAL` and restart the calculator; the page then shows a base-rate box instead of the product dropdown.

---

## 18. Later: switch to Group 10's real login

> **Status: PENDING.** The package uses the dev login stand-in.

When Group 10's Login/Customer service runs (planned port **8080**), agree these with Group 10 first:

1. **One shared JWT secret** for Group 10, calculator-service **and** product-service (all three check the same tokens). Put it in everyone's `local-config\dev-secrets.properties` as `calculator.jwt.secret`. If Group 10's secret is Base64 text, also set `calculator.jwt.secret-base64=true` in `application.properties`.
2. Tokens must carry `sub` = username and `role` = `CUSTOMER` (`ROLE_CUSTOMER` or a `roles` list also work).
3. `GET /api/customer/profile` must return the customer's category in one of: `custCategoryCode`, `categoryCode`, `classification`, `customerClassification`.
4. **Ports**: Group 10 must not use **8082** or **8083** (taken by the calculator and product-service). Note: the seniors' Credexa customer-service used 8082.

Then change in `calculator-service\src\main\resources\application.properties`:

| Setting | New value |
|---|---|
| `calculator.dev-auth.enabled` | `false` |
| `calculator.customer.source` | `REMOTE` |
| `calculator.customer.base-url` | `http://localhost:8080` |

and in `banking-frontend\proxy.conf.json`, inside `"/api/auth"`, change `"target"` to `"http://localhost:8080"`. Restart the calculator **and** `npm start`. Full background: `docs\FD_Calculator_Guide.md`, Step 16.

---

## 19. Later: replace FDLAB1 with final rates

The calculator needs **no code change** — it shows whatever ACTIVE FD products product-service has.

1. The Product & Pricing team confirms the real values (base rate, add-on per category, MAX_RATE, tenure/amount bands).
2. With an ADMIN or BANK_OFFICER token (section 16), in product-service Swagger or Postman:
   1. `POST /products` — the real product (starts as DRAFT).
   2. `POST /api/product-rate` — one row per category (`BASE_RATE` + `ADDITIONAL_RATE_PERCENT`). product-service rejects rows whose base + add-on exceeds the product's `MAX_RATE`, and overlapping rows.
   3. `PATCH /products/{CODE}/status?STATUS=ACTIVE&REMARKS=Approved`.
3. Retire the test product: `PATCH /products/FDLAB1/status?STATUS=WITHDRAWN&REMARKS=Replaced by final rates`. It disappears from the dropdown. (Only DRAFT products can be deleted; withdrawing keeps the history.)
4. If the **combined add-on cap** changes, edit `calculator.rates.add-on-cap` and restart the calculator.
5. If a new category is introduced, add it with `POST /api/customer-category` and give it rate rows.

To make the final data reproducible for everyone, the Product & Pricing team can add it as a new Flyway migration (`V10__...sql`) — never edit V1–V9.

---

## 20. Troubleshooting

| Problem | Cause | Fix |
|---|---|---|
| `local-config\dev-secrets.properties not found` | Private settings not created | `python integration\setup_local_config.py` (section 5) |
| product-service: `Access denied for user 'product_dev'@'localhost'` | Password in `dev-secrets.properties` ≠ MySQL user's password | As root in Workbench: `ALTER USER 'product_dev'@'localhost' IDENTIFIED BY '<password from the file>';` |
| product-service: `Communications link failure` | MySQL not running / wrong port | Services → **MySQL80** → Start. Check the port in `product.db.url`. |
| product-service: `Unknown database 'product_db_dev'` | Section 5.3 not done | Run `create_product_db_dev.sql` in Workbench |
| product-service: `Validate failed: Migrations have failed validation` / checksum mismatch | A V1–V9 file was edited, or a different dump was imported | Restore the original migration files; use one database set-up method only (Appendix A) |
| `'java' is not recognized` / `JAVA_HOME not found` | Java not installed or not on PATH | Install Temurin 17/21 with "Set JAVA_HOME"; restart VS Code |
| `'python' is not recognized` or opens the Microsoft Store | Python not on PATH | Rerun the Python installer → Modify → tick "Add Python to environment variables" |
| `Port 8083/8082/4200 was already in use` | That service is already running | Use the running one, or stop it (`Ctrl + C` in its terminal) |
| Calculator page: "Product service is not reachable at http://localhost:8083" | product-service not running | Start it (section 9) |
| Calculator page: "Product service answered HTTP 401" | Different JWT secrets | Both services must read the same `local-config\dev-secrets.properties`; restart both |
| No products in the dropdown | FDLAB1 not loaded / not ACTIVE | `python integration\seed_product_data.py` |
| "Product FD001 is not active (status DRAFT)" | FD001 is the teammates' sample, intentionally DRAFT | Use FDLAB1 |
| "Cannot reach the FD Calculator service. Is the backend running on port 8082?" | calculator-service stopped, or website started with `ng serve` | Start it (section 10); use `npm start` |
| Website asks to log in again | Token expired or secret changed | Log in again — normal |
| "Could not start Python using the command 'python'" (Add to report) | Python missing | Install Python with "Add to PATH"; restart the calculator |
| `npm start`: `'ng' is not recognized` | `npm install` not run | `cd banking-frontend` → `npm install` |
| Windows: `running scripts is disabled on this system` | PowerShell policy | `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned` → `Y` |
| Maven `Could not transfer artifact` | Network blocks downloads | Try a mobile hotspot; run again |
| Seed script: `product-service is not running at http://localhost:8083` | Started too early | Start product-service and wait for `Started ProductServiceApplication` |

**Reading an error:** read the **first** red line, look for a file name and line number, and for website problems press `F12` → **Console** / **Network**.

---

## Appendix A — Database export and import

There are **two ways** to fill `product_db_dev`. **Use one, never both on the same database.**

### Option A (recommended): migrations + seed script

Sections 5 → 9 → 7: empty database → product-service runs the 9 migrations → `seed_product_data.py` adds FDLAB1. Everyone gets identical data from the original source files.

### Option B: import the included export

`database\product_db_dev_schema_and_devdata.sql` is a `mysqldump` of a working `product_db_dev` (DEVELOPMENT TEST DATA only). It contains the 12 tables, **`flyway_schema_history` at version 9**, FD001, the categories, currencies, FDLAB1 and its 5 rates. It contains **no** MySQL users, passwords or customer data, and **no** `CREATE DATABASE` line.

1. Do section 5 (settings + **empty** database + user). **Do not** start product-service yet.
2. Workbench (as root): **Server → Data Import** → **Import from Self-Contained File** → select `database\product_db_dev_schema_and_devdata.sql` → **Default Target Schema**: `product_db_dev` → **Import Progress** tab → **Start Import**.
   Command-line alternative: `"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p product_db_dev < database\product_db_dev_schema_and_devdata.sql`
3. Start product-service: it reports `Schema product_db_dev is up to date. No migration necessary.` — Flyway sees version 9 in the imported history, so **it does not rerun any migration**.
4. Do **not** run the seed script (it would only report `exists` — harmless, but not needed).

**Never** import the export into a database that already has tables (for example after Option A) — the `DROP TABLE IF EXISTS` lines in the file would replace them.

### Making a new export of your own database (to share development data)

Workbench (as root): **Server → Data Export** → tick **only `product_db_dev`** (never `mysql` or `sys`) → **Dump Structure and Data** → **Export to Self-Contained File** (choose a path) → leave **Include Create Schema** unticked (so the importer chooses the database) → **Start Export**.
Command-line alternative (asks for the password):
`"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe" -u product_dev -p --no-tablespaces --single-transaction --set-gtid-purged=OFF product_db_dev > my_export.sql`

Label shared exports **"development test data"** and check they contain no real customer information.

---

## Appendix B — Daily start / stop cheat sheet

| Order | Terminal | Command | Wait for |
|---|---|---|---|
| 1 | Terminal 1 | `.\start-product-service.cmd` | `Started ProductServiceApplication` |
| 2 | Terminal 2 | `.\start-calculator-service.cmd` | `Started CalculatorServiceApplication` |
| 3 | Terminal 3 | `cd banking-frontend` then `npm start` | `Local: http://localhost:4200/` |
| 4 | Browser | http://localhost:4200 | login page / home |

Stop: `Ctrl + C` in each terminal. MySQL keeps running in the background as a Windows service — that is normal.
No seed script is needed on normal days — the data is saved in MySQL.

| Address | What |
|---|---|
| http://localhost:4200 | The website |
| http://localhost:8082/swagger-ui.html | FD Calculator API test page |
| http://localhost:8083/swagger-ui.html | Product & Pricing API test page |
| http://localhost:8082/actuator/health · http://localhost:8083/actuator/health | Health checks |
