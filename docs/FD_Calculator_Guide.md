# FD Calculator Module — Complete Step-by-Step Build Guide

**Banking Technology Lab (CSF 3142) · Group 6 · Labs L6, L10, L11, L14**

This one document takes you from an empty computer to a fully working FD Calculator module: backend, frontend, login integration, Python report, tests and demo. Follow it top to bottom. Every file is given in full, and nothing is skipped.

---

## Table of contents

- [Part 0 — Read this first](#part-0--read-this-first)
- [Part 1 — What we are building and why](#part-1--what-we-are-building-and-why)
- [Part 2 — The seniors' Credexa ZIP: what it is, and what to take from it](#part-2--the-seniors-credexa-zip-what-it-is-and-what-to-take-from-it)
- [Part 3 — Setup (Step 1)](#part-3--setup)
- [Phase A — Standalone FD Calculator, Lab L6 (Steps 2–10)](#phase-a--standalone-fd-calculator-lab-l6)
- [Phase B — Testing and Python report, Lab L11 (Steps 11–13)](#phase-b--testing-and-python-report-lab-l11)
- [Phase C — Login integration and product rates, Labs L10 and L14 (Steps 14–17)](#phase-c--login-integration-and-product-rates-labs-l10-and-l14)
- [Step 18 — Final testing checklist](#step-18--final-testing-checklist)
- [Step 19 — Troubleshooting](#step-19--troubleshooting)
- [Appendix A — What every file does](#appendix-a--what-every-file-does)
- [Appendix B — Viva preparation](#appendix-b--viva-preparation)
- [Appendix C — Saving your work with Git (optional)](#appendix-c--saving-your-work-with-git-optional)

---

## Part 0 — Read this first

### How the instructions are written

Every time you must create or change a file, you'll see a block like this:

> **File:** `calculator-service/src/main/java/com/banklab/calculator/model/TermUnit.java`
> **Action:** Create this file

Then the complete code follows.
- **"Create this file"** means the file doesn't exist yet. Make it and paste the code.
- **"Replace the entire file"** means the file exists. Delete everything in it, then paste the new code.
- All paths start from your workspace folder `banking-lab`.

This guide never asks you to insert a snippet into the middle of a file. When a file changes, you always get the whole new file. That's the safest approach.

### Words you will meet

| Word | Meaning in plain language |
|---|---|
| **Frontend** | The screens you see in the browser. Ours is built with **Angular**. |
| **Backend** | A program running in the background that does the real work (validation, maths). Ours is built with **Spring Boot** (Java). |
| **API** | A named "door" on the backend, such as `/api/fd/calculate`. The frontend sends data through the door and gets an answer back. |
| **JSON** | The text format the frontend and backend use to talk, e.g. `{"principal": 100000}`. |
| **localhost** | "This same computer". |
| **Port** | A numbered door on your computer, so several programs can run at once. Angular uses **4200**, our backend uses **8082**. |
| **Terminal** | A window where you type commands instead of clicking. |
| **Folder the terminal is "in"** | The terminal always works inside one folder, shown at the start of the line. Commands act on that folder. |
| **Server running** | A program that keeps running and waiting for requests. It keeps the terminal busy until you stop it with `Ctrl + C`. |

### How to do common things in VS Code

**Open the workspace:**
1. Start VS Code.
2. Click **File → Open Folder…** (Mac: **File → Open…**).
3. Choose `Documents/banking-lab` and click **Select Folder** / **Open**.

**Create a folder:**
1. In the left panel (the **Explorer**), right-click the folder where the new folder should go.
2. Choose **New Folder…**, type the name, and press Enter.

**Create a file:**
1. Right-click the folder where the file should go and choose **New File…**.
2. Type the exact file name, including the extension (for example `TermUnit.java`), and press Enter.
3. The empty file opens on the right.

**Paste code:**
1. Click inside the file.
2. Press `Ctrl + A` (Mac: `Cmd + A`) to select everything.
3. Press `Ctrl + V` (Mac: `Cmd + V`) to paste. Pasting over a selection replaces it.

**Save:** press `Ctrl + S` (Mac: `Cmd + S`). An unsaved file shows a white dot on its tab. **Always save after pasting.** Most "it doesn't work" problems are unsaved files.

**Open a terminal:** click **Terminal → New Terminal**. It opens at the bottom, inside the `banking-lab` folder.

**Open a second terminal:** click the **+** icon at the top-right of the terminal panel. Switch between terminals with the list on the right of the panel.

**Change folder in the terminal:** type `cd` followed by the folder name, e.g. `cd calculator-service`. To go back up one level, type `cd ..`.

**Stop a running server:** click inside its terminal and press `Ctrl + C`. On Windows, if asked "Terminate batch job (Y/N)?", type `Y` and press Enter.

**Copy code from this document:** in most viewers, hover over a code block and click the copy icon in its top-right corner. Otherwise select the text with the mouse and press `Ctrl + C`.

### Windows vs Mac commands

Commands that differ are shown twice:

```
Windows:  .\mvnw.cmd spring-boot:run
Mac:      ./mvnw spring-boot:run
```

Type only the line for your system, without the `Windows:` / `Mac:` label.


---

## Part 1 — What we are building and why

### 1.1 What is in your Project

Your Project contains **design documents, not code**. That's normal at this stage, and it's why we build from scratch while following the architecture your documents define:

| File | What it is | How we use it |
|---|---|---|
| `Banking-Technology-Lab-Manual.docx` | The lab manual | Source of all requirements (L6, L10, L11, L14) |
| `productpricingservice__1_.yaml` | API spec of your team's product-service | Rate matrix and categories structure (Phase C) |
| `cust_auth_swagger_api.yaml` | API spec of Group 10's Login + Customer module | Login request/response shape, `custCategoryCode` |
| `fd_module_openapi_fin_final.yaml` | API spec of the FD Account module team | Confirms the calculator is **stateless** and uses `/api/fd/calculate` |
| `product_pricing_erd_v6_ALLCAPS.png` | Your team's database diagram | `CUSTOMER_CATEGORY`, `PRODUCT_RATE_MATRIX`, `PRODUCTS` |
| `Cust_Auth_ERD_Final.png`, `fd_module_erd_g3_corrected.png` | Other teams' diagrams | Context for integration |
| `Group_6_wbs.xlsx` | **Your team's** work plan | calculator-service on **port 8082**, BigDecimal, APY, Swagger, `calc-` prefix |
| `Group_10_WBS.xlsx`, `Group_2_Work_Breakdown_Structure.xlsx` | Other teams' plans | Who owns login (Group 10) and FD accounts (Group 2) |

You also uploaded a ZIP of the seniors' **Credexa customer-service**. It is not their calculator, and you cannot run it, but it is a useful preview of the Customer module your calculator talks to in Phase C. [Part 2](#part-2--the-seniors-credexa-zip-what-it-is-and-what-to-take-from-it) covers it in full.

**Technology stack** (manual section 3): Angular frontend, Java Spring Boot backend, MySQL database, Python reports, JWT + BCrypt security.

### 1.2 What the manual requires

**Explicitly required by the manual:**

| Lab | Requirement | Where we build it |
|---|---|---|
| L6 §2 | Simple interest **M = P + (P × r × t / 100)** | `FdCalculatorService.simpleMaturity` |
| L6 §2 | Compound interest **M = P × (1 + r/n)^(nt)** | `FdCalculatorService.compoundMaturity` |
| L6 §2 | Rate varies by customer category (Senior Citizen +1%); combined add-on capped (e.g. 2%) | `LocalRateProvider` + cap logic |
| L6 §3 | `POST /api/fd/calculate` with `principal`, `term{value,unit}`, `baseRate`, `categories` → `maturityAmount`, `effectiveRate`, `interestEarned` | `FdCalculatorController` |
| L6 §4 | UI: category dropdown fetched from the rate matrix, add-on % shown per category, cap enforced before submission | Calculator page |
| L6 §5, L11 §3 | Python report: `Username,Principal,Term,BaseRate,CategoryAddOn,FinalRate,Maturity`, run from Java | `generate_fd_report.py` + `FdReportService` |
| L10 | Only logged-in users; CUSTOMER role only (others get 403); categories come from the logged-in customer | Phase C security + `CategorySelectionService` |
| L11 | Test scenario table; cap enforcement; product-defined maximum rate | JUnit tests + `ProductRateProvider` |
| L14 | Demo: "FD Calculator, login page with FD Calculator integrated" | Phase C |

**Inferred from your project documents:** a separate `calculator-service` on port **8082**; BigDecimal maths; compounding frequency choice (WBS B2.2); term in days/months/years (WBS B1.2); APY and maturity date (WBS B1.1/B4.1); no database of its own (FD spec says stateless).

**Optional improvements added:**
- A built-in rate table (WBS A3.2 "stub product data provider"), so everything works before product-service exists.
- A `capApplied` flag.
- Swagger UI for testing.
- A temporary "dev login stand-in" so you can demo login before Group 10's service exists.
- Accepting both `/api/fd/calculate` (manual) and `/api/calculator/fd` (your WBS).

### 1.3 Architecture

```
 Browser  ──►  Angular app (port 4200)
                 │  all calls to /api/...
                 ▼  (dev proxy forwards them)
            calculator-service (Spring Boot, port 8082)
                 │  Phase C only:
                 ├──► Group 10 login/customer service (port 8080)  [or built-in dev login]
                 └──► product-service of your teammates (port 8081) [or built-in rate table]
```

**The flow in plain language (Phase A):**
1. You open the FD Calculator page. It asks the backend for the category list and shows each category's bonus %.
2. You fill in the amount, term, base rate, categories and interest type. The page previews the cap as you go.
3. You click **Calculate**. The page checks your input, then sends it as JSON to `POST /api/fd/calculate`.
4. The backend checks the input again, adds up the category bonuses, caps them at 2%, and applies the formula with exact decimals.
5. It returns the result, and the page displays the maturity amount, interest, effective rate, cap notice and maturity date.

**Phase C adds:** Login page → token saved in the browser → every request carries the token → the backend allows only CUSTOMER users and uses that customer's own categories.

### 1.4 Database decision: no database change is needed

The calculator does **not** get a database table, for these reasons:
1. The manual defines tables for users and FD accounts, but none for the calculator (L6, L10 and L11 have no table).
2. A calculation is pure maths on the inputs.
3. The FD team's spec states the calculator has "no persistence, no fd_db table".
4. The L11 "report" is a **CSV file written by Python**, not a table.
5. Rates belong to your teammates' `PRODUCT_RATE_MATRIX`. Copying them into a second database would create two sources of truth.

So you don't need to install or configure MySQL for this module.

### 1.5 The formulas

**Effective rate** = base rate + category add-ons, with the add-ons capped at 2%.
Example: 6.5% + (Senior 1% + Employee 1%) = 8.5%.

**Simple interest:** interest is earned only on the original amount.
- **Interest = P × R × t ÷ 100**
  - P is the principal (the amount deposited).
  - R is the effective rate per year (e.g. 8.5).
  - t is the time in years (12 months = 1; 18 months = 1.5; 400 days = 400/365).
- **Maturity = P + Interest.**

**Compound interest:** interest is added to the balance every period and then earns interest itself.
- **Maturity = P × (1 + r/n)^(n × t)**
  - r is the rate as a decimal (8.5% → 0.085).
  - n is the number of compounding periods per year: monthly 12, quarterly 4, half-yearly 2, annually 1.
- **Interest = Maturity − P.**
- With n = 12, this is exactly the L14 formula `P × (1 + Rate/(12×100))^TermMonths`.

**Leftover part-periods:** with quarterly compounding, 5 months = 1 full quarter + ⅔ of a quarter. The full quarter compounds, and the leftover earns simple interest on the grown amount (usual Indian bank practice).

**APY (annual yield)** = (1 + r/n)^n − 1. This is what a compound rate is "really worth" per year.

**Rounding:** 2 decimal places (paise), half-up.

### 1.6 Sample calculations (your answer key)

Every value was computed exactly and is checked automatically by the tests in Step 11.

| # | Input | Expected output |
|---|---|---|
| 1 | ₹100000, 12 months, base 6.5, **Senior + Employee**, Simple | Rate 8.50%, interest **8,500.00**, maturity **1,08,500.00** (matches the manual's L11 CSV row) |
| 2 | ₹100000, 12 months, base 6.5, **Employee + Premium User**, Simple | Add-on 3.00 → **capped to 2.00**, rate 8.50%, maturity **1,08,500.00** |
| 3 | ₹100000, 12 months, base 6.5, no category, Compound **Quarterly** | Interest **6,660.16**, maturity **1,06,660.16**, APY **6.66%** |
| 4 | ₹50000, 24 months, base 7.0, **Senior**, Compound **Monthly** | Rate 8.00%, interest **8,644.40**, maturity **58,644.40** |
| 5 | ₹100000, 1 month, base 6.6, no category, Compound **Monthly** | Interest **550.00**, maturity **1,00,550.00** (matches the manual's L14 example) |
| 6 | ₹25000, 18 months, base 7.25, no category, Simple | Interest **2,718.75**, maturity **27,718.75** |
| 7 | ₹100000, 5 months, base 7.0, no category, Compound **Quarterly** | Interest **2,937.08**, maturity **1,02,937.08** (leftover-period rule) |

### 1.7 Mismatches found in the documents (know these for your viva)

1. **The manual's L6 sample output contradicts itself.**
   - It shows Senior + Employee on 6.5% giving 7.0% and ₹107000.
   - But its own rule (Senior +1%) and its L11 table give 8.5% and ₹108500.
   - We follow L11.
2. **Category add-on values are only partly given.**
   - Only Senior = +1% is stated.
   - We use Employee +1% and Premium User +2%, which reproduces both L11 rows exactly.
   - In L11 row 1, the 2.0% add-on equals the cap exactly. We report `capApplied: false` when nothing was cut off, and the final 8.5% still matches.
3. **Super Senior has no rate in the manual.** Your team's WBS seeds a "Super Senior" category, and the Customer module will send it, but the manual never says what it earns. The built-in table uses **+1.50%**, which keeps the senior-to-super-senior step used in the Credexa reference project (0.50 → 0.75) while starting from the manual's Senior +1%. Confirm the real value with Lavanya, who owns the rate matrix.
4. **Endpoint name.** The manual says `/api/fd/calculate`; your WBS says `/api/calculator/fd`. The backend answers on **both**.
5. **Access rule.** L10 says CUSTOMER only, while the FD team's spec says PUBLIC. We follow the manual, with an on/off switch (`calculator.security.enabled`).
6. **Database.** Your WBS mentions `calculator_db`, but the manual defines none. We use no database (section 1.4).
7. **Dependencies.** Real login (Group 10, port 8080) and product-service (port 8081) are built by others. We provide stand-ins and switch to the real services in Steps 16–17.
8. **Product spec paths.** The product YAML mixes `/products` and `/api/product`. Paths are configurable in `application.properties`.

### 1.8 Final folder structure

```
banking-lab/
├── calculator-service/                         ← BACKEND (Spring Boot, port 8082)
│   ├── pom.xml
│   ├── mvnw, mvnw.cmd, .mvn/                  (generated, do not edit)
│   ├── scripts/generate_fd_report.py          (Phase B)
│   ├── reports/                               (created automatically, CSV files)
│   └── src/
│       ├── main/java/com/banklab/calculator/
│       │   ├── CalculatorServiceApplication.java
│       │   ├── model/       InterestType, CompoundingFrequency, TermUnit, CategoryCodes
│       │   ├── dto/         Term, FdCalculationRequest, FdCalculationResponse, CategoryInfo,
│       │   │                CategoryListResponse, ProductInfo, ErrorResponse,
│       │   │                ReportResult, FdReportResponse (B)
│       │   ├── exception/   CalculationException, GlobalExceptionHandler
│       │   ├── rates/       RateInputs, RateProvider, LocalRateProvider, ProductRateProvider (C)
│       │   ├── service/     FdCalculatorService, CategorySelectionService
│       │   ├── controller/  FdCalculatorController, FdReportController (B)
│       │   ├── report/      FdReportService (B)
│       │   ├── config/      CorsConfig, SecurityConfig (C), OpenApiConfig (C)
│       │   ├── security/    JwtService (C), JwtAuthFilter (C)
│       │   ├── auth/        DevUserStore (C), DevAuthController (C)
│       │   └── client/      JsonFields (C), CustomerClient (C), ProductClient (C)
│       ├── main/resources/application.properties
│       └── test/java/com/banklab/calculator/
│           ├── CalculatorServiceApplicationTests.java
│           └── service/FdCalculatorServiceTest.java   (B)
└── banking-frontend/                           ← FRONTEND (Angular, port 4200)
    ├── package.json, proxy.conf.json
    └── src/
        ├── index.html, styles.css
        └── app/
            ├── app.ts, app.html, app.css, app.config.ts, app.routes.ts
            ├── home/        home.ts, home.html, home.css
            ├── calculator/  fd.models.ts, fd-calculator.service.ts,
            │                fd-calculator.ts, fd-calculator.html, fd-calculator.css
            └── auth/        auth.service.ts, auth.interceptor.ts, auth.guard.ts   (C)
                             login/ login.ts, login.html, login.css                (C)
```


---

## Part 2 — The seniors' Credexa ZIP: what it is, and what to take from it

You uploaded `praaneshanandan_Credexa-Application_main_credexa-customer-service.zip`. I extracted it and read all 25 files. Here is everything it contains, what's wrong with it, and exactly what you should and shouldn't take from it.

**The single most important fact first:** this ZIP is **not** the seniors' FD Calculator. It is their **customer-service** — one microservice out of the seven in their Credexa application. The calculator is a different service (`fd-calculator-service`) and is not in this ZIP.

That does not make it useless. It's the closest thing you have to a preview of **Group 10's** Login/Customer module, which your calculator must talk to in Phase C. I've used it to fill in the real values in [Step 16](#step-16-switch-to-group-10s-real-login-and-customer-service).

### 2.1 What's inside

```
customer-service/
├── pom.xml                          ← dependency list (incomplete on its own — see 2.3)
├── start-service.bat                ← Windows one-click start
└── src/main/
    ├── resources/application.yml    ← settings: port, database, JWT secret
    └── java/com/app/customer/
        ├── CustomerServiceApplication.java   ← starting point
        ├── controller/CustomerController.java ← the API doors
        ├── service/CustomerService.java       ← the business rules
        ├── repository/CustomerRepository.java ← database access
        ├── entity/Customer.java               ← the customers table
        ├── dto/            6 files: request/response shapes
        ├── config/         5 files: security, JWT filter, CORS, Swagger, RestTemplate
        ├── client/LoginServiceClient.java     ← calls the login service
        └── exception/      5 files: error types + one global handler
```

**Technology:** exactly the stack your manual prescribes — Java, Spring Boot 3, Maven, MySQL with Spring Data JPA, Spring Security with JWT, and springdoc (Swagger). Two extras: **Lombok** (a library that writes getters and setters for you, via `@Data` and `@Builder`) and their own shared `common-lib`.

**How the pieces talk:**
1. A request arrives with an `Authorization: Bearer <token>` header.
2. `JwtAuthenticationFilter` checks the token and records who the user is.
3. `SecurityConfig` decides whether that user's role may proceed.
4. `CustomerController` receives the call and passes it to `CustomerService`.
5. `CustomerService` reads or writes through `CustomerRepository`, which talks to MySQL (`customer_db`).
6. When it needs the user's ID, `LoginServiceClient` calls the login service over HTTP.

**Their main endpoints** (all under the context path `/api/customer`):

| Endpoint | Who may call it |
|---|---|
| `GET /api/customer/profile` | any logged-in user — returns **their own** profile |
| `GET /api/customer/all` | MANAGER, ADMIN |
| `GET /api/customer/{id}` | CUSTOMER, MANAGER, ADMIN |
| `GET /api/customer/username/{username}` | any logged-in user |
| `GET /api/customer/{id}/classification` | CUSTOMER, MANAGER, ADMIN |
| `GET /api/customer/{id}/360-view` | MANAGER, ADMIN |
| `POST /api/customer`, `PUT /api/customer/{id}` | CUSTOMER, MANAGER, ADMIN |
| `GET /api/customer/health` | everyone |

### 2.2 The part that matters to your calculator

Their `Customer` entity has a field called **`classification`**, and this is the customer category your calculator needs:

```java
public enum CustomerClassification {
    REGULAR,        // Standard interest rates
    PREMIUM,        // 0.25% additional
    VIP,            // 0.50% additional
    SENIOR_CITIZEN, // Senior citizen benefits (0.50% additional)
    SUPER_SENIOR    // Super senior (80+) benefits (0.75% additional)
}
```

And the add-on percentages are defined in `CustomerClassificationResponse.java`:

```java
public static BigDecimal getAdditionalRate(Customer.CustomerClassification classification) {
    return switch (classification) {
        case REGULAR -> BigDecimal.ZERO;
        case PREMIUM -> new BigDecimal("0.25");
        case VIP -> new BigDecimal("0.50");
        case SENIOR_CITIZEN -> new BigDecimal("0.50");
        case SUPER_SENIOR -> new BigDecimal("0.75");
    };
}
```

**Two things to notice, because both are exam-worthy:**

1. **Interest rates are hard-coded inside the customer module.** In your architecture — and in the lab manual — pricing belongs to the **Product & Pricing** module (`PRODUCT_RATE_MATRIX`), which your teammates own. Putting rates in the customer module means a rate change needs a code change and a redeploy of the wrong service.
2. **Their numbers disagree with the lab manual and with their own calculator.** The manual (L6) says Senior Citizen **+1%**; this says **+0.50%**. And their `fd-calculator-service` (which we examined in an earlier session) ignores this table completely and adds a flat 0.25% per classification. So their application has **two hard-coded rate tables in two services that contradict each other**, plus a third source in the manual. This is very likely one of the "issues" you were warned about.

One idea here **is** worth copying: their service **works out Senior/Super-Senior automatically from the date of birth** instead of trusting whatever the form sent. That's good banking practice — suggest it to whoever builds your Customer module.

### 2.3 Can you just open and run this ZIP? No — and here's exactly why

**Question: can I extract it and open it in Visual Studio or VS Code?**
You can *open and read* it in VS Code. You **cannot build or run it.** Four separate blockers, any one of which stops it:

1. **The parent project is missing.** `pom.xml` starts with:
   ```xml
   <parent>
       <groupId>com.app</groupId>
       <artifactId>credexa-parent</artifactId>
       <version>0.0.1-SNAPSHOT</version>
   </parent>
   ```
   That parent file is not in the ZIP. Maven stops immediately with `Non-resolvable parent POM`. Every version number — Spring Boot's, Java's, Lombok's — lives in that missing file, so nothing can even be downloaded.
2. **A required library is missing.** It depends on `com.app:common-lib:0.0.1-SNAPSHOT`, which is also not in the ZIP. `JwtAuthenticationFilter.java` imports `com.app.common.util.JwtUtil` from it, so the code cannot compile.
3. **The build tool is missing.** `start-service.bat` runs `..\mvnw.cmd` — one folder *above* the ZIP's contents. There is no `mvnw`, `mvnw.cmd` or `.mvn` folder anywhere in the ZIP.
4. **It's one service out of seven.** Even fully built, it needs their login service running on port 8081 to issue tokens, and a MySQL server. It has no user interface of its own.

**Which IDE?** **VS Code**, the same one you're already using. Visual Studio (without "Code") is mainly for .NET and C#. You'll notice there is no `.sln` or `.csproj` file anywhere in this ZIP — those are .NET files, and this is a Java project. The equivalent here is **`pom.xml`**, and the folder you'd open is the one containing it.

**If you ever did want to run the whole Credexa app**, you'd need: the complete repository (not this one-service ZIP), JDK 17+, MySQL Server running with the root password matching `application.yml`, and their services started in order — login-service first, then the rest. That's a substantial detour, and it is **not required for your assignment**.

**My recommendation: don't try to run it.** Read it as reference, and build your own module with the guide below. Your calculator doesn't depend on their customer service in any way.

### 2.4 Bugs and problems I found

| # | Problem | Where | Why it matters |
|---|---|---|---|
| 1 | Won't build standalone: missing parent POM, missing `common-lib`, missing Maven wrapper | `pom.xml`, `start-service.bat` | The ZIP alone can never compile |
| 2 | Port confusion: `application.yml` says **8082**, but `start-service.bat` prints "Starting Customer Service on port **8083**" | config vs script | The message lies; you'd look for it on the wrong port |
| 3 | `LoginServiceClient` falls back to `http://localhost:8082/api/auth` — **the customer service's own port** | `LoginServiceClient.java` | If the property is ever missing, the service calls itself and hangs or 404s |
| 4 | Interest add-ons hard-coded in the customer module | `CustomerClassificationResponse.java` | Pricing belongs in Product & Pricing; a rate change needs a code change |
| 5 | Senior = 0.50% contradicts the manual's 1% **and** their own calculator's 0.25% | across services | Three different answers to "what does a senior get?" |
| 6 | Roles are turned into authorities **without** the `ROLE_` prefix, but `SecurityConfig` uses `hasAnyRole(...)`, which requires it | `JwtAuthenticationFilter` + `SecurityConfig` | If `common-lib` doesn't add the prefix, **every** call returns 403. Unverifiable here because `common-lib` is missing |
| 7 | The JWT filter is annotated `@Component` **and** registered in the security chain | `JwtAuthenticationFilter` | Spring Boot then runs it twice, once outside the chain. This is the exact trap our `JwtAuthFilter` avoids by not being a `@Component` |
| 8 | `catch (Exception e)` around the whole token check, which then continues silently | `JwtAuthenticationFilter` | A broken token looks identical to no token; you get a confusing 403 instead of "token expired" |
| 9 | `ddl-auto: update` | `application.yml` | Hibernate rewrites the schema on start-up; fine in a lab, indefensible in a viva about production |
| 10 | The JWT secret is written into the file as a default | `application.yml` | Acceptable for a lab, but say "it would come from an environment variable in production" if asked |
| 11 | No tests at all | whole ZIP | Your L11 test suite is a genuine advantage over this |

### 2.5 What to copy, what not to copy

**COPY AS-IS: nothing.** Not one file transfers into your calculator. Every file is in the package `com.app.customer`, depends on Lombok and on the missing `common-lib`, and belongs to a different module (customer management, not FD calculation). Pasting any of it into `calculator-service` would not compile.

**USE AS AN INTEGRATION SPEC (this is the real value).** These four facts from the ZIP are now baked into Step 16 of this guide, so your calculator already understands them:

| What | Value from the ZIP | Where it's used |
|---|---|---|
| Customer profile address | `GET /api/customer/profile` | Matches the `calculator.customer.profile-path` default |
| Category field name in the JSON | `classification` | Added to the field names `CustomerClient` reads |
| Category values | `REGULAR`, `PREMIUM`, `VIP`, `SENIOR_CITIZEN`, `SUPER_SENIOR` | `CategoryCodes` maps four of these onto our codes |
| JWT secret style | Plain text, 256-bit, not Base64 | Tells you to set `calculator.jwt.secret-base64=false` |

**COPY THE IDEA, NOT THE CODE:**
- Auto-deciding Senior/Super-Senior from the date of birth — suggest it to your Customer-module teammates.
- A `/health` endpoint — you already have one, via Actuator.
- Swagger on every service — you already have it.

**DO NOT COPY:**
- The hard-coded rate table. Rates belong in `PRODUCT_RATE_MATRIX`.
- `@Component` on a JWT filter that is also added to the chain (problem 7).
- `catch (Exception e)` that swallows the reason (problem 8).
- **Port 8082** — their customer-service uses it, and so does your calculator. If you ever run both, change one of them (see Step 16).
- Lombok. It's fine in general, but your calculator is small enough not to need another library, and written-out getters are easier for you to read and explain in a viva.

**KEEP OUR VERSION — it's better, and here's the defensible reason for each:**

| Ours | Theirs | Why ours wins |
|---|---|---|
| `BigDecimal` throughout the money path | Their calculator uses `double` then wraps the result | Exact to the paisa; theirs drifts |
| Add-ons summed, then capped **once** | Flat 0.25% each, and only the first category is read | Ours matches the manual's L11 table exactly |
| Rates come from a pluggable `RateProvider` | Hard-coded in two different services | One source of truth; a rate change is a data change |
| No database for the calculator | Their calculator points at a `calculator_db` it never uses | Stateless calculation, as the FD module's spec requires |
| 20 automatic tests | None | This is your L11 deliverable |
| Product-service down → clear 503 | Silent fallback to a made-up rate | A silent wrong number is worse than a visible error |

### 2.6 So what do you actually do?

Follow this guide from Step 1. Your FD Calculator does not need anything from the ZIP to be built, run, tested or demonstrated. Come back to Step 16 when Group 10's service is running — and if they modelled it on Credexa (likely, since it's the same assignment), the settings in Step 16 will already be correct.

---

## Part 3 — Setup

## Step 1: Install your tools and create your workspace

**What:** install the free programs this project needs.
**Why:** nothing can run without them.

| Tool | Why we need it |
|---|---|
| VS Code | The editor where you open, edit and save files |
| Java JDK 21 | Runs Spring Boot (the backend) |
| Node.js (LTS) | Runs Angular (the frontend) |
| Angular CLI 20 | The `ng` command that creates and runs Angular apps |
| Python 3 | Runs the Lab L11 report script |

### 1a. VS Code
1. Go to **https://code.visualstudio.com** and click **Download**.
2. **Windows:** run the `.exe`, accept the defaults, and keep **"Add to PATH"** ticked.
3. **Mac:** open the `.zip` and drag **Visual Studio Code** into **Applications**.

### 1b. Java JDK 21
1. Go to **https://adoptium.net** and download **Temurin 21 (LTS)**. If the site shows a newer version first, look for "21" under other versions.
2. **Windows:** choose the **x64 `.msi`** and run it.
   - On the **"Custom Setup"** screen, click the icon next to **"Set JAVA_HOME variable"** and choose **"Will be installed on local hard drive"**.
   - Keep **"Add to PATH"** enabled, then click Next through to Install.
3. **Mac:** click Apple menu → **About This Mac** to check your chip.
   - "Apple M1/M2/M3/M4" → download the **aarch64 `.pkg`**. "Intel" → download the **x64 `.pkg`**.
   - Run the installer.

### 1c. Node.js
1. Go to **https://nodejs.org** and download the version marked **LTS**.
2. Run the installer and accept the defaults.
3. **Windows:** leave "Tools for Native Modules" **unticked**.

### 1d. Python
1. Go to **https://www.python.org/downloads** and click the yellow Download button.
2. **Windows:** on the first screen, **tick "Add python.exe to PATH"** before clicking Install Now.
3. **Mac:** run the `.pkg` normally.

### 1e. Create your workspace folder
- **Windows:** File Explorer → **Documents** → right-click empty space → **New → Folder** → name it `banking-lab`.
- **Mac:** Finder → **Documents** → right-click → **New Folder** → name it `banking-lab`.

Use lowercase, a hyphen, and **no spaces**. Spaces in paths break some developer tools.

### 1f. Open the workspace in VS Code
1. **Close VS Code completely and reopen it.** It only sees newly installed tools after a restart.
2. Click **File → Open Folder…** (Mac: **File → Open…**), choose `banking-lab`, and click **Select Folder** / **Open**.
3. If asked "Do you trust the authors…?", click **Yes, I trust the authors**.
4. Click **Terminal → New Terminal**. The prompt should end with `banking-lab>` (Windows) or `banking-lab %` (Mac).

### 1g. VS Code extensions
1. Click the **Extensions** icon on the left bar (four squares), or press `Ctrl+Shift+X` (Mac: `Cmd+Shift+X`).
2. Search for and **Install**:
   - **Extension Pack for Java** (Microsoft)
   - **Spring Boot Extension Pack** (VMware/Broadcom)
   - **Angular Language Service** (Angular)

### 1h. Angular CLI
In the terminal, type this and press Enter:

```
npm install -g @angular/cli@20
```

**Expected:** after 30–90 seconds, `added ... packages`.
- **Mac:** if you see `EACCES: permission denied`, run `sudo npm install -g @angular/cli@20` and type your Mac password. The password is invisible while typing; that's normal.

We pin version **20** so the file names Angular generates match this guide exactly.

### 1i. Verify everything
Run each command and compare the result with the table:

```
java -version
node -v
npm -v
ng version
```

Python check:

```
Windows:  python --version
Mac:      python3 --version
```

| Command | Expected (numbers may differ slightly) |
|---|---|
| `java -version` | `openjdk version "21.0.x"` … Temurin |
| `node -v` | `v20.19` or newer, `v22.x`, or `v24.x` |
| `npm -v` | `10.x` or `11.x` |
| `ng version` | Banner with `Angular CLI: 20.x.x` |
| python | `Python 3.12` or newer |

The first time you run `ng version`, it may ask about sharing usage data or autocompletion. Type `N` and press Enter.

**Common errors in Step 1:**

| Error | Fix |
|---|---|
| `'java' is not recognized` / `command not found: java` | Close and reopen VS Code. If it persists on Windows, rerun the Temurin installer with "Add to PATH" and "Set JAVA_HOME" enabled. |
| Windows: `ng.ps1 cannot be loaded because running scripts is disabled` | Run `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned`, type `Y`, Enter, then retry. |
| Mac: `command not found: ng` | Rerun `sudo npm install -g @angular/cli@20`. |
| Windows: `python --version` opens the Microsoft Store | Rerun the Python installer → **Modify** → tick "Add Python to environment variables". |


---

# Phase A — Standalone FD Calculator (Lab L6)

At the end of Phase A you'll have a working calculator page in the browser, with no login yet.

## Step 2: Create the backend project

**What:** generate an empty Spring Boot project, then replace its settings with ours.
**Why:** Spring Initializr creates the tricky parts for you, including `mvnw`, a helper that downloads Maven (the Java build tool) automatically.

### 2a. Generate the project
1. Open **https://start.spring.io** in your browser.
2. Fill the form:
   - **Project:** Maven
   - **Language:** Java
   - **Spring Boot:** leave the default (any version without "SNAPSHOT"). We replace the version in 2c.
   - **Group:** `com.banklab`
   - **Artifact:** `calculator-service`
   - **Name:** `calculator-service`
   - **Package name:** `com.banklab.calculator`. **Important:** the site pre-fills `com.banklab.calculatorservice`; edit it to `com.banklab.calculator`.
   - **Packaging:** Jar
   - **Configuration:** Properties
   - **Java:** 21 (choose 17 if 21 isn't offered)
   - **Dependencies:** add nothing. We replace the dependency list in 2c.
3. Click **GENERATE**. A file `calculator-service.zip` downloads.

### 2b. Unzip into your workspace
- **Windows:**
  1. Open **Downloads**, right-click `calculator-service.zip`, and choose **Extract All…**.
  2. In the box, change the destination to `C:\Users\<YourName>\Documents\banking-lab`. **Delete any `\calculator-service` at the end**, otherwise you get a folder inside a folder.
  3. Click **Extract**.
- **Mac:**
  1. Double-click the zip in **Downloads**. A folder `calculator-service` appears.
  2. Drag that folder into `Documents/banking-lab`.

**Verify:** in VS Code's Explorer, you should see `banking-lab/calculator-service/pom.xml`. If you instead see `banking-lab/calculator-service/calculator-service/pom.xml`, move the inner folder up one level and delete the empty outer one.

Also check that this file exists: `calculator-service/src/main/java/com/banklab/calculator/CalculatorServiceApplication.java`.
- If the folder is called `calculatorservice` instead of `calculator`, you missed the package name. Delete `banking-lab/calculator-service` and redo 2a.

### 2c. Replace `pom.xml`

**File:** `calculator-service/pom.xml`
**Action:** Replace the entire file

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Spring Boot version is pinned so every step of the guide behaves the same -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/>
    </parent>

    <groupId>com.banklab</groupId>
    <artifactId>calculator-service</artifactId>
    <version>1.0.0</version>
    <name>calculator-service</name>
    <description>FD Calculator service - Banking Technology Lab (CSF 3142), Group 6</description>

    <properties>
        <java.version>17</java.version>
        <springdoc.version>2.8.9</springdoc.version>
    </properties>

    <dependencies>
        <!-- REST APIs (controllers, JSON) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Input validation annotations such as @NotNull -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <!-- /actuator/health endpoint (liveness/readiness probes in the manual) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- Swagger UI: test the API from the browser -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <!-- JUnit tests -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**What it does:** `pom.xml` is the project's shopping list. It tells Maven which libraries to download:
- web (REST APIs)
- validation (input checks)
- actuator (a health check)
- springdoc (Swagger UI, a test page in the browser)
- test (JUnit)

Spring Boot is pinned to **3.5.6** so the project behaves exactly as described here, whatever version the website offered. Java code is compiled for version 17, which runs fine on JDK 21.

### 2d. Replace `application.properties`

**File:** `calculator-service/src/main/resources/application.properties`
**Action:** Replace the entire file

```properties
# ==========================================================
#  calculator-service settings (Phase A)
# ==========================================================
spring.application.name=calculator-service

# The "door number" this backend listens on (your team's WBS: calculator-service = 8082)
server.port=8082

# ---------- FD Calculator rules ----------
# Where base rates and category add-ons come from:
#   LOCAL   = built-in table inside this service (Phase A and B)
#   PRODUCT = live product-service of your teammates (Phase C, Step 17)
calculator.rates.source=LOCAL

# Maximum COMBINED category add-on, in percent (Lab L6: "configured cap (e.g., 2%)")
calculator.rates.add-on-cap=2.00

# ---------- JSON ----------
# Reject decimals where a whole number is expected (e.g. term value 12.5)
spring.jackson.deserialization.accept-float-as-int=false

# ---------- Swagger UI ----------
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.try-it-out-enabled=true
```

**What it does:** settings for the backend.
- `server.port=8082` makes it listen on port 8082 (your WBS).
- `calculator.rates.source=LOCAL` uses the built-in rate table.
- `calculator.rates.add-on-cap=2.00` is the 2% cap from Lab L6.
- Lines starting with `#` are comments and are ignored.

### 2e. Replace the main class

**File:** `calculator-service/src/main/java/com/banklab/calculator/CalculatorServiceApplication.java`
**Action:** Replace the entire file

```java
package com.banklab.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starting point of the FD Calculator backend.
 * Running this class starts a small web server on port 8082.
 */
@SpringBootApplication
public class CalculatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculatorServiceApplication.class, args);
    }
}
```

**What it does:** the starting point. `@SpringBootApplication` tells Spring to find all our classes and start a web server.

### 2f. Replace the generated test class

**File:** `calculator-service/src/test/java/com/banklab/calculator/CalculatorServiceApplicationTests.java`
**Action:** Replace the entire file (create it if it doesn't exist)

```java
package com.banklab.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Checks that the whole application can start (all settings and classes fit together).
 */
@SpringBootTest
class CalculatorServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

**What it does:** a basic test that checks the whole application can start.

### 2g. First run (checkpoint)

**Where:** in the VS Code terminal. First move into the backend folder:

```
cd calculator-service
```

The prompt now ends with `calculator-service>` (Windows) or `calculator-service %` (Mac). Then start the backend:

```
Windows:  .\mvnw.cmd spring-boot:run
Mac:      chmod +x mvnw
          ./mvnw spring-boot:run
```

(On Mac, `chmod +x mvnw` is needed only once. It gives the helper script permission to run.)

**What happens:** the first run downloads Maven and all the libraries, which takes **2–10 minutes**. Later runs take about 15 seconds.

**Expected:** the last lines include something like

```
Tomcat started on port 8082 (http) with context path '/'
Started CalculatorServiceApplication in 3.2 seconds
```

The terminal stays busy. That means the server is running.

**Verify:** open **http://localhost:8082/actuator/health** in your browser. You should see `{"status":"UP"}`.

**Stop it:** click in the terminal and press `Ctrl + C`. You'll start it again after adding the code.

**Common errors:**

| Error | Fix |
|---|---|
| `JAVA_HOME not found` / `java not recognized` | Redo Step 1b, then restart VS Code. |
| `Port 8082 was already in use` | Another copy is still running. Close all terminals (trash-can icon) and try again. |
| `Could not transfer artifact` / download errors | Check your internet connection (college Wi-Fi sometimes blocks downloads; try a mobile hotspot), then rerun. |
| Mac: `permission denied: ./mvnw` | Run `chmod +x mvnw` first. |
| `Non-resolvable parent POM` | `pom.xml` wasn't pasted completely. Paste it again and save. |

---

## Step 3: The "model" package — words the calculator understands

**What:** four small files that define interest types, compounding frequencies, term units, and category-name cleanup.

**How to create the package folder:** in Explorer, right-click `calculator-service/src/main/java/com/banklab/calculator` → **New Folder…** → type `model` → Enter. Create each file below inside `model`.

**File:** `calculator-service/src/main/java/com/banklab/calculator/model/InterestType.java`
**Action:** Create this file

```java
package com.banklab.calculator.model;

import java.util.Locale;

/**
 * The two interest methods required by Lab L6 and the evaluation plan
 * ("FD Calculator (Simple & Compound)").
 */
public enum InterestType {
    SIMPLE,
    COMPOUND;

    /**
     * Converts text such as "simple", "Compound" or "CI" into an InterestType.
     * If nothing is sent we use SIMPLE, because the manual's sample request
     * (Lab L6 section 3.1) does not contain an interest type.
     * Returns null when the text is not recognised.
     */
    public static InterestType parse(String text) {
        if (text == null || text.isBlank()) {
            return SIMPLE;
        }
        String t = text.trim().toUpperCase(Locale.ROOT);
        switch (t) {
            case "SIMPLE", "SI":
                return SIMPLE;
            case "COMPOUND", "CI":
                return COMPOUND;
            default:
                return null;
        }
    }
}
```

**What it does:** lists SIMPLE and COMPOUND, and turns text such as "compound" into the right value. An empty value means SIMPLE, because the manual's sample request has no interest type.

**File:** `calculator-service/src/main/java/com/banklab/calculator/model/CompoundingFrequency.java`
**Action:** Create this file

```java
package com.banklab.calculator.model;

import java.util.Locale;

/**
 * How often interest is added to the balance in compound interest.
 * periodsPerYear is the "n" in the formula M = P x (1 + r/n)^(n x t).
 */
public enum CompoundingFrequency {
    MONTHLY(12),
    QUARTERLY(4),
    HALF_YEARLY(2),
    ANNUALLY(1);

    private final int periodsPerYear;

    CompoundingFrequency(int periodsPerYear) {
        this.periodsPerYear = periodsPerYear;
    }

    public int getPeriodsPerYear() {
        return periodsPerYear;
    }

    /**
     * Converts text such as "quarterly" or "yearly" into a frequency.
     * If nothing is sent we use QUARTERLY (the usual Indian bank practice).
     * Returns null when the text is not recognised.
     */
    public static CompoundingFrequency parse(String text) {
        if (text == null || text.isBlank()) {
            return QUARTERLY;
        }
        String t = text.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        switch (t) {
            case "MONTHLY", "MONTH":
                return MONTHLY;
            case "QUARTERLY", "QUARTER":
                return QUARTERLY;
            case "HALF_YEARLY", "HALFYEARLY", "SEMI_ANNUALLY", "SEMI_ANNUAL":
                return HALF_YEARLY;
            case "ANNUALLY", "ANNUAL", "YEARLY", "YEAR":
                return ANNUALLY;
            default:
                return null;
        }
    }
}
```

**What it does:** gives each frequency its **n** (periods per year) for the compound formula.

**File:** `calculator-service/src/main/java/com/banklab/calculator/model/TermUnit.java`
**Action:** Create this file

```java
package com.banklab.calculator.model;

import java.util.Locale;

/**
 * Units in which an FD term (tenure) can be entered.
 * unitsPerYear converts the term into years: 12 MONTHS = 12/12 = 1 year.
 * min and max are the allowed term range for that unit
 * (7 days to 10 years, the usual range for Indian bank FDs).
 */
public enum TermUnit {
    DAYS(365, 7, 3650),
    MONTHS(12, 1, 120),
    YEARS(1, 1, 10);

    private final int unitsPerYear;
    private final int min;
    private final int max;

    TermUnit(int unitsPerYear, int min, int max) {
        this.unitsPerYear = unitsPerYear;
        this.min = min;
        this.max = max;
    }

    public int getUnitsPerYear() {
        return unitsPerYear;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    /** One-letter code used in the L11 CSV report, e.g. "12M". */
    public String shortCode() {
        return name().substring(0, 1);
    }

    /** Whole months in the term (used to find the right row in a rate matrix). */
    public int toWholeMonths(int value) {
        return (int) ((long) value * 12 / unitsPerYear);
    }

    /**
     * Converts text such as "months", "Month" or "M" into a TermUnit.
     * Returns null when the text is not recognised.
     */
    public static TermUnit parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String t = text.trim().toUpperCase(Locale.ROOT);
        switch (t) {
            case "DAYS", "DAY", "D":
                return DAYS;
            case "MONTHS", "MONTH", "M":
                return MONTHS;
            case "YEARS", "YEAR", "Y", "YR", "YRS":
                return YEARS;
            default:
                return null;
        }
    }
}
```

**What it does:** days, months or years.
- Each unit knows how many of it fit in a year (365, 12, 1). That converts a term into years: 18 months = 18/12 = 1.5 years.
- Each unit also has an allowed range: 7 days to 10 years.

**File:** `calculator-service/src/main/java/com/banklab/calculator/model/CategoryCodes.java`
**Action:** Create this file

```java
package com.banklab.calculator.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Turns category names typed in different ways into one standard code.
 * The manual writes "Senior Citizen" in Lab L6 but "SENIOR" in Lab L10/L11,
 * so both must mean the same thing.
 */
public final class CategoryCodes {

    private static final Map<String, String> ALIASES = Map.of(
            "SENIOR_CITIZEN", "SENIOR",
            "SR_CITIZEN", "SENIOR",
            "SUPER_SENIOR_CITIZEN", "SUPER_SENIOR",
            "STAFF", "EMPLOYEE",
            "BANK_EMPLOYEE", "EMPLOYEE",
            "PREMIUM", "PREMIUM_USER",
            "REGULAR", "GENERAL",
            "NORMAL", "GENERAL");

    private CategoryCodes() {
    }

    /** "Senior Citizen" becomes "SENIOR", "premium user" becomes "PREMIUM_USER". */
    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String t = raw.trim().toUpperCase(Locale.ROOT).replaceAll("[\\s\\-]+", "_");
        return ALIASES.getOrDefault(t, t);
    }

    /** Normalises a whole list, removing blanks and duplicates but keeping the order. */
    public static List<String> normalizeAll(List<String> raw) {
        Set<String> result = new LinkedHashSet<>();
        if (raw != null) {
            for (String r : raw) {
                String code = normalize(r);
                if (code != null) {
                    result.add(code);
                }
            }
        }
        return new ArrayList<>(result);
    }
}
```

**What it does:** "Senior Citizen" (the Lab L6 wording) and "SENIOR" (the L10/L11 wording) both become `SENIOR`. Duplicates are removed.

---

## Step 4: The "dto" package — shapes of the data

**What:** DTO = Data Transfer Object, the exact shape of the JSON going in and out.
Create a folder `dto` next to `model` (right-click `calculator` → New Folder… → `dto`), then create the seven files below inside it.

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/Term.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * FD term exactly as written in Lab L6: "term": { "value": 12, "unit": "months" }
 */
public class Term {

    @NotNull(message = "Term value is required")
    @Positive(message = "Term value must be greater than 0")
    private Integer value;

    @NotBlank(message = "Term unit is required: DAYS, MONTHS or YEARS")
    private String unit;

    public Term() {
    }

    public Term(Integer value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
```

**What it does:** the `"term": {"value": 12, "unit": "months"}` part of the Lab L6 request. `@NotNull` and `@Positive` are automatic checks.

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/FdCalculationRequest.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * What the frontend sends to POST /api/fd/calculate.
 * The first four fields are exactly the Lab L6 payload:
 *   principal, term, baseRate, categories
 * The other fields are optional extras (interest type, compounding, product).
 */
public class FdCalculationRequest {

    @NotNull(message = "Deposit amount (principal) is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Deposit amount must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Deposit amount can have at most 2 decimal places")
    private BigDecimal principal;

    @NotNull(message = "Term is required (value and unit)")
    @Valid
    private Term term;

    @DecimalMin(value = "0.00", inclusive = false, message = "Base rate must be greater than 0")
    @Digits(integer = 3, fraction = 2, message = "Base rate can have at most 2 decimal places")
    private BigDecimal baseRate;

    private List<String> categories = new ArrayList<>();

    /** SIMPLE or COMPOUND. Empty means SIMPLE (the manual's sample has no interest type). */
    private String interestType;

    /** MONTHLY, QUARTERLY, HALF_YEARLY or ANNUALLY. Used only for COMPOUND. Empty means QUARTERLY. */
    private String compoundingFrequency;

    /** Product code such as FD001. Needed only when rates come from product-service (Phase C). */
    private String productCode;

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public String getCompoundingFrequency() {
        return compoundingFrequency;
    }

    public void setCompoundingFrequency(String compoundingFrequency) {
        this.compoundingFrequency = compoundingFrequency;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
```

**What it does:** everything the page sends.
- The first four fields are the Lab L6 payload.
- The annotations reject a missing amount, zero or negative values, and more than 2 decimal places, before any maths runs.

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/FdCalculationResponse.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * What the backend sends back after a calculation.
 * maturityAmount, effectiveRate and interestEarned are the three outputs
 * named in Lab L6 section 3.1; the rest explain HOW the result was reached.
 */
public record FdCalculationResponse(
        BigDecimal principal,
        int termValue,
        String termUnit,
        String interestType,
        String compoundingFrequency,
        String calculationMethod,
        String productCode,
        BigDecimal baseRate,
        List<String> categories,
        Map<String, BigDecimal> categoryAddOns,
        BigDecimal requestedAddOn,
        BigDecimal appliedAddOn,
        BigDecimal addOnCap,
        boolean capApplied,
        BigDecimal maxEffectiveRate,
        boolean maxRateCapApplied,
        BigDecimal effectiveRate,
        BigDecimal interestEarned,
        BigDecimal maturityAmount,
        BigDecimal apy,
        LocalDate startDate,
        LocalDate maturityDate,
        String currencyCode,
        String rateSource) {
}
```

**What it does:** everything sent back.
- `maturityAmount`, `effectiveRate` and `interestEarned` are the three fields named in the manual.
- The rest explain the calculation: base rate, each category's add-on, whether the cap was applied, dates and APY.
- A `record` is a short way to write a class that only holds data.

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/CategoryInfo.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

import java.math.BigDecimal;

/**
 * One customer category shown in the dropdown, e.g. SENIOR, "Senior Citizen", +1.00%.
 * additionalRatePercent can be null when the add-on depends on a product (Phase C).
 */
public record CategoryInfo(String code, String name, BigDecimal additionalRatePercent) {
}
```

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/CategoryListResponse.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Answer of GET /api/fd/categories.
 * rateSource     : LOCAL (built-in table) or PRODUCT (product-service)
 * categorySource : REQUEST (user picks categories) or PROFILE (taken from the logged-in customer)
 * addOnCap       : maximum combined add-on in percent (2.00)
 */
public record CategoryListResponse(
        String rateSource,
        String categorySource,
        BigDecimal addOnCap,
        List<CategoryInfo> categories) {
}
```

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/ProductInfo.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

import java.math.BigDecimal;

/**
 * A deposit product from product-service (used only in Phase C when rates come from products).
 */
public record ProductInfo(
        String code,
        String name,
        BigDecimal minRate,
        BigDecimal maxRate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String defaultCompoundingFrequency) {
}
```

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/ErrorResponse.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

import java.util.List;

/**
 * Error format used by every error answer of this service.
 * Same shape as your product-service spec: { code, message, details[] }.
 */
public record ErrorResponse(String code, String message, List<String> details) {
}
```

**What they do:**
- `CategoryInfo` is one dropdown entry.
- `CategoryListResponse` is the answer for the dropdown, and includes the cap.
- `ProductInfo` is used in Phase C.
- `ErrorResponse` is the single error format, the same as your product-service spec: `{code, message, details}`.

---

## Step 5: Errors and rate providers

Create two folders next to `dto`: `exception` and `rates`.

**File:** `calculator-service/src/main/java/com/banklab/calculator/exception/CalculationException.java`
**Action:** Create this file

```java
package com.banklab.calculator.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Our own error type. Whenever something is wrong (bad input, missing product,
 * a service that is down) we throw this, and GlobalExceptionHandler turns it
 * into a friendly JSON answer with the right HTTP status code.
 */
public class CalculationException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final List<String> details;

    public CalculationException(HttpStatus status, String code, String message, List<String> details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    /** 400 - one or more input values are wrong. */
    public static CalculationException validation(List<String> details) {
        return new CalculationException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Please correct the highlighted values.", details);
    }

    /** 400 - a single input problem. */
    public static CalculationException badRequest(String code, String message) {
        return new CalculationException(HttpStatus.BAD_REQUEST, code, message, List.of(message));
    }

    /** 401 - not logged in / wrong password. */
    public static CalculationException unauthorized(String code, String message) {
        return new CalculationException(HttpStatus.UNAUTHORIZED, code, message, List.of());
    }

    /** 404 - something that was asked for does not exist. */
    public static CalculationException notFound(String code, String message) {
        return new CalculationException(HttpStatus.NOT_FOUND, code, message, List.of());
    }

    /** 422 - the input is understood but breaks a business rule (e.g. product not active). */
    public static CalculationException unprocessable(String code, String message) {
        return new CalculationException(HttpStatus.UNPROCESSABLE_ENTITY, code, message, List.of(message));
    }

    /** 502 - another service answered with an error. */
    public static CalculationException badGateway(String code, String message) {
        return new CalculationException(HttpStatus.BAD_GATEWAY, code, message, List.of());
    }

    /** 503 - another service (product/customer) is not reachable. */
    public static CalculationException serviceUnavailable(String code, String message) {
        return new CalculationException(HttpStatus.SERVICE_UNAVAILABLE, code, message, List.of());
    }

    /** 500 - something failed inside this service. */
    public static CalculationException internal(String code, String message, List<String> details) {
        return new CalculationException(HttpStatus.INTERNAL_SERVER_ERROR, code, message, details);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public List<String> getDetails() {
        return details;
    }
}
```

**What it does:** our own error type. Each shortcut (`validation`, `notFound`, …) carries the right HTTP status code: 400 for bad input, 404 for not found, 503 for a service that's down.

**File:** `calculator-service/src/main/java/com/banklab/calculator/rates/RateInputs.java`
**Action:** Create this file

```java
package com.banklab.calculator.rates;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Everything the calculator needs to know about rates for ONE calculation.
 *
 * baseRate         : base yearly rate in percent (e.g. 6.50)
 * addOns           : add-on percent for each selected category (e.g. SENIOR=1.00, EMPLOYEE=1.00)
 * addOnCap         : maximum combined add-on (e.g. 2.00)
 * maxEffectiveRate : product-defined maximum rate (Lab L11); null when there is no product
 * source           : where the numbers came from, e.g. "LOCAL" or "PRODUCT:FD001"
 */
public record RateInputs(
        BigDecimal baseRate,
        Map<String, BigDecimal> addOns,
        BigDecimal addOnCap,
        BigDecimal maxEffectiveRate,
        String source) {
}
```

**File:** `calculator-service/src/main/java/com/banklab/calculator/rates/RateProvider.java`
**Action:** Create this file

```java
package com.banklab.calculator.rates;

import com.banklab.calculator.dto.CategoryInfo;
import com.banklab.calculator.dto.ProductInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * A "plug" for rate data. The calculator does not care WHERE rates come from;
 * it just asks a RateProvider.
 *
 *  - LocalRateProvider   : built-in table (Phase A/B)
 *  - ProductRateProvider : your teammates' product-service (Phase C)
 *
 * Only one of them is switched on at a time, chosen by the setting
 * calculator.rates.source in application.properties.
 * (Same "pluggable" idea as the AccountNumberGenerator interface in Lab L12.)
 */
public interface RateProvider {

    /** "LOCAL" or "PRODUCT". */
    String sourceName();

    /** True when the base rate must be typed by the user (LOCAL). False when it comes from a product. */
    boolean usesRequestBaseRate();

    /** Maximum combined category add-on in percent. */
    BigDecimal addOnCap();

    /** Deposit products the user can pick from (empty list in LOCAL mode). */
    List<ProductInfo> listProducts();

    /** Categories for the dropdown, with their add-on percent. */
    List<CategoryInfo> listCategories(String productCode);

    /**
     * Rates for one calculation.
     *
     * @param productCode       product such as FD001 (may be null in LOCAL mode)
     * @param requestedBaseRate base rate typed by the user (may be null in PRODUCT mode)
     * @param categoryCodes     normalised category codes, e.g. [SENIOR, EMPLOYEE]
     * @param principal         deposit amount (products can have amount bands)
     * @param tenureMonths      term in whole months (products can have term bands)
     */
    RateInputs getRateInputs(String productCode,
                             BigDecimal requestedBaseRate,
                             List<String> categoryCodes,
                             BigDecimal principal,
                             int tenureMonths);
}
```

**File:** `calculator-service/src/main/java/com/banklab/calculator/rates/LocalRateProvider.java`
**Action:** Create this file

```java
package com.banklab.calculator.rates;

import com.banklab.calculator.dto.CategoryInfo;
import com.banklab.calculator.dto.ProductInfo;
import com.banklab.calculator.exception.CalculationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Built-in rate table, used until product-service is live
 * (your WBS task A3.2 "Stub product data provider for offline dev").
 *
 * Add-on values:
 *   SENIOR       +1.00  -> stated in Lab L6 ("Senior Citizen +1%")
 *   EMPLOYEE     +1.00  -> inferred so that Lab L11 row 1 gives 8.5%
 *   PREMIUM_USER +2.00  -> inferred so that Lab L11 row 2 shows +3.0% before the cap
 *   SUPER_SENIOR +1.50  -> not in the manual; your team's WBS seeds this category, and
 *                          1.50 keeps the senior:super-senior step used by the Credexa
 *                          reference project (0.50 -> 0.75). CONFIRM the value with Lavanya.
 *   GENERAL       0.00  -> ordinary customer, no bonus
 * The base rate is typed by the user, exactly like the Lab L6 payload ("baseRate": 6.5).
 *
 * Active only when calculator.rates.source=LOCAL (or when the setting is missing).
 */
@Component
@ConditionalOnProperty(name = "calculator.rates.source", havingValue = "LOCAL", matchIfMissing = true)
public class LocalRateProvider implements RateProvider {

    private static final Map<String, CategoryInfo> TABLE = new LinkedHashMap<>();

    static {
        add("GENERAL", "General (Regular)", "0.00");
        add("SENIOR", "Senior Citizen", "1.00");
        add("EMPLOYEE", "Bank Employee", "1.00");
        add("PREMIUM_USER", "Premium User", "2.00");
        add("SUPER_SENIOR", "Super Senior Citizen (80+)", "1.50");
    }

    private static void add(String code, String name, String addOn) {
        TABLE.put(code, new CategoryInfo(code, name, new BigDecimal(addOn)));
    }

    private final BigDecimal addOnCap;

    public LocalRateProvider(@Value("${calculator.rates.add-on-cap:2.00}") BigDecimal addOnCap) {
        this.addOnCap = addOnCap;
    }

    @Override
    public String sourceName() {
        return "LOCAL";
    }

    @Override
    public boolean usesRequestBaseRate() {
        return true;
    }

    @Override
    public BigDecimal addOnCap() {
        return addOnCap;
    }

    @Override
    public List<ProductInfo> listProducts() {
        return List.of();
    }

    @Override
    public List<CategoryInfo> listCategories(String productCode) {
        return new ArrayList<>(TABLE.values());
    }

    @Override
    public RateInputs getRateInputs(String productCode,
                                    BigDecimal requestedBaseRate,
                                    List<String> categoryCodes,
                                    BigDecimal principal,
                                    int tenureMonths) {
        Map<String, BigDecimal> addOns = new LinkedHashMap<>();
        List<String> unknown = new ArrayList<>();
        for (String code : categoryCodes) {
            CategoryInfo info = TABLE.get(code);
            if (info == null) {
                unknown.add(code);
            } else {
                addOns.put(code, info.additionalRatePercent());
            }
        }
        if (!unknown.isEmpty()) {
            throw CalculationException.validation(List.of(
                    "Unknown customer category: " + String.join(", ", unknown)
                            + ". Allowed: " + String.join(", ", TABLE.keySet()) + "."));
        }
        return new RateInputs(requestedBaseRate, addOns, addOnCap, null, "LOCAL");
    }
}
```

**What they do:**
- `RateProvider` is a "plug shape". The calculator asks it for rates without caring where they come from.
- `LocalRateProvider` is the built-in table: General 0%, Senior +1%, Employee +1%, Premium User +2%, Super Senior +1.5%.
- In Phase C, `ProductRateProvider` plugs in instead, when you change one setting. This is the same idea as the pluggable `AccountNumberGenerator` in Lab L12.

---

## Step 6: The calculation engine

Create a folder `service` next to `rates`.

**File:** `calculator-service/src/main/java/com/banklab/calculator/service/FdCalculatorService.java`
**Action:** Create this file

```java
package com.banklab.calculator.service;

import com.banklab.calculator.dto.FdCalculationRequest;
import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.Term;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.model.CategoryCodes;
import com.banklab.calculator.model.CompoundingFrequency;
import com.banklab.calculator.model.InterestType;
import com.banklab.calculator.model.TermUnit;
import com.banklab.calculator.rates.RateInputs;
import com.banklab.calculator.rates.RateProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The heart of the module: validates the input, works out the effective rate
 * (base + category add-ons, capped) and applies the Simple or Compound formula.
 *
 * All money maths uses BigDecimal (exact decimal numbers), never double,
 * so results are correct to the paisa.
 */
@Service
public class FdCalculatorService {

    /** Smallest deposit accepted (usual Indian bank minimum). */
    public static final BigDecimal MIN_PRINCIPAL = new BigDecimal("1000.00");
    /** Largest deposit accepted: 10 crore rupees. */
    public static final BigDecimal MAX_PRINCIPAL = new BigDecimal("100000000.00");
    /** Largest base rate accepted, in percent. */
    public static final BigDecimal MAX_BASE_RATE = new BigDecimal("15.00");

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    /** 34 significant digits for all intermediate steps. */
    private static final MathContext MC = MathContext.DECIMAL128;
    private static final ZoneId INDIA = ZoneId.of("Asia/Kolkata");
    private static final String RUPEE = "\u20B9";

    private final RateProvider rateProvider;

    public FdCalculatorService(RateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    public FdCalculationResponse calculate(FdCalculationRequest request) {
        if (request == null) {
            throw CalculationException.badRequest("VALIDATION_ERROR", "Request body is required.");
        }
        List<String> errors = new ArrayList<>();

        // ---------- 1. Deposit amount ----------
        BigDecimal principal = request.getPrincipal();
        if (principal == null) {
            errors.add("Deposit amount (principal) is required.");
        } else if (principal.signum() <= 0) {
            errors.add("Deposit amount must be greater than 0.");
        } else if (principal.compareTo(MIN_PRINCIPAL) < 0) {
            errors.add("Minimum deposit amount is " + RUPEE + "1,000.");
        } else if (principal.compareTo(MAX_PRINCIPAL) > 0) {
            errors.add("Maximum deposit amount is " + RUPEE + "10,00,00,000 (10 crore).");
        } else if (principal.stripTrailingZeros().scale() > 2) {
            errors.add("Deposit amount can have at most 2 decimal places (paise).");
        }

        // ---------- 2. Term ----------
        Term term = request.getTerm();
        TermUnit unit = null;
        int termValue = 0;
        if (term == null || term.getValue() == null) {
            errors.add("Term value is required.");
        } else {
            termValue = term.getValue();
            unit = TermUnit.parse(term.getUnit());
            if (unit == null) {
                errors.add("Term unit must be DAYS, MONTHS or YEARS.");
            } else if (termValue < unit.getMin() || termValue > unit.getMax()) {
                errors.add("For " + unit.name() + ", the term must be between "
                        + unit.getMin() + " and " + unit.getMax() + ".");
            }
        }

        // ---------- 3. Interest type and compounding ----------
        InterestType interestType = InterestType.parse(request.getInterestType());
        if (interestType == null) {
            errors.add("Interest type must be SIMPLE or COMPOUND.");
        }
        CompoundingFrequency frequency = null;
        if (interestType == InterestType.COMPOUND) {
            frequency = CompoundingFrequency.parse(request.getCompoundingFrequency());
            if (frequency == null) {
                errors.add("Compounding frequency must be MONTHLY, QUARTERLY, HALF_YEARLY or ANNUALLY.");
            }
        }

        // ---------- 4. Base rate (only when the user types it) ----------
        if (rateProvider.usesRequestBaseRate()) {
            checkBaseRate(request.getBaseRate(), errors);
        }

        if (!errors.isEmpty()) {
            throw CalculationException.validation(errors);
        }

        // ---------- 5. Rates: base + category add-ons ----------
        List<String> categories = CategoryCodes.normalizeAll(request.getCategories());
        RateInputs rates = rateProvider.getRateInputs(
                request.getProductCode(), request.getBaseRate(), categories, principal,
                unit.toWholeMonths(termValue));

        BigDecimal baseRate = rates.baseRate();
        List<String> rateErrors = new ArrayList<>();
        checkBaseRate(baseRate, rateErrors);
        if (!rateErrors.isEmpty()) {
            throw CalculationException.validation(rateErrors);
        }

        // Sum ALL selected add-ons, then cap ONCE (Lab L6 / L11)
        BigDecimal requestedAddOn = BigDecimal.ZERO;
        for (BigDecimal addOn : rates.addOns().values()) {
            requestedAddOn = requestedAddOn.add(addOn);
        }
        BigDecimal cap = rates.addOnCap();
        boolean capApplied = requestedAddOn.compareTo(cap) > 0;
        BigDecimal appliedAddOn = capApplied ? cap : requestedAddOn;
        BigDecimal effectiveRate = baseRate.add(appliedAddOn);

        // Product-defined maximum rate (Lab L11), only when a product supplies one
        boolean maxRateCapApplied = false;
        if (rates.maxEffectiveRate() != null && effectiveRate.compareTo(rates.maxEffectiveRate()) > 0) {
            effectiveRate = rates.maxEffectiveRate();
            maxRateCapApplied = true;
        }

        // ---------- 6. The formula ----------
        BigDecimal maturity;
        BigDecimal apy = null;
        String method;
        if (interestType == InterestType.SIMPLE) {
            maturity = simpleMaturity(principal, effectiveRate, termValue, unit);
            method = "SIMPLE";
        } else {
            maturity = compoundMaturity(principal, effectiveRate, termValue, unit, frequency);
            apy = annualYield(effectiveRate, frequency);
            method = "COMPOUND_" + frequency.name();
        }
        BigDecimal principal2 = principal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal maturity2 = maturity.setScale(2, RoundingMode.HALF_UP);
        BigDecimal interest2 = maturity2.subtract(principal2);

        // ---------- 7. Dates ----------
        LocalDate startDate = LocalDate.now(INDIA);
        LocalDate maturityDate = switch (unit) {
            case DAYS -> startDate.plusDays(termValue);
            case MONTHS -> startDate.plusMonths(termValue);
            case YEARS -> startDate.plusYears(termValue);
        };

        Map<String, BigDecimal> addOns2 = new LinkedHashMap<>();
        rates.addOns().forEach((code, value) -> addOns2.put(code, scale2(value)));

        return new FdCalculationResponse(
                principal2,
                termValue,
                unit.name(),
                interestType.name(),
                frequency == null ? null : frequency.name(),
                method,
                request.getProductCode(),
                scale2(baseRate),
                new ArrayList<>(addOns2.keySet()),
                addOns2,
                scale2(requestedAddOn),
                scale2(appliedAddOn),
                scale2(cap),
                capApplied,
                rates.maxEffectiveRate() == null ? null : scale2(rates.maxEffectiveRate()),
                maxRateCapApplied,
                scale2(effectiveRate),
                interest2,
                maturity2,
                apy,
                startDate,
                maturityDate,
                "INR",
                rates.source());
    }

    private static void checkBaseRate(BigDecimal baseRate, List<String> errors) {
        if (baseRate == null) {
            errors.add("Base rate is required.");
        } else if (baseRate.signum() <= 0) {
            errors.add("Base rate must be greater than 0.");
        } else if (baseRate.compareTo(MAX_BASE_RATE) > 0) {
            errors.add("Base rate cannot be more than 15% per year.");
        } else if (baseRate.stripTrailingZeros().scale() > 2) {
            errors.add("Base rate can have at most 2 decimal places.");
        }
    }

    /**
     * SIMPLE INTEREST (Lab L6): M = P + (P x R x t / 100)
     * t in years = termValue / unitsPerYear  (e.g. 18 months = 18/12 = 1.5 years)
     */
    public static BigDecimal simpleMaturity(BigDecimal principal, BigDecimal ratePercent,
                                            int termValue, TermUnit unit) {
        BigDecimal interest = principal
                .multiply(ratePercent, MC)
                .multiply(BigDecimal.valueOf(termValue), MC)
                .divide(HUNDRED.multiply(BigDecimal.valueOf(unit.getUnitsPerYear())), MC);
        return principal.add(interest, MC);
    }

    /**
     * COMPOUND INTEREST (Lab L6): M = P x (1 + r/n)^(n x t), with r = rate / 100.
     *
     * Total periods = n x t. If that is not a whole number (e.g. 5 months with
     * quarterly compounding = 1 full quarter + 2/3 of a quarter), the full
     * periods are compounded and the leftover part-period earns simple interest
     * on the grown amount (usual Indian bank practice).
     */
    public static BigDecimal compoundMaturity(BigDecimal principal, BigDecimal ratePercent,
                                              int termValue, TermUnit unit,
                                              CompoundingFrequency frequency) {
        int n = frequency.getPeriodsPerYear();
        BigDecimal periodRate = ratePercent.divide(HUNDRED.multiply(BigDecimal.valueOf(n)), MC);

        long numerator = (long) termValue * n;       // periods = termValue x n / unitsPerYear
        int denominator = unit.getUnitsPerYear();
        int wholePeriods = (int) (numerator / denominator);
        BigDecimal leftoverFraction = BigDecimal.valueOf(numerator % denominator)
                .divide(BigDecimal.valueOf(denominator), MC);

        BigDecimal growth = BigDecimal.ONE.add(periodRate).pow(wholePeriods, MC);
        BigDecimal leftoverGrowth = BigDecimal.ONE.add(periodRate.multiply(leftoverFraction, MC));
        return principal.multiply(growth, MC).multiply(leftoverGrowth, MC);
    }

    /**
     * Annual Percentage Yield (your WBS task B4.1): APY = (1 + r/n)^n - 1, shown in percent.
     * It tells the customer what the compound rate is "really worth" per year.
     */
    public static BigDecimal annualYield(BigDecimal ratePercent, CompoundingFrequency frequency) {
        int n = frequency.getPeriodsPerYear();
        BigDecimal periodRate = ratePercent.divide(HUNDRED.multiply(BigDecimal.valueOf(n)), MC);
        return BigDecimal.ONE.add(periodRate).pow(n, MC)
                .subtract(BigDecimal.ONE)
                .multiply(HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal scale2(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
```

**What it does (the heart of the module), in order:**
1. Checks every input and collects **all** problems at once, so the user sees them together.
2. Gets the base rate and each category's add-on from the rate provider.
3. Adds up **all** add-ons, then applies the cap **once** (Lab L6/L11).
4. Applies the Simple or Compound formula with `BigDecimal`, which does exact decimal maths (`double` would drift by fractions of a paisa).
5. Rounds to 2 decimals and works out the maturity date.

**File:** `calculator-service/src/main/java/com/banklab/calculator/service/CategorySelectionService.java`
**Action:** Create this file

```java
package com.banklab.calculator.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

/**
 * Decides WHICH customer categories are used for a calculation.
 *
 * Phase A/B version: the categories ticked by the user on the page are used
 * as they are ("REQUEST" mode). Nobody is logged in yet, so "my categories"
 * is always empty.
 *
 * In Phase C (Step 15) this whole file is replaced by a version that can also
 * take the categories from the logged-in customer's profile (Lab L10).
 */
@Service
public class CategorySelectionService {

    /** "REQUEST" = categories come from the page. */
    public String categorySource() {
        return "REQUEST";
    }

    /** Categories to use for this calculation. */
    public List<String> resolve(List<String> requestedCategories, Principal principal,
                                HttpServletRequest httpRequest) {
        return requestedCategories == null ? List.of() : requestedCategories;
    }

    /** Categories of the logged-in customer (none in Phase A/B). */
    public List<String> myCategories(Principal principal, HttpServletRequest httpRequest) {
        return List.of();
    }
}
```

**What it does:** decides which categories are used. In Phase A it's simply the ones ticked on the page. In Phase C this file is replaced by a version that uses the logged-in customer's categories (Lab L10).

---

## Step 7: The API controller, error handler and CORS — then test the backend

Create folders `controller` and `config` next to `service`. `exception` already exists.

**File:** `calculator-service/src/main/java/com/banklab/calculator/controller/FdCalculatorController.java`
**Action:** Create this file

```java
package com.banklab.calculator.controller;

import com.banklab.calculator.dto.CategoryListResponse;
import com.banklab.calculator.dto.FdCalculationRequest;
import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.ProductInfo;
import com.banklab.calculator.rates.RateProvider;
import com.banklab.calculator.service.CategorySelectionService;
import com.banklab.calculator.service.FdCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * The API "doors" of the FD Calculator.
 *
 *   GET  /api/fd/categories     -> categories + add-on % for the dropdown (Lab L6 section 4)
 *   GET  /api/fd/products       -> FD products (only filled in Phase C)
 *   GET  /api/fd/my-categories  -> logged-in customer's categories (only filled in Phase C)
 *   POST /api/fd/calculate      -> the calculation (Lab L6 section 3.1)
 *   POST /api/calculator/fd     -> same calculation, the name used in your team's WBS
 */
@RestController
@Tag(name = "FD Calculator", description = "Fixed Deposit maturity calculator (Labs L6, L10, L11)")
public class FdCalculatorController {

    private final FdCalculatorService calculatorService;
    private final RateProvider rateProvider;
    private final CategorySelectionService categorySelection;

    public FdCalculatorController(FdCalculatorService calculatorService,
                                  RateProvider rateProvider,
                                  CategorySelectionService categorySelection) {
        this.calculatorService = calculatorService;
        this.rateProvider = rateProvider;
        this.categorySelection = categorySelection;
    }

    @GetMapping("/api/fd/categories")
    @Operation(summary = "List customer categories with their additional interest %")
    public CategoryListResponse categories(@RequestParam(required = false) String productCode) {
        return new CategoryListResponse(
                rateProvider.sourceName(),
                categorySelection.categorySource(),
                rateProvider.addOnCap(),
                rateProvider.listCategories(productCode));
    }

    @GetMapping("/api/fd/products")
    @Operation(summary = "List FD products (empty when rates are LOCAL)")
    public List<ProductInfo> products() {
        return rateProvider.listProducts();
    }

    @GetMapping("/api/fd/my-categories")
    @Operation(summary = "Categories of the logged-in customer (Lab L10)")
    public List<String> myCategories(Principal principal, HttpServletRequest httpRequest) {
        return categorySelection.myCategories(principal, httpRequest);
    }

    @PostMapping({"/api/fd/calculate", "/api/calculator/fd"})
    @Operation(summary = "Calculate FD maturity amount, interest earned and effective rate")
    public FdCalculationResponse calculate(@Valid @RequestBody FdCalculationRequest request,
                                           Principal principal,
                                           HttpServletRequest httpRequest) {
        request.setCategories(categorySelection.resolve(request.getCategories(), principal, httpRequest));
        return calculatorService.calculate(request);
    }
}
```

**What it does:** defines the API doors.
- `@GetMapping` means "answer GET requests at this address"; `@PostMapping` means "answer POST requests".
- `@Valid` runs the automatic checks from Step 4.
- The calculate door has two addresses, the manual's and your WBS's.

**File:** `calculator-service/src/main/java/com/banklab/calculator/exception/GlobalExceptionHandler.java`
**Action:** Create this file

```java
package com.banklab.calculator.exception;

import com.banklab.calculator.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Catches every error and turns it into the same friendly JSON shape:
 * { "code": "...", "message": "...", "details": ["...", "..."] }
 * so the Angular page can always show a clear message.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Our own errors (validation, product not found, service down, ...). */
    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<ErrorResponse> handleCalculation(CalculationException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponse(ex.getCode(), ex.getMessage(), ex.getDetails()));
    }

    /** Errors from annotations such as @NotNull on the request classes. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.add(fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "Please correct the highlighted values.", details));
    }

    /** Body missing or not valid JSON, e.g. "principal": "abc". */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        String msg = "The request body is missing or has a wrong format "
                + "(for example text where a number is expected, or a decimal term value).";
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_JSON", msg, List.of(msg)));
    }

    /** e.g. sending GET to an address that only accepts POST. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("METHOD_NOT_ALLOWED", ex.getMessage(), List.of()));
    }

    /** Unknown address. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", "No such address: /" + ex.getResourcePath(), List.of()));
    }

    /** Anything unexpected: log the full error for the developer, show a short message to the user. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR",
                        "Something went wrong on the server: " + ex.getMessage(), List.of()));
    }
}
```

**What it does:** catches every error anywhere in the backend and turns it into the same friendly JSON. The page can then always show a clear message instead of a crash.

**File:** `calculator-service/src/main/java/com/banklab/calculator/config/CorsConfig.java`
**Action:** Create this file

```java
package com.banklab.calculator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Browsers block a page from localhost:4200 calling localhost:8082 unless the
 * backend says it is allowed (this is called CORS). Our Angular app uses a
 * proxy so it normally does not need this, but a teammate's app might call
 * this service directly, so we allow the Angular dev address.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition");
    }
}
```

**What it does:** allows a web page on port 4200 to call this backend directly, if a teammate's app ever does that.

### 7a. Run the backend
Make sure every file is saved (no white dots on tabs). In the terminal, which is still inside `calculator-service`:

```
Windows:  .\mvnw.cmd spring-boot:run
Mac:      ./mvnw spring-boot:run
```

**Expected:** `Started CalculatorServiceApplication …`. If you see `COMPILATION ERROR` or `cannot find symbol` instead, go to [Step 19](#step-19--troubleshooting): it's almost always a missing file, an unsaved file, or a file in the wrong folder.

### 7b. Test with Swagger UI (a test page built into the backend)
1. Open **http://localhost:8082/swagger-ui.html**. A page titled "OpenAPI definition" appears, listing **FD Calculator** endpoints.
2. Click **GET /api/fd/categories** → **Try it out** → **Execute**.
   - **Expected:** under "Response body", 5 categories (GENERAL 0.00, SENIOR 1.00, EMPLOYEE 1.00, PREMIUM_USER 2.00, SUPER_SENIOR 1.50), `"addOnCap": 2.00`, `"rateSource": "LOCAL"`.
3. Click **POST /api/fd/calculate** → **Try it out**. Replace the example body with Sample 1 (this is exactly the Lab L6 payload shape):

```json
{
  "principal": 100000,
  "term": { "value": 12, "unit": "months" },
  "baseRate": 6.5,
  "categories": ["Senior Citizen", "Employee"]
}
```

4. Click **Execute**. **Expected:** code **200** and a body containing:

```json
"effectiveRate": 8.50,
"interestEarned": 8500.00,
"maturityAmount": 108500.00,
"capApplied": false
```

5. Try Sample 3 (compound quarterly):

```json
{
  "principal": 100000,
  "term": { "value": 12, "unit": "MONTHS" },
  "baseRate": 6.5,
  "categories": [],
  "interestType": "COMPOUND",
  "compoundingFrequency": "QUARTERLY"
}
```

   **Expected:** `"maturityAmount": 106660.16`, `"interestEarned": 6660.16`, `"apy": 6.66`.

6. Try an invalid body:

```json
{
  "principal": 500,
  "term": { "value": 200, "unit": "MONTHS" },
  "baseRate": 40,
  "categories": ["ALIEN"]
}
```

   **Expected:** code **400** with `"code": "VALIDATION_ERROR"` and details:
   - "Minimum deposit amount is ₹1,000."
   - "For MONTHS, the term must be between 1 and 120."
   - "Base rate cannot be more than 15% per year."

Leave the backend running and continue with the frontend.


---

## Step 8: Create the Angular frontend

**What:** create the Angular app next to the backend.

**Where:** you need a **second terminal**, because the first one is busy running the backend. Click the **+** at the top-right of the terminal panel. The new terminal starts in `banking-lab`. If it doesn't, type `cd ..` until the prompt ends with `banking-lab`.

### 8a. Generate the app

```
ng new banking-frontend --style=css --ssr=false --routing=true --zoneless=false --ai-config=none --skip-git=true
```

**What the options mean:**
- Plain CSS for styling.
- No server-side rendering.
- Page routing switched on.
- Classic change detection.
- No AI config files.
- No Git repository yet.

**Expected:** a list of `CREATE …` lines, then `✔ Packages installed successfully.` This takes 1–4 minutes.

**If you see "Unknown argument: ai-config":** run the same command without `--ai-config=none`. If it asks any question, press Enter to accept the default.

Now move into the new folder:

```
cd banking-frontend
```

### 8b. Delete the sample test file
In Explorer, right-click `banking-frontend/src/app/app.spec.ts` → **Delete**. It tests the default page, which we're replacing.

### 8c. The dev proxy
**Why:** the page runs on port 4200 but the backend is on 8082. The proxy lets the page call `/api/...` as if it were on the same port, and quietly forwards the call to 8082. (In the final cloud deployment, a gateway does the same job.)

**File:** `banking-frontend/proxy.conf.json`
**Action:** Create this file (in the `banking-frontend` folder itself, next to `package.json`)

```json
{
  "/api/fd": {
    "target": "http://localhost:8082",
    "secure": false,
    "changeOrigin": true
  },
  "/api/calculator": {
    "target": "http://localhost:8082",
    "secure": false,
    "changeOrigin": true
  },
  "/api/auth": {
    "target": "http://localhost:8082",
    "secure": false,
    "changeOrigin": true
  }
}
```

### 8d. Make `npm start` use the proxy
**File:** `banking-frontend/package.json`
**Action:** Change one line

Open the file and find this exact line (near the top, inside `"scripts"`):

```json
    "start": "ng serve",
```

Replace **only that line** with:

```json
    "start": "ng serve --proxy-config proxy.conf.json",
```

Keep the comma at the end, and save.

### 8e. Page title

**File:** `banking-frontend/src/index.html`
**Action:** Replace the entire file

```html
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>BankLab | Banking Technology Lab</title>
  <base href="/">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="icon" type="image/x-icon" href="favicon.ico">
</head>
<body>
  <app-root></app-root>
</body>
</html>
```

### 8f. The shared look (colours, cards, buttons, form fields)

**File:** `banking-frontend/src/styles.css`
**Action:** Replace the entire file

```css
/* =========================================================
   BankLab global styles - shared by every page of the app.
   Colours follow the navy/teal of your team's ERD diagrams.
   ========================================================= */
:root {
  --navy: #1f3864;
  --navy-dark: #152a4d;
  --teal: #1f7a6d;
  --teal-light: #e3f2ef;
  --bg: #f4f6fa;
  --card: #ffffff;
  --text: #1d2433;
  --muted: #5c677d;
  --border: #d6dbe6;
  --danger: #b42318;
  --danger-bg: #fdecea;
  --warning: #8a5a00;
  --warning-bg: #fff4dc;
  --success: #146c43;
  --success-bg: #e6f4ea;
  --radius: 10px;
  --shadow: 0 1px 3px rgba(16, 24, 40, 0.08), 0 1px 2px rgba(16, 24, 40, 0.04);
}

* {
  box-sizing: border-box;
}

html,
body {
  margin: 0;
  padding: 0;
}

body {
  font-family: "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  font-size: 15px;
  line-height: 1.5;
  color: var(--text);
  background: var(--bg);
}

a {
  color: var(--navy);
}

.container {
  max-width: 1140px;
  margin: 0 auto;
  padding: 24px 20px;
}

.page-header h1 {
  margin: 0 0 4px;
  font-size: 26px;
  color: var(--navy);
}

.page-header p {
  margin: 0 0 20px;
  color: var(--muted);
}

/* ---------- Cards ---------- */
.card {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 20px 22px;
}

.card-title {
  margin: 0 0 16px;
  font-size: 18px;
  color: var(--navy);
}

/* ---------- Form fields ---------- */
.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
}

.label {
  font-weight: 600;
  font-size: 14px;
}

input[type="text"],
input[type="password"],
input[type="number"],
select {
  width: 100%;
  padding: 10px 12px;
  font-size: 15px;
  font-family: inherit;
  color: var(--text);
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
}

input:focus,
select:focus {
  outline: none;
  border-color: var(--navy);
  box-shadow: 0 0 0 3px rgba(31, 56, 100, 0.15);
}

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.hint {
  margin: -8px 0 14px;
  font-size: 13px;
  color: var(--muted);
}

/* ---------- Buttons ---------- */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 18px;
  font-size: 15px;
  font-weight: 600;
  font-family: inherit;
  border-radius: 8px;
  border: 1px solid transparent;
  cursor: pointer;
  text-decoration: none;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: var(--navy);
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: var(--navy-dark);
}

.btn-secondary {
  background: #fff;
  color: var(--navy);
  border-color: var(--border);
}

.btn-secondary:hover:not(:disabled) {
  border-color: var(--navy);
}

.btn-teal {
  background: var(--teal);
  color: #fff;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
}

/* ---------- Messages ---------- */
.error-box,
.notice,
.success-box {
  border-radius: 8px;
  padding: 10px 14px;
  margin: 12px 0;
  font-size: 14px;
}

.error-box {
  background: var(--danger-bg);
  color: var(--danger);
  border: 1px solid #f5c2bd;
}

.error-box ul {
  margin: 4px 0 0;
  padding-left: 20px;
}

.notice {
  background: var(--warning-bg);
  color: var(--warning);
  border: 1px solid #f3dca5;
}

.success-box {
  background: var(--success-bg);
  color: var(--success);
  border: 1px solid #b7dfc3;
  word-break: break-word;
}

.chip {
  display: inline-block;
  padding: 3px 10px;
  margin: 0 6px 6px 0;
  font-size: 13px;
  font-weight: 600;
  border-radius: 999px;
  background: var(--teal-light);
  color: var(--teal);
}

.muted {
  color: var(--muted);
}
```

**What it does:** the design system for the whole app.
- Navy and teal colours match your team's ERD diagrams.
- It defines reusable `card`, `btn`, `field`, `error-box` and `chip` styles, so every page, including future modules, looks the same.

---

## Step 9: The calculator page

In Explorer, right-click `banking-frontend/src/app` → **New Folder…** → `calculator`. Create the five files below inside it.

**File:** `banking-frontend/src/app/calculator/fd.models.ts`
**Action:** Create this file

```typescript
/**
 * TypeScript "shapes" of the data exchanged with calculator-service.
 * They mirror the Java classes in calculator-service/src/main/java/com/banklab/calculator/dto.
 */
export type TermUnit = 'DAYS' | 'MONTHS' | 'YEARS';
export type InterestType = 'SIMPLE' | 'COMPOUND';
export type CompoundingFrequency = 'MONTHLY' | 'QUARTERLY' | 'HALF_YEARLY' | 'ANNUALLY';

export interface Term {
  value: number;
  unit: TermUnit;
}

/** Sent to POST /api/fd/calculate (Lab L6 payload + optional extras). */
export interface FdCalculationRequest {
  principal: number;
  term: Term;
  baseRate: number | null;
  categories: string[];
  interestType: InterestType;
  compoundingFrequency: CompoundingFrequency | null;
  productCode: string | null;
}

/** Received from POST /api/fd/calculate. */
export interface FdCalculationResponse {
  principal: number;
  termValue: number;
  termUnit: TermUnit;
  interestType: InterestType;
  compoundingFrequency: CompoundingFrequency | null;
  calculationMethod: string;
  productCode: string | null;
  baseRate: number;
  categories: string[];
  categoryAddOns: Record<string, number>;
  requestedAddOn: number;
  appliedAddOn: number;
  addOnCap: number;
  capApplied: boolean;
  maxEffectiveRate: number | null;
  maxRateCapApplied: boolean;
  effectiveRate: number;
  interestEarned: number;
  maturityAmount: number;
  apy: number | null;
  startDate: string;
  maturityDate: string;
  currencyCode: string;
  rateSource: string;
}

export interface CategoryInfo {
  code: string;
  name: string;
  additionalRatePercent: number | null;
}

/** Received from GET /api/fd/categories. */
export interface CategoryListResponse {
  rateSource: string;
  categorySource: string;
  addOnCap: number;
  categories: CategoryInfo[];
}

export interface ProductInfo {
  code: string;
  name: string;
  minRate: number | null;
  maxRate: number | null;
  minAmount: number | null;
  maxAmount: number | null;
  defaultCompoundingFrequency: string | null;
}

export interface ReportResult {
  fileName: string;
  csvRow: string;
  rowsInFile: number;
}

export interface FdReportResponse {
  calculation: FdCalculationResponse;
  report: ReportResult;
}

/** Every error from the backend has this shape. */
export interface ApiError {
  code: string;
  message: string;
  details: string[];
}
```

**What it does:** TypeScript versions of the backend's DTOs, so the editor warns you if a field name is misspelled.

**File:** `banking-frontend/src/app/calculator/fd-calculator.service.ts`
**Action:** Create this file

```typescript
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ApiError,
  CategoryListResponse,
  FdCalculationRequest,
  FdCalculationResponse,
  FdReportResponse,
  ProductInfo,
} from './fd.models';

/**
 * All HTTP calls from the Angular app to calculator-service.
 * Addresses start with /api/... ; the dev proxy (proxy.conf.json) forwards
 * them to http://localhost:8082.
 */
@Injectable({ providedIn: 'root' })
export class FdCalculatorService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/fd';

  getCategories(productCode?: string | null): Observable<CategoryListResponse> {
    let params = new HttpParams();
    if (productCode) {
      params = params.set('productCode', productCode);
    }
    return this.http.get<CategoryListResponse>(`${this.baseUrl}/categories`, { params });
  }

  getProducts(): Observable<ProductInfo[]> {
    return this.http.get<ProductInfo[]>(`${this.baseUrl}/products`);
  }

  getMyCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/my-categories`);
  }

  calculate(request: FdCalculationRequest): Observable<FdCalculationResponse> {
    return this.http.post<FdCalculationResponse>(`${this.baseUrl}/calculate`, request);
  }

  addToReport(request: FdCalculationRequest): Observable<FdReportResponse> {
    return this.http.post<FdReportResponse>(`${this.baseUrl}/report`, request);
  }

  downloadReport(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/report/download`, { responseType: 'blob' });
  }
}

/** Turns any HTTP error into a list of friendly messages for the page. */
export function describeHttpError(err: unknown): string[] {
  if (err instanceof HttpErrorResponse) {
    const body = err.error as Partial<ApiError> | null;
    if (body && typeof body === 'object' && typeof body.message === 'string') {
      const details = Array.isArray(body.details) ? body.details.filter((d) => !!d) : [];
      return details.length > 0 ? details : [body.message];
    }
    if (err.status === 0 || err.status === 500 || err.status === 502 || err.status === 504) {
      return ['Cannot reach the FD Calculator service. Is the backend running on port 8082?'];
    }
    if (err.status === 401) {
      return ['Please log in first.'];
    }
    if (err.status === 403) {
      return ['Access denied. The FD Calculator is only for customers.'];
    }
    return [`Unexpected error (HTTP ${err.status}).`];
  }
  return ['Unexpected error. Please try again.'];
}
```

**What it does:**
- One function per backend door: categories, products, my categories, calculate, add to report, download report.
- `describeHttpError` turns any failure into a readable message. For example, if the backend isn't running you get "Cannot reach the FD Calculator service…".

**File:** `banking-frontend/src/app/calculator/fd-calculator.ts`
**Action:** Create this file

```typescript
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FdCalculatorService, describeHttpError } from './fd-calculator.service';
import {
  CategoryInfo,
  CompoundingFrequency,
  FdCalculationRequest,
  FdCalculationResponse,
  InterestType,
  ProductInfo,
  TermUnit,
} from './fd.models';

/** Values typed into the form. */
interface FormModel {
  principal: number | null;
  termValue: number | null;
  termUnit: TermUnit;
  baseRate: number | null;
  interestType: InterestType;
  compoundingFrequency: CompoundingFrequency;
  productCode: string;
}

const TERM_LIMITS: Record<TermUnit, { min: number; max: number }> = {
  DAYS: { min: 7, max: 3650 },
  MONTHS: { min: 1, max: 120 },
  YEARS: { min: 1, max: 10 },
};

function defaultModel(): FormModel {
  return {
    principal: null,
    termValue: 12,
    termUnit: 'MONTHS',
    baseRate: 6.5,
    interestType: 'COMPOUND',
    compoundingFrequency: 'QUARTERLY',
    productCode: '',
  };
}

/**
 * The FD Calculator page (Lab L6 section 4 "UI Integration"):
 *  - category dropdown fetched from the backend's rate table
 *  - shows the additional % of every selected category
 *  - shows the cap being applied BEFORE the form is submitted
 */
@Component({
  selector: 'calc-fd-calculator',
  imports: [FormsModule, DatePipe],
  templateUrl: './fd-calculator.html',
  styleUrl: './fd-calculator.css',
})
export class FdCalculator implements OnInit {
  private readonly api = inject(FdCalculatorService);

  model: FormModel = defaultModel();

  // Data loaded from the backend
  readonly categories = signal<CategoryInfo[]>([]);
  readonly products = signal<ProductInfo[]>([]);
  readonly myCategories = signal<string[]>([]);
  readonly addOnCap = signal(2);
  readonly rateSource = signal('LOCAL');
  readonly categorySource = signal('REQUEST');

  // Page state
  readonly selected = signal<string[]>([]);
  readonly loadingSetup = signal(true);
  readonly setupErrors = signal<string[]>([]);
  readonly calculating = signal(false);
  readonly errors = signal<string[]>([]);
  readonly result = signal<FdCalculationResponse | null>(null);
  readonly reportBusy = signal(false);
  readonly reportMessage = signal<string | null>(null);
  readonly reportError = signal<string | null>(null);

  /** Categories that will be used: ticked ones, or the customer's own in PROFILE mode. */
  readonly activeCategories = computed(() =>
    this.categorySource() === 'PROFILE' ? this.myCategories() : this.selected(),
  );

  /** Sum of the add-ons of the active categories, before the cap. */
  readonly requestedAddOn = computed(() =>
    this.activeCategories().reduce((sum, code) => sum + (this.addOnOf(code) ?? 0), 0),
  );

  readonly capExceeded = computed(() => this.requestedAddOn() > this.addOnCap() + 1e-9);

  readonly appliedAddOn = computed(() => Math.min(this.requestedAddOn(), this.addOnCap()));

  ngOnInit(): void {
    this.loadSetup();
  }

  /** Loads categories (and products / my categories when those modes are switched on). */
  loadSetup(): void {
    this.loadingSetup.set(true);
    this.setupErrors.set([]);
    this.api.getCategories().subscribe({
      next: (res) => {
        this.categories.set(res.categories);
        this.addOnCap.set(Number(res.addOnCap));
        this.rateSource.set(res.rateSource);
        this.categorySource.set(res.categorySource);
        if (res.rateSource === 'PRODUCT') {
          this.loadProducts();
        }
        if (res.categorySource === 'PROFILE') {
          this.loadMyCategories();
        }
        this.loadingSetup.set(false);
      },
      error: (err) => {
        this.setupErrors.set(describeHttpError(err));
        this.loadingSetup.set(false);
      },
    });
  }

  private loadProducts(): void {
    this.api.getProducts().subscribe({
      next: (list) => this.products.set(list),
      error: (err) => this.setupErrors.set(describeHttpError(err)),
    });
  }

  private loadMyCategories(): void {
    this.api.getMyCategories().subscribe({
      next: (list) => this.myCategories.set(list),
      error: (err) => this.setupErrors.set(describeHttpError(err)),
    });
  }

  /** When a product is chosen, reload categories so the add-ons of that product are shown. */
  onProductChange(): void {
    this.result.set(null);
    this.api.getCategories(this.model.productCode || null).subscribe({
      next: (res) => this.categories.set(res.categories),
      error: (err) => this.setupErrors.set(describeHttpError(err)),
    });
  }

  // ---------- Category helpers ----------

  addOnOf(code: string): number | null {
    const info = this.categories().find((c) => c.code === code);
    return info && info.additionalRatePercent !== null ? Number(info.additionalRatePercent) : null;
  }

  nameOf(code: string): string {
    return this.categories().find((c) => c.code === code)?.name ?? code;
  }

  isSelected(code: string): boolean {
    return this.selected().includes(code);
  }

  toggleCategory(code: string): void {
    this.selected.update((list) =>
      list.includes(code) ? list.filter((c) => c !== code) : [...list, code],
    );
  }

  selectionSummary(): string {
    const list = this.selected();
    if (list.length === 0) {
      return 'No category selected (General customer)';
    }
    return list.map((c) => this.nameOf(c)).join(', ');
  }

  addOnLabel(value: number | null): string {
    return value === null ? 'depends on product' : '+' + Number(value).toFixed(2) + '%';
  }

  // ---------- Preview (before submitting) ----------

  previewRate(): number | null {
    if (this.rateSource() !== 'LOCAL' || this.model.baseRate === null) {
      return null;
    }
    return Number(this.model.baseRate) + this.appliedAddOn();
  }

  termHint(): string {
    const limits = TERM_LIMITS[this.model.termUnit];
    return `Allowed: ${limits.min} to ${limits.max} ${this.model.termUnit.toLowerCase()}.`;
  }

  // ---------- Validation (same rules as the backend) ----------

  private validate(): string[] {
    const e: string[] = [];
    const m = this.model;

    if (m.principal === null || m.principal === undefined) {
      e.push('Please enter the deposit amount.');
    } else if (m.principal <= 0) {
      e.push('Deposit amount must be greater than 0.');
    } else if (m.principal < 1000) {
      e.push('Minimum deposit amount is ₹1,000.');
    } else if (m.principal > 100000000) {
      e.push('Maximum deposit amount is ₹10,00,00,000 (10 crore).');
    } else if (!/^\d+(\.\d{1,2})?$/.test(String(m.principal))) {
      e.push('Deposit amount can have at most 2 decimal places (paise).');
    }

    const limits = TERM_LIMITS[m.termUnit];
    if (m.termValue === null || m.termValue === undefined) {
      e.push('Please enter the term.');
    } else if (!Number.isInteger(m.termValue)) {
      e.push('Term must be a whole number.');
    } else if (m.termValue < limits.min || m.termValue > limits.max) {
      e.push(`For ${m.termUnit}, the term must be between ${limits.min} and ${limits.max}.`);
    }

    if (this.rateSource() === 'LOCAL') {
      if (m.baseRate === null || m.baseRate === undefined) {
        e.push('Please enter the base rate.');
      } else if (m.baseRate <= 0) {
        e.push('Base rate must be greater than 0.');
      } else if (m.baseRate > 15) {
        e.push('Base rate cannot be more than 15% per year.');
      } else if (!/^\d+(\.\d{1,2})?$/.test(String(m.baseRate))) {
        e.push('Base rate can have at most 2 decimal places.');
      }
    } else if (!m.productCode) {
      e.push('Please choose an FD product.');
    }
    return e;
  }

  private buildRequest(): FdCalculationRequest {
    const m = this.model;
    return {
      principal: Number(m.principal),
      term: { value: Number(m.termValue), unit: m.termUnit },
      baseRate: this.rateSource() === 'LOCAL' ? Number(m.baseRate) : null,
      categories: this.categorySource() === 'PROFILE' ? [] : this.selected(),
      interestType: m.interestType,
      compoundingFrequency: m.interestType === 'COMPOUND' ? m.compoundingFrequency : null,
      productCode: this.rateSource() === 'PRODUCT' ? m.productCode : null,
    };
  }

  // ---------- Buttons ----------

  calculate(): void {
    this.reportMessage.set(null);
    this.reportError.set(null);
    const problems = this.validate();
    this.errors.set(problems);
    if (problems.length > 0) {
      this.result.set(null);
      return;
    }
    this.calculating.set(true);
    this.api.calculate(this.buildRequest()).subscribe({
      next: (res) => {
        this.result.set(res);
        this.calculating.set(false);
      },
      error: (err) => {
        this.result.set(null);
        this.errors.set(describeHttpError(err));
        this.calculating.set(false);
      },
    });
  }

  addToReport(): void {
    this.reportMessage.set(null);
    this.reportError.set(null);
    this.reportBusy.set(true);
    this.api.addToReport(this.buildRequest()).subscribe({
      next: (res) => {
        this.reportMessage.set(
          `Row ${res.report.rowsInFile} added to ${res.report.fileName}: ${res.report.csvRow}`,
        );
        this.reportBusy.set(false);
      },
      error: (err) => {
        this.reportError.set(describeHttpError(err).join(' '));
        this.reportBusy.set(false);
      },
    });
  }

  downloadReport(): void {
    this.reportError.set(null);
    this.api.downloadReport().subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'fd_report.csv';
        link.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => {
        if (err instanceof HttpErrorResponse && err.status === 404) {
          this.reportError.set("No report yet. Calculate first, then click 'Add to report'.");
        } else {
          this.reportError.set(describeHttpError(err).join(' '));
        }
      },
    });
  }

  reset(): void {
    this.model = defaultModel();
    this.selected.set([]);
    this.errors.set([]);
    this.result.set(null);
    this.reportMessage.set(null);
    this.reportError.set(null);
  }

  // ---------- Display helpers ----------

  inr(value: number | null | undefined): string {
    if (value === null || value === undefined) {
      return '-';
    }
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(Number(value));
  }

  pct(value: number | null | undefined): string {
    return value === null || value === undefined ? '-' : Number(value).toFixed(2) + '%';
  }

  termText(r: FdCalculationResponse): string {
    return `${r.termValue} ${r.termUnit.toLowerCase()}`;
  }
}
```

**What it does:** the page's brain.
- Loads the categories when the page opens.
- Keeps track of your selections, and live-computes the add-on total and the cap preview.
- Validates the form using the same rules as the backend.
- Sends the request, then shows the result or the errors.

Parts for product mode and profile mode stay switched off until Phase C turns them on from the backend.

**File:** `banking-frontend/src/app/calculator/fd-calculator.html`
**Action:** Create this file

```html
<div class="container">
  <div class="page-header">
    <h1>FD Calculator</h1>
    <p>Estimate the maturity amount of a Fixed Deposit using simple or compound interest.</p>
  </div>

  @if (loadingSetup()) {
    <p class="muted">Loading categories...</p>
  }

  @if (setupErrors().length > 0) {
    <div class="error-box">
      <strong>Could not load the calculator settings.</strong>
      <ul>
        @for (e of setupErrors(); track e) {
          <li>{{ e }}</li>
        }
      </ul>
      <button type="button" class="btn btn-secondary" (click)="loadSetup()">Try again</button>
    </div>
  }

  <div class="layout">
    <!-- ================= LEFT: THE FORM ================= -->
    <form class="card" (ngSubmit)="calculate()" novalidate>
      <h2 class="card-title">Deposit details</h2>

      @if (rateSource() === 'PRODUCT') {
        <label class="field">
          <span class="label">FD product</span>
          <select name="productCode" [(ngModel)]="model.productCode" (ngModelChange)="onProductChange()">
            <option value="">-- Choose a product --</option>
            @for (p of products(); track p.code) {
              <option [value]="p.code">{{ p.name }} ({{ p.code }})</option>
            }
          </select>
        </label>
      }

      <label class="field">
        <span class="label">Deposit amount (₹)</span>
        <input type="number" name="principal" [(ngModel)]="model.principal"
               min="1000" step="0.01" placeholder="e.g. 100000" />
      </label>

      <div class="row">
        <label class="field">
          <span class="label">Term</span>
          <input type="number" name="termValue" [(ngModel)]="model.termValue" min="1" step="1" />
        </label>
        <label class="field">
          <span class="label">Term unit</span>
          <select name="termUnit" [(ngModel)]="model.termUnit">
            <option value="DAYS">Days</option>
            <option value="MONTHS">Months</option>
            <option value="YEARS">Years</option>
          </select>
        </label>
      </div>
      <p class="hint">{{ termHint() }}</p>

      @if (rateSource() === 'LOCAL') {
        <label class="field">
          <span class="label">Base interest rate (% per year)</span>
          <input type="number" name="baseRate" [(ngModel)]="model.baseRate"
                 min="0.01" max="15" step="0.01" placeholder="e.g. 6.5" />
        </label>
      } @else {
        <p class="hint">The base rate comes from the selected product.</p>
      }

      <div class="field">
        <span class="label">Interest type</span>
        <div class="radios">
          <label class="radio">
            <input type="radio" name="interestType" value="SIMPLE" [(ngModel)]="model.interestType" />
            Simple
          </label>
          <label class="radio">
            <input type="radio" name="interestType" value="COMPOUND" [(ngModel)]="model.interestType" />
            Compound
          </label>
        </div>
      </div>

      @if (model.interestType === 'COMPOUND') {
        <label class="field">
          <span class="label">Compounding frequency</span>
          <select name="compoundingFrequency" [(ngModel)]="model.compoundingFrequency">
            <option value="MONTHLY">Monthly</option>
            <option value="QUARTERLY">Quarterly</option>
            <option value="HALF_YEARLY">Half-yearly</option>
            <option value="ANNUALLY">Annually</option>
          </select>
        </label>
      }

      <!-- ---------- Customer categories (Lab L6 section 4) ---------- -->
      <div class="field">
        <span class="label">Customer category</span>
        @if (categorySource() === 'PROFILE') {
          <p class="hint profile-hint">Taken automatically from your customer profile:</p>
          <div>
            @for (code of myCategories(); track code) {
              <span class="chip">{{ nameOf(code) }} {{ addOnLabel(addOnOf(code)) }}</span>
            }
          </div>
        } @else {
          <details class="dropdown">
            <summary>{{ selectionSummary() }}</summary>
            <div class="dropdown-panel">
              @for (c of categories(); track c.code) {
                <label class="check">
                  <input type="checkbox" [checked]="isSelected(c.code)" (change)="toggleCategory(c.code)" />
                  <span class="check-name">{{ c.name }}</span>
                  <span class="check-addon">{{ addOnLabel(c.additionalRatePercent) }}</span>
                </label>
              }
            </div>
          </details>
        }
      </div>

      <!-- ---------- Cap preview (enforced before submission) ---------- -->
      <div class="cap-box" [class.cap-exceeded]="capExceeded()">
        <div>Selected add-ons: <strong>+{{ requestedAddOn().toFixed(2) }}%</strong></div>
        <div>Maximum combined add-on allowed: <strong>+{{ addOnCap().toFixed(2) }}%</strong></div>
        @if (capExceeded()) {
          <div class="cap-warning">
            Your add-ons exceed the cap, so only +{{ appliedAddOn().toFixed(2) }}% will be applied.
          </div>
        }
        @if (previewRate() !== null) {
          <div>Estimated effective rate: <strong>{{ pct(previewRate()) }}</strong></div>
        }
      </div>

      @if (errors().length > 0) {
        <div class="error-box">
          <strong>Please fix the following:</strong>
          <ul>
            @for (e of errors(); track e) {
              <li>{{ e }}</li>
            }
          </ul>
        </div>
      }

      <div class="actions">
        <button type="submit" class="btn btn-primary" [disabled]="calculating()">
          {{ calculating() ? 'Calculating...' : 'Calculate' }}
        </button>
        <button type="button" class="btn btn-secondary" (click)="reset()">Reset</button>
      </div>
    </form>

    <!-- ================= RIGHT: THE RESULT ================= -->
    @if (result(); as r) {
      <section class="card result">
        <h2 class="card-title">Result</h2>

        <div class="maturity">
          <span>Maturity amount</span>
          <strong>{{ inr(r.maturityAmount) }}</strong>
        </div>

        <div class="stats">
          <div class="stat">
            <span>Interest earned</span>
            <strong>{{ inr(r.interestEarned) }}</strong>
          </div>
          <div class="stat">
            <span>Effective rate</span>
            <strong>{{ pct(r.effectiveRate) }}</strong>
          </div>
          @if (r.apy !== null) {
            <div class="stat">
              <span>Annual yield (APY)</span>
              <strong>{{ pct(r.apy) }}</strong>
            </div>
          }
        </div>

        @if (r.capApplied) {
          <p class="notice">
            Your category add-ons total +{{ pct(r.requestedAddOn) }}, but the maximum allowed is
            +{{ pct(r.addOnCap) }}, so +{{ pct(r.appliedAddOn) }} was applied.
          </p>
        }
        @if (r.maxRateCapApplied) {
          <p class="notice">
            The rate was limited to the product's maximum rate of {{ pct(r.maxEffectiveRate) }}.
          </p>
        }

        <table class="breakdown">
          <tbody>
            <tr><th>Deposit amount</th><td>{{ inr(r.principal) }}</td></tr>
            <tr><th>Term</th><td>{{ termText(r) }}</td></tr>
            <tr>
              <th>Interest method</th>
              <td>
                {{ r.interestType === 'SIMPLE' ? 'Simple interest' : 'Compound interest' }}
                @if (r.compoundingFrequency) {
                  ({{ r.compoundingFrequency.toLowerCase().replace('_', '-') }})
                }
              </td>
            </tr>
            <tr><th>Base rate</th><td>{{ pct(r.baseRate) }}</td></tr>
            <tr>
              <th>Categories</th>
              <td>
                @for (code of r.categories; track code) {
                  <span class="chip">{{ nameOf(code) }} +{{ pct(r.categoryAddOns[code]) }}</span>
                } @empty {
                  <span class="muted">None</span>
                }
              </td>
            </tr>
            <tr><th>Add-on applied</th><td>+{{ pct(r.appliedAddOn) }}</td></tr>
            <tr><th>Effective rate</th><td>{{ pct(r.effectiveRate) }}</td></tr>
            <tr><th>Start date</th><td>{{ r.startDate | date: 'dd MMM yyyy' }}</td></tr>
            <tr><th>Maturity date</th><td>{{ r.maturityDate | date: 'dd MMM yyyy' }}</td></tr>
            <tr><th>Rate source</th><td>{{ r.rateSource }}</td></tr>
          </tbody>
        </table>

        <div class="actions">
          <button type="button" class="btn btn-teal" (click)="addToReport()" [disabled]="reportBusy()">
            {{ reportBusy() ? 'Adding...' : 'Add to report (CSV)' }}
          </button>
          <button type="button" class="btn btn-secondary" (click)="downloadReport()">
            Download my report
          </button>
        </div>
        @if (reportMessage()) {
          <p class="success-box">{{ reportMessage() }}</p>
        }
        @if (reportError()) {
          <p class="error-box">{{ reportError() }}</p>
        }
      </section>
    } @else {
      <section class="card placeholder">
        <h2 class="card-title">Result</h2>
        <p class="muted">Enter the deposit details and click <strong>Calculate</strong>.</p>
        <p class="muted small">
          Simple interest: M = P + (P × r × t / 100)<br />
          Compound interest: M = P × (1 + r/n)<sup>n×t</sup>
        </p>
      </section>
    }
  </div>
</div>
```

**What it does:** the page layout. The form is on the left and the result on the right, stacked on phones.

The **category dropdown** is a real dropdown (`<details>`) holding checkboxes, because the manual's example combines two categories and a normal dropdown allows only one. Each option shows its add-on %. The teal box shows the cap *before* you submit, which is the manual's "enforce cap logic before submission".

**File:** `banking-frontend/src/app/calculator/fd-calculator.css`
**Action:** Create this file

```css
.layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 20px;
  align-items: start;
}

@media (max-width: 900px) {
  .layout {
    grid-template-columns: 1fr;
  }
}

.radios {
  display: flex;
  gap: 20px;
}

.radio {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

/* Category dropdown (a <details> element that opens a list of checkboxes) */
.dropdown {
  border: 1px solid var(--border);
  border-radius: 8px;
  background: #fff;
}

.dropdown summary {
  padding: 10px 12px;
  cursor: pointer;
  list-style: none;
}

.dropdown summary::after {
  content: "▾";
  float: right;
  color: var(--muted);
}

.dropdown[open] summary {
  border-bottom: 1px solid var(--border);
}

.dropdown-panel {
  padding: 6px 12px;
}

.check {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  cursor: pointer;
}

.check-name {
  flex: 1;
}

.check-addon {
  font-weight: 600;
  color: var(--teal);
}

.profile-hint {
  margin: 0;
}

.cap-box {
  background: var(--teal-light);
  border: 1px solid #b9ded7;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 14px;
  margin-bottom: 8px;
}

.cap-box.cap-exceeded {
  background: var(--warning-bg);
  border-color: #f3dca5;
}

.cap-warning {
  font-weight: 600;
  color: var(--warning);
}

.maturity {
  display: flex;
  flex-direction: column;
  padding: 16px;
  margin-bottom: 14px;
  border-radius: 8px;
  background: var(--navy);
  color: #fff;
}

.maturity span {
  font-size: 14px;
  opacity: 0.85;
}

.maturity strong {
  font-size: 30px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 10px;
  margin-bottom: 8px;
}

.stat {
  display: flex;
  flex-direction: column;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 8px;
}

.stat span {
  font-size: 13px;
  color: var(--muted);
}

.stat strong {
  font-size: 18px;
  color: var(--navy);
}

.breakdown {
  width: 100%;
  border-collapse: collapse;
  margin: 10px 0 12px;
  font-size: 14px;
}

.breakdown th,
.breakdown td {
  padding: 7px 4px;
  border-bottom: 1px solid var(--border);
  text-align: left;
  vertical-align: top;
}

.breakdown th {
  width: 42%;
  color: var(--muted);
  font-weight: 600;
}

.placeholder .small {
  font-size: 13px;
}
```

---

## Step 10: Home page, navigation bar, routes — then run everything

In `banking-frontend/src/app` create a folder `home`, and inside it the three files below.

**File:** `banking-frontend/src/app/home/home.ts`
**Action:** Create this file

```typescript
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

/** Dashboard / landing page with a card for each module. */
@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly route = inject(ActivatedRoute);

  /** True when the user was sent here because their role may not open a page. */
  readonly denied = this.route.snapshot.queryParamMap.has('denied');
}
```

**File:** `banking-frontend/src/app/home/home.html`
**Action:** Create this file

```html
<div class="container">
  <div class="page-header">
    <h1>Welcome to BankLab</h1>
    <p>Banking Technology Lab (CSF 3142) - integrated banking application.</p>
  </div>

  @if (denied) {
    <div class="error-box">
      Access denied. The FD Calculator is only available to customers (CUSTOMER role).
    </div>
  }

  <div class="modules">
    <a class="card module" routerLink="/fd-calculator">
      <span class="module-tag">Group 6</span>
      <h2>FD Calculator</h2>
      <p>Estimate your Fixed Deposit maturity amount with simple or compound interest and
        category benefits.</p>
      <span class="module-link">Open calculator →</span>
    </a>

    <div class="card module disabled">
      <span class="module-tag">Group 6</span>
      <h2>Product &amp; Pricing</h2>
      <p>FD products, rate matrix and fees - built by the product-service team.</p>
      <span class="muted">Joins this app when ready</span>
    </div>

    <div class="card module disabled">
      <span class="module-tag">Group 2</span>
      <h2>FD Accounts</h2>
      <p>Open and manage Fixed Deposit accounts - built by the FD module team.</p>
      <span class="muted">Joins this app when ready</span>
    </div>
  </div>
</div>
```

**File:** `banking-frontend/src/app/home/home.css`
**Action:** Create this file

```css
.modules {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
}

.module {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: inherit;
  text-decoration: none;
  transition: border-color 0.15s, transform 0.15s;
}

.module h2 {
  margin: 0;
  font-size: 19px;
  color: var(--navy);
}

.module p {
  margin: 0 0 6px;
  color: var(--muted);
}

a.module:hover {
  border-color: var(--navy);
  transform: translateY(-2px);
}

.module.disabled {
  opacity: 0.7;
}

.module-tag {
  align-self: flex-start;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 999px;
  background: var(--teal-light);
  color: var(--teal);
}

.module-link {
  font-weight: 600;
  color: var(--navy);
}
```

**What they do:** a dashboard with a card per module. FD Calculator is active; the other modules show as "joins when ready". It also shows an "Access denied" message when Phase C sends a non-customer here.

Now the four app-level files. These already exist, so replace them.

**File:** `banking-frontend/src/app/app.config.ts`
**Action:** Replace the entire file

```typescript
import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // Lets the app make HTTP calls to the backend
    provideHttpClient(),
  ],
};
```

**What it does:** app-wide setup. The new line `provideHttpClient()` lets the app talk to the backend.

**File:** `banking-frontend/src/app/app.routes.ts`
**Action:** Replace the entire file

```typescript
import { Routes } from '@angular/router';
import { FdCalculator } from './calculator/fd-calculator';
import { Home } from './home/home';

/** Which page is shown for which address. */
export const routes: Routes = [
  { path: '', component: Home, title: 'Home | BankLab' },
  { path: 'fd-calculator', component: FdCalculator, title: 'FD Calculator | BankLab' },
  { path: '**', redirectTo: '' },
];
```

**What it does:** address → page. `/` shows Home, `/fd-calculator` shows the calculator, and anything else goes to Home.

**File:** `banking-frontend/src/app/app.ts`
**Action:** Replace the entire file

```typescript
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

/** The frame around every page: top navigation bar + footer. */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
```

**File:** `banking-frontend/src/app/app.html`
**Action:** Replace the entire file

```html
<header class="navbar">
  <div class="navbar-inner">
    <a routerLink="/" class="brand">
      <span class="brand-logo">₹</span>
      <span>BankLab</span>
    </a>
    <nav class="nav-links">
      <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Home</a>
      <a routerLink="/fd-calculator" routerLinkActive="active">FD Calculator</a>
    </nav>
  </div>
</header>

<main>
  <router-outlet />
</main>

<footer class="footer">
  Banking Technology Lab (CSF 3142) · Group 6 · FD Calculator Module
</footer>
```

**File:** `banking-frontend/src/app/app.css`
**Action:** Replace the entire file

```css
.navbar {
  background: var(--navy);
  color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
}

.navbar-inner {
  max-width: 1140px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 28px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  text-decoration: none;
}

.brand-logo {
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  background: var(--teal);
}

.nav-links {
  display: flex;
  gap: 6px;
  flex: 1;
}

.nav-links a {
  color: #dbe3f3;
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 6px;
  font-weight: 600;
}

.nav-links a:hover,
.nav-links a.active {
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
}

.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.user-area .role {
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.15);
  font-size: 12px;
  font-weight: 600;
}

.nav-button {
  padding: 6px 12px;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  color: var(--navy);
  background: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  text-decoration: none;
}

main {
  min-height: calc(100vh - 110px);
}

.footer {
  text-align: center;
  padding: 16px;
  font-size: 13px;
  color: var(--muted);
}
```

**What they do:** the frame around every page, with the navy top bar, links and footer. `<router-outlet />` is where the current page appears. `app.css` already contains styles for the login area used in Phase C.

### 10a. Run the frontend
Check the **backend is still running** in terminal 1. If not, restart it with the command from Step 7a.
In **terminal 2**, inside `banking-frontend`:

```
npm start
```

**Expected** after about 10–20 seconds:

```
Application bundle generation complete.
  ➜  Local:   http://localhost:4200/
```

(Always start the frontend with `npm start`, not `ng serve`, so the proxy is used.)

### 10b. Test in the browser
1. Open **http://localhost:4200**. You should see the navy BankLab bar and the module cards.
2. Click **FD Calculator** in the top bar.
3. Click the category box ("No category selected…"). It opens with General, Senior Citizen +1.00%, Bank Employee +1.00%, Premium User +2.00%, Super Senior Citizen (80+) +1.50%.
4. **Sample 1:**
   1. Enter amount `100000`, term `12`, unit **Months**, base rate `6.5`, interest type **Simple**.
   2. Tick **Senior Citizen** and **Bank Employee**. The teal box shows "Selected add-ons +2.00%" and "Estimated effective rate 8.50%".
   3. Click **Calculate**.
   4. **Expected:** Maturity amount **₹1,08,500.00**, interest **₹8,500.00**, effective rate **8.50%**.
5. **Sample 2:** untick Senior, tick **Premium User**. The box turns yellow: "Your add-ons exceed the cap, so only +2.00% will be applied". Click **Calculate** → **₹1,08,500.00** with a yellow notice explaining the cap.
6. **Sample 3:** click **Reset**, then enter `100000`, `12` Months, `6.5`, **Compound**, **Quarterly**, no categories → **₹1,06,660.16**, APY **6.66%**.
7. **Invalid input:** clear the amount and click **Calculate** → a red box "Please enter the deposit amount." Then enter `-5` → "Deposit amount must be greater than 0."
8. **Backend down:** stop the backend (terminal 1, `Ctrl+C`), refresh the page → "Cannot reach the FD Calculator service. Is the backend running on port 8082?". Start the backend again.

**Phase A is complete: this is your Lab L6 deliverable.** The "Add to report" button needs Phase B; clicking it now shows an error.


---

# Phase B — Testing and Python report (Lab L11)

## Step 11: Automatic tests (JUnit)

**What:** a test file that runs all 7 sample calculations, the L11 scenario table and the invalid-input cases automatically.
**Why:** L11 is literally "FD Calculator Testing". Automatic tests prove the maths is right, and they're strong evidence in your record and viva.

In Explorer, go to `calculator-service/src/test/java/com/banklab/calculator`, create a folder `service`, and inside it:

**File:** `calculator-service/src/test/java/com/banklab/calculator/service/FdCalculatorServiceTest.java`
**Action:** Create this file

```java
package com.banklab.calculator.service;

import com.banklab.calculator.dto.FdCalculationRequest;
import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.Term;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.rates.LocalRateProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Lab L11 "FD Calculator Testing": every expected value below was worked out by hand
 * (see the Sample Calculations table in the guide). If the formulas ever change by
 * even one paisa, these tests fail.
 */
class FdCalculatorServiceTest {

    private final FdCalculatorService service =
            new FdCalculatorService(new LocalRateProvider(new BigDecimal("2.00")));

    private static FdCalculationRequest request(String principal, int termValue, String termUnit,
                                                String baseRate, String interestType,
                                                String frequency, String... categories) {
        FdCalculationRequest r = new FdCalculationRequest();
        r.setPrincipal(principal == null ? null : new BigDecimal(principal));
        r.setTerm(new Term(termValue, termUnit));
        r.setBaseRate(baseRate == null ? null : new BigDecimal(baseRate));
        r.setInterestType(interestType);
        r.setCompoundingFrequency(frequency);
        r.setCategories(Arrays.asList(categories));
        return r;
    }

    // ---------- The 7 sample calculations from the guide ----------

    @Test
    void sample1_simple_seniorPlusEmployee_matchesManualL11CsvRow() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, "SENIOR", "EMPLOYEE"));
        assertThat(r.effectiveRate()).isEqualByComparingTo("8.50");
        assertThat(r.interestEarned()).isEqualByComparingTo("8500.00");
        assertThat(r.maturityAmount()).isEqualByComparingTo("108500.00");
        assertThat(r.capApplied()).isFalse();
    }

    @Test
    void sample2_simple_employeePlusPremium_isCappedAtTwoPercent() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, "EMPLOYEE", "PREMIUM_USER"));
        assertThat(r.requestedAddOn()).isEqualByComparingTo("3.00");
        assertThat(r.appliedAddOn()).isEqualByComparingTo("2.00");
        assertThat(r.capApplied()).isTrue();
        assertThat(r.effectiveRate()).isEqualByComparingTo("8.50");
        assertThat(r.maturityAmount()).isEqualByComparingTo("108500.00");
    }

    @Test
    void sample3_compoundQuarterly_noCategory() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "COMPOUND", "QUARTERLY"));
        assertThat(r.interestEarned()).isEqualByComparingTo("6660.16");
        assertThat(r.maturityAmount()).isEqualByComparingTo("106660.16");
        assertThat(r.apy()).isEqualByComparingTo("6.66");
    }

    @Test
    void sample4_compoundMonthly_senior() {
        FdCalculationResponse r = service.calculate(
                request("50000", 24, "MONTHS", "7.0", "COMPOUND", "MONTHLY", "SENIOR"));
        assertThat(r.effectiveRate()).isEqualByComparingTo("8.00");
        assertThat(r.interestEarned()).isEqualByComparingTo("8644.40");
        assertThat(r.maturityAmount()).isEqualByComparingTo("58644.40");
    }

    @Test
    void sample5_oneMonthMonthly_matchesManualL14Example() {
        FdCalculationResponse r = service.calculate(
                request("100000", 1, "MONTHS", "6.6", "COMPOUND", "MONTHLY"));
        assertThat(r.interestEarned()).isEqualByComparingTo("550.00");
        assertThat(r.maturityAmount()).isEqualByComparingTo("100550.00");
    }

    @Test
    void sample6_simple_eighteenMonths() {
        FdCalculationResponse r = service.calculate(
                request("25000", 18, "MONTHS", "7.25", "SIMPLE", null));
        assertThat(r.interestEarned()).isEqualByComparingTo("2718.75");
        assertThat(r.maturityAmount()).isEqualByComparingTo("27718.75");
    }

    @Test
    void sample7_compoundQuarterly_leftoverPartPeriod() {
        FdCalculationResponse r = service.calculate(
                request("100000", 5, "MONTHS", "7.0", "COMPOUND", "QUARTERLY"));
        assertThat(r.interestEarned()).isEqualByComparingTo("2937.08");
        assertThat(r.maturityAmount()).isEqualByComparingTo("102937.08");
    }

    // ---------- Lab L11 scenario table ----------

    @ParameterizedTest(name = "L11: {0} + {1} on base {2}% -> {4}% (capped={5})")
    @CsvSource({
            "EMPLOYEE, SENIOR,       6.5, 2.0, 8.5, false",
            "EMPLOYEE, PREMIUM_USER, 6.5, 3.0, 8.5, true"
    })
    void labL11ScenarioTable(String cat1, String cat2, String base, String addOnBeforeCap,
                             String finalRate, boolean capped) {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", base, "SIMPLE", null, cat1, cat2));
        assertThat(r.requestedAddOn()).isEqualByComparingTo(addOnBeforeCap);
        assertThat(r.effectiveRate()).isEqualByComparingTo(finalRate);
        assertThat(r.capApplied()).isEqualTo(capped);
    }

    // ---------- Category names written the Lab L6 way ----------

    @Test
    void categoryNamesFromLabL6PayloadAreUnderstood() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "months", "6.5", null, null, "Senior Citizen", "Employee"));
        assertThat(r.categories()).containsExactly("SENIOR", "EMPLOYEE");
        assertThat(r.interestType()).isEqualTo("SIMPLE");
        assertThat(r.maturityAmount()).isEqualByComparingTo("108500.00");
    }

    // ---------- Categories coming from the Customer module (Step 16) ----------

    @Test
    void superSeniorCategoryIsSupported() {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, "SUPER_SENIOR"));
        assertThat(r.effectiveRate()).isEqualByComparingTo("8.00");
        assertThat(r.maturityAmount()).isEqualByComparingTo("108000.00");
    }

    @ParameterizedTest(name = "customer-module classification {0} maps to {1}")
    @CsvSource({
            "REGULAR,        GENERAL",
            "PREMIUM,        PREMIUM_USER",
            "SENIOR_CITIZEN, SENIOR",
            "SUPER_SENIOR,   SUPER_SENIOR"
    })
    void classificationValuesFromTheCustomerModuleAreUnderstood(String incoming, String expectedCode) {
        FdCalculationResponse r = service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, incoming));
        assertThat(r.categories()).containsExactly(expectedCode);
    }

    // ---------- Invalid input must be rejected ----------

    @Test
    void rejectsTooSmallAmount() {
        assertThatThrownBy(() -> service.calculate(request("500", 12, "MONTHS", "6.5", "SIMPLE", null)))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .anyMatch(d -> d.contains("Minimum deposit")));
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> service.calculate(request("-5000", 12, "MONTHS", "6.5", "SIMPLE", null)))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .anyMatch(d -> d.contains("greater than 0")));
    }

    @Test
    void rejectsMissingBaseRate() {
        assertThatThrownBy(() -> service.calculate(request("100000", 12, "MONTHS", null, "SIMPLE", null)))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .contains("Base rate is required."));
    }

    @Test
    void rejectsTermOutOfRange() {
        assertThatThrownBy(() -> service.calculate(request("100000", 200, "MONTHS", "6.5", "SIMPLE", null)))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .anyMatch(d -> d.contains("between 1 and 120")));
    }

    @Test
    void rejectsUnknownCategory() {
        assertThatThrownBy(() -> service.calculate(
                request("100000", 12, "MONTHS", "6.5", "SIMPLE", null, "ALIEN")))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails())
                        .anyMatch(d -> d.contains("Unknown customer category: ALIEN")));
    }

    @Test
    void reportsSeveralProblemsAtOnce() {
        FdCalculationRequest r = request(null, 0, "WEEKS", "40", "FANCY", null);
        assertThatThrownBy(() -> service.calculate(r))
                .isInstanceOf(CalculationException.class)
                .satisfies(e -> assertThat(((CalculationException) e).getDetails()).hasSizeGreaterThanOrEqualTo(4));
    }

    @Test
    void emptyCategoryListMeansNoAddOn() {
        FdCalculationResponse r = service.calculate(
                request("100000", 1, "YEARS", "6.5", "SIMPLE", null));
        assertThat(r.appliedAddOn()).isEqualByComparingTo("0.00");
        assertThat(r.categories()).isEqualTo(List.of());
        assertThat(r.maturityAmount()).isEqualByComparingTo("106500.00");
    }
}
```

**What it does:** each `@Test` method feeds known inputs to the calculator and checks the answer to the paisa. `@ParameterizedTest` runs the L11 table rows one by one.

### 11a. Run the tests
1. **Stop the backend first** (terminal 1, `Ctrl+C`). Tests start their own copy of the app, and two copies can't share port 8082.
2. In terminal 1 (inside `calculator-service`), run:

```
Windows:  .\mvnw.cmd test
Mac:      ./mvnw test
```

**Expected** near the end:

```
Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

If a test fails, Maven prints its name and "expected … but was …". Compare your `FdCalculatorService.java` with Step 6. A missing line or unsaved file is the usual cause.

Take a screenshot of this output for your lab record: it's your "evidence of system behaviour".

---

## Step 12: Python CSV report (Lab L11 §3)

**What:** for each calculation the user wants to keep, Java starts a Python script that appends one row to a CSV file, in exactly the manual's format:

```
Username,Principal,Term,BaseRate,CategoryAddOn,FinalRate,Maturity
alice123,100000,12M,6.5,2.0,8.5,108500
```

### 12a. The Python script
In Explorer, right-click `calculator-service` (the project folder itself, next to `pom.xml`) → **New Folder…** → `scripts`.

**File:** `calculator-service/scripts/generate_fd_report.py`
**Action:** Create this file

```python
#!/usr/bin/env python3
"""
generate_fd_report.py  -  Banking Technology Lab, Lab L11
FD Calculator report generator, started from Spring Boot (FdReportService.java).

Appends ONE calculation as a row to a CSV file, using exactly the columns
shown in the lab manual:

    Username,Principal,Term,BaseRate,CategoryAddOn,FinalRate,Maturity
    alice123,100000,12M,6.5,2.0,8.5,108500

You can also run it by hand to test it, for example:
    python scripts/generate_fd_report.py --output reports/test.csv --username alice123 \
        --principal 100000.00 --term 12M --base-rate 6.50 --addon 2.00 \
        --final-rate 8.50 --maturity 108500.00

On success it prints one JSON line, e.g.
    {"status": "ok", "file": "...", "row": "alice123,100000,...", "rowsInFile": 1}
"""
import argparse
import csv
import json
import os
import sys
from decimal import Decimal, InvalidOperation

HEADER = ["Username", "Principal", "Term", "BaseRate", "CategoryAddOn", "FinalRate", "Maturity"]


def fmt_amount(text):
    """100000.00 -> 100000 ; 27718.75 -> 27718.75 (same style as the manual)."""
    value = Decimal(text)
    if value == value.to_integral_value():
        return str(value.to_integral_value())
    return f"{value:.2f}"


def fmt_rate(text):
    """6.50 -> 6.5 ; 2.00 -> 2.0 ; 7.25 -> 7.25 (same style as the manual)."""
    value = Decimal(text).normalize()
    if value == value.to_integral_value():
        return f"{value.to_integral_value()}.0"
    return format(value, "f")


def main():
    parser = argparse.ArgumentParser(description="Append an FD calculation to a CSV report (Lab L11).")
    parser.add_argument("--output", required=True, help="CSV file to create or append to")
    parser.add_argument("--username", required=True)
    parser.add_argument("--principal", required=True)
    parser.add_argument("--term", required=True, help="e.g. 12M, 400D, 5Y")
    parser.add_argument("--base-rate", required=True)
    parser.add_argument("--addon", required=True)
    parser.add_argument("--final-rate", required=True)
    parser.add_argument("--maturity", required=True)
    args = parser.parse_args()

    try:
        row = [
            args.username,
            fmt_amount(args.principal),
            args.term,
            fmt_rate(args.base_rate),
            fmt_rate(args.addon),
            fmt_rate(args.final_rate),
            fmt_amount(args.maturity),
        ]
    except InvalidOperation:
        print("ERROR: one of the numbers is not a valid decimal number", file=sys.stderr)
        return 1

    folder = os.path.dirname(os.path.abspath(args.output))
    os.makedirs(folder, exist_ok=True)
    new_file = not os.path.exists(args.output) or os.path.getsize(args.output) == 0

    with open(args.output, "a", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        if new_file:
            writer.writerow(HEADER)
        writer.writerow(row)

    with open(args.output, newline="", encoding="utf-8") as f:
        rows_in_file = max(sum(1 for _ in csv.reader(f)) - 1, 0)

    print(json.dumps({
        "status": "ok",
        "file": os.path.abspath(args.output),
        "row": ",".join(row),
        "rowsInFile": rows_in_file,
    }))
    return 0


if __name__ == "__main__":
    sys.exit(main())
```

**What it does:**
- Reads the values given on the command line.
- Formats numbers like the manual does (100000.00 → `100000`, 6.50 → `6.5`).
- Adds the header row if the file is new, then appends the row.
- Prints a one-line JSON summary for Java to read.

**Test it by hand** (terminal 1, inside `calculator-service`). Type it as **one line**:

```
Windows:  python scripts\generate_fd_report.py --output reports\test.csv --username alice123 --principal 100000.00 --term 12M --base-rate 6.50 --addon 2.00 --final-rate 8.50 --maturity 108500.00
Mac:      python3 scripts/generate_fd_report.py --output reports/test.csv --username alice123 --principal 100000.00 --term 12M --base-rate 6.50 --addon 2.00 --final-rate 8.50 --maturity 108500.00
```

**Expected:**

```
{"status": "ok", "file": ".../reports/test.csv", "row": "alice123,100000,12M,6.5,2.0,8.5,108500", "rowsInFile": 1}
```

A folder `reports` appears with `test.csv`, which is identical to the manual's example. You can delete `test.csv`.

### 12b. Two small data classes

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/ReportResult.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

/**
 * Result of adding one calculation to the CSV report (Lab L11).
 * fileName   : e.g. fd_report_alice123.csv
 * csvRow     : the line that was written, e.g. alice123,100000,12M,6.5,2.0,8.5,108500
 * rowsInFile : how many data rows the file now has
 */
public record ReportResult(String fileName, String csvRow, int rowsInFile) {
}
```

**File:** `calculator-service/src/main/java/com/banklab/calculator/dto/FdReportResponse.java`
**Action:** Create this file

```java
package com.banklab.calculator.dto;

/**
 * Answer of POST /api/fd/report: the calculation plus what was written to the report.
 */
public record FdReportResponse(FdCalculationResponse calculation, ReportResult report) {
}
```

### 12c. The Java side that runs Python
Create a folder `report` next to `service` (inside `src/main/java/com/banklab/calculator`).

**File:** `calculator-service/src/main/java/com/banklab/calculator/report/FdReportService.java`
**Action:** Create this file

```java
package com.banklab.calculator.report;

import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.ReportResult;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.model.TermUnit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Lab L11: "Report generation through Python script (executed via Spring Boot)".
 *
 * For every calculation the user wants to keep, Java starts the Python script
 * scripts/generate_fd_report.py, which appends one row to a CSV file:
 *   Username,Principal,Term,BaseRate,CategoryAddOn,FinalRate,Maturity
 * Each user gets their own file: reports/fd_report_<username>.csv
 */
@Service
public class FdReportService {

    private final String pythonCommand;
    private final String scriptPath;
    private final String outputDir;
    private final ObjectMapper objectMapper;

    public FdReportService(@Value("${calculator.report.python-command:python}") String pythonCommand,
                           @Value("${calculator.report.script-path:scripts/generate_fd_report.py}") String scriptPath,
                           @Value("${calculator.report.output-dir:reports}") String outputDir,
                           ObjectMapper objectMapper) {
        this.pythonCommand = pythonCommand;
        this.scriptPath = scriptPath;
        this.outputDir = outputDir;
        this.objectMapper = objectMapper;
    }

    /** Runs the Python script to append this calculation to the user's CSV report. */
    public ReportResult appendToReport(String username, FdCalculationResponse calc) {
        Path script = Paths.get(scriptPath).toAbsolutePath();
        if (!Files.exists(script)) {
            throw CalculationException.internal("REPORT_SCRIPT_MISSING",
                    "Python report script not found at " + script, List.of());
        }
        Path output = reportFileFor(username);
        String termCode = calc.termValue() + TermUnit.valueOf(calc.termUnit()).shortCode();

        List<String> command = List.of(
                pythonCommand, script.toString(),
                "--output", output.toString(),
                "--username", username,
                "--principal", calc.principal().toPlainString(),
                "--term", termCode,
                "--base-rate", calc.baseRate().toPlainString(),
                "--addon", calc.appliedAddOn().toPlainString(),
                "--final-rate", calc.effectiveRate().toPlainString(),
                "--maturity", calc.maturityAmount().toPlainString());

        String processOutput;
        int exitCode;
        try {
            Files.createDirectories(output.getParent());
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            processOutput = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw CalculationException.internal("REPORT_TIMEOUT", "Python report script took too long.", List.of());
            }
            exitCode = process.exitValue();
        } catch (IOException e) {
            throw CalculationException.internal("PYTHON_NOT_FOUND",
                    "Could not start Python using the command '" + pythonCommand + "'. "
                            + "Check calculator.report.python-command in application.properties "
                            + "(use python on Windows, python3 on Mac).",
                    List.of(e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw CalculationException.internal("REPORT_INTERRUPTED", "Report generation was interrupted.", List.of());
        }

        if (exitCode != 0) {
            throw CalculationException.internal("REPORT_FAILED",
                    "The Python report script failed (exit code " + exitCode + ").", List.of(processOutput));
        }
        try {
            // The script prints one JSON line such as {"status":"ok","file":"...","row":"...","rowsInFile":3}
            String lastLine = processOutput.substring(processOutput.lastIndexOf('\n') + 1);
            JsonNode json = objectMapper.readTree(lastLine);
            return new ReportResult(output.getFileName().toString(),
                    json.path("row").asText(),
                    json.path("rowsInFile").asInt());
        } catch (IOException e) {
            throw CalculationException.internal("REPORT_OUTPUT_UNREADABLE",
                    "The Python script ran but its output could not be read.", List.of(processOutput));
        }
    }

    /** Location of a user's CSV report, e.g. reports/fd_report_alice123.csv */
    public Path reportFileFor(String username) {
        String safeName = username.replaceAll("[^A-Za-z0-9_-]", "_");
        return Paths.get(outputDir, "fd_report_" + safeName + ".csv").toAbsolutePath();
    }
}
```

**What it does:** builds the command `python scripts/generate_fd_report.py --output … --username …` and runs it. This is the manual's `Runtime.getRuntime().exec(...)`, done the safer `ProcessBuilder` way, so values with spaces can't break the command. It then reads the script's JSON answer and reports clear errors (for example, Python not installed). Each user gets their own file: `reports/fd_report_<username>.csv`.

**File:** `calculator-service/src/main/java/com/banklab/calculator/controller/FdReportController.java`
**Action:** Create this file

```java
package com.banklab.calculator.controller;

import com.banklab.calculator.dto.FdCalculationRequest;
import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.FdReportResponse;
import com.banklab.calculator.dto.ReportResult;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.report.FdReportService;
import com.banklab.calculator.service.CategorySelectionService;
import com.banklab.calculator.service.FdCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;

/**
 * Lab L11 report endpoints.
 *   POST /api/fd/report          -> calculate and append a row to the user's CSV report
 *   GET  /api/fd/report/download -> download the user's CSV report
 * Before login exists (Phase B) the username is "guest"; after Phase C it is the logged-in user.
 */
@RestController
@Tag(name = "FD Calculator Report", description = "CSV report generated by Python (Lab L11)")
public class FdReportController {

    private final FdCalculatorService calculatorService;
    private final CategorySelectionService categorySelection;
    private final FdReportService reportService;

    public FdReportController(FdCalculatorService calculatorService,
                              CategorySelectionService categorySelection,
                              FdReportService reportService) {
        this.calculatorService = calculatorService;
        this.categorySelection = categorySelection;
        this.reportService = reportService;
    }

    @PostMapping("/api/fd/report")
    @Operation(summary = "Calculate and add the result as a row in your CSV report")
    public FdReportResponse addToReport(@Valid @RequestBody FdCalculationRequest request,
                                        Principal principal,
                                        HttpServletRequest httpRequest) {
        request.setCategories(categorySelection.resolve(request.getCategories(), principal, httpRequest));
        FdCalculationResponse calc = calculatorService.calculate(request);
        ReportResult report = reportService.appendToReport(username(principal), calc);
        return new FdReportResponse(calc, report);
    }

    @GetMapping("/api/fd/report/download")
    @Operation(summary = "Download your CSV report")
    public ResponseEntity<Resource> download(Principal principal) {
        Path file = reportService.reportFileFor(username(principal));
        if (!Files.exists(file)) {
            throw CalculationException.notFound("REPORT_NOT_FOUND",
                    "No report yet. Calculate first, then click 'Add to report'.");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));
    }

    private static String username(Principal principal) {
        return principal == null ? "guest" : principal.getName();
    }
}
```

**What it does:** two new doors.
- `POST /api/fd/report` calculates, then appends the row.
- `GET /api/fd/report/download` sends the CSV file.

Until login exists, the username is `guest`.

### 12d. Report settings

**File:** `calculator-service/src/main/resources/application.properties`
**Action:** Replace the entire file

```properties
# ==========================================================
#  calculator-service settings (Phase B)
# ==========================================================
spring.application.name=calculator-service

# The "door number" this backend listens on (your team's WBS: calculator-service = 8082)
server.port=8082

# ---------- FD Calculator rules ----------
# Where base rates and category add-ons come from:
#   LOCAL   = built-in table inside this service (Phase A and B)
#   PRODUCT = live product-service of your teammates (Phase C, Step 17)
calculator.rates.source=LOCAL

# Maximum COMBINED category add-on, in percent (Lab L6: "configured cap (e.g., 2%)")
calculator.rates.add-on-cap=2.00

# ---------- Lab L11 Python report ----------
# The command that starts Python.  Windows: python     Mac: python3
calculator.report.python-command=python
# Where the script is, relative to the calculator-service folder
calculator.report.script-path=scripts/generate_fd_report.py
# Folder where the CSV files are written
calculator.report.output-dir=reports

# ---------- JSON ----------
# Reject decimals where a whole number is expected (e.g. term value 12.5)
spring.jackson.deserialization.accept-float-as-int=false

# ---------- Swagger UI ----------
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.try-it-out-enabled=true
```

**Mac users:** change `calculator.report.python-command=python` to `calculator.report.python-command=python3` and save.

---

## Step 13: Test the report end to end

1. Start the backend again (terminal 1):

```
Windows:  .\mvnw.cmd spring-boot:run
Mac:      ./mvnw spring-boot:run
```

2. Make sure the frontend is still running in terminal 2 (`npm start`). Open **http://localhost:4200/fd-calculator**.
3. Calculate Sample 1: `100000`, 12 Months, 6.5, Simple, Senior Citizen + Bank Employee.
4. Click **Add to report (CSV)**. **Expected:** a green box: *Row 1 added to fd_report_guest.csv: guest,100000,12M,6.5,2.0,8.5,108500*.
5. Calculate Sample 6 (`25000`, 18 Months, 7.25, Simple, no category) and click **Add to report** → *Row 2 added …: guest,25000,18M,7.25,0.0,7.25,27718.75*.
6. Click **Download my report** → a file `fd_report.csv` downloads. Open it in Excel or Notepad:

```
Username,Principal,Term,BaseRate,CategoryAddOn,FinalRate,Maturity
guest,100000,12M,6.5,2.0,8.5,108500
guest,25000,18M,7.25,0.0,7.25,27718.75
```

7. The same file lives at `calculator-service/reports/fd_report_guest.csv`.

**If you see "Could not start Python using the command 'python'":** fix `calculator.report.python-command` (use `python3` on Mac), save, and restart the backend.

**Phase B is complete: your Lab L11 deliverable** (tests + Python report).


---

# Phase C — Login integration and product rates (Labs L10 and L14)

**What Lab L10 requires:**
- Only logged-in users can calculate.
- Only the **CUSTOMER** role may use the calculator; others get **403**.
- The calculator uses the **logged-in customer's category** automatically.

L14 is the demo of exactly this: "login page with FD Calculator integrated".

**About the login service:** the real login belongs to **Group 10** (port 8080). So you can build and demo now, the backend includes a small **dev login stand-in** with demo users. It uses the same request/response format as Group 10's spec, so switching to their real service later (Step 16) needs only setting changes, no code changes.

| Demo user | Password | Role | Categories | Used to show |
|---|---|---|---|---|
| `alice123` | `Alice@123` | CUSTOMER | Senior + Employee | L11 row 1 and the manual's CSV row |
| `ravi` | `Ravi@1234` | CUSTOMER | Employee + Premium User | Cap applied (L11 row 2) |
| `bob` | `Bob@12345` | CUSTOMER | General | No bonus |
| `officer1` | `Officer@123` | BANK_OFFICER | – | **403 Access denied** (L10) |

## Step 14: Backend security

Stop the backend (terminal 1, `Ctrl+C`) before changing files.

### 14a. Add the security libraries

**File:** `calculator-service/pom.xml`
**Action:** Replace the entire file

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Spring Boot version is pinned so every step of the guide behaves the same -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/>
    </parent>

    <groupId>com.banklab</groupId>
    <artifactId>calculator-service</artifactId>
    <version>1.0.0</version>
    <name>calculator-service</name>
    <description>FD Calculator service - Banking Technology Lab (CSF 3142), Group 6</description>

    <properties>
        <java.version>17</java.version>
        <springdoc.version>2.8.9</springdoc.version>
        <jjwt.version>0.12.6</jjwt.version>
    </properties>

    <dependencies>
        <!-- REST APIs (controllers, JSON) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Input validation annotations such as @NotNull -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <!-- /actuator/health endpoint (liveness/readiness probes in the manual) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- PHASE C: Spring Security (login checks, roles, BCrypt) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <!-- PHASE C: JWT tokens (read and create login tokens) -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <!-- Swagger UI: test the API from the browser -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <!-- JUnit tests -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**What changed:** added Spring Security (login checks, roles, BCrypt) and JJWT (reading and creating JWT tokens).

### 14b. Final settings

**File:** `calculator-service/src/main/resources/application.properties`
**Action:** Replace the entire file

```properties
# ==========================================================
#  calculator-service settings (Phase C - final version)
# ==========================================================
spring.application.name=calculator-service

# The "door number" this backend listens on (your team's WBS: calculator-service = 8082)
server.port=8082

# ---------- FD Calculator rules ----------
# Where base rates and category add-ons come from:
#   LOCAL   = built-in table inside this service
#   PRODUCT = live product-service of your teammates (Step 17)
calculator.rates.source=LOCAL

# Maximum COMBINED category add-on, in percent (Lab L6: "configured cap (e.g., 2%)")
calculator.rates.add-on-cap=2.00

# ---------- Security (Lab L10) ----------
# true  = only logged-in CUSTOMER users can use the calculator (Lab L10)
# false = anyone can use it (like Phase A/B)
calculator.security.enabled=true

# Secret used to check login tokens. MUST be the SAME secret the Login team (Group 10) uses.
# At least 32 characters.
calculator.jwt.secret=banklab-group6-dev-secret-change-me-0123456789abcdef
# Set to true if the Login team's secret is Base64 text (see Step 16)
calculator.jwt.secret-base64=false
# How long tokens from the dev login stand-in stay valid (36000 s = 10 hours)
calculator.jwt.expiry-seconds=36000

# ---------- Which categories are used (Lab L10) ----------
# REQUEST = user ticks categories on the page
# PROFILE = categories of the logged-in customer are used automatically
calculator.categories.source=PROFILE

# Where the logged-in customer's categories come from:
#   DEV    = built-in demo users of the dev login stand-in
#   REMOTE = Group 10's customer service (GET /api/customer/profile)
calculator.customer.source=DEV
calculator.customer.base-url=http://localhost:8080
calculator.customer.profile-path=/api/customer/profile
calculator.customer.timeout-ms=3000

# ---------- Dev login stand-in ----------
# true  = this service offers POST /api/auth/login with demo users (until Group 10's login is ready)
# false = use Group 10's real login service (Step 16)
calculator.dev-auth.enabled=true

# ---------- Product-service (used only when calculator.rates.source=PRODUCT) ----------
calculator.product.base-url=http://localhost:8081
calculator.product.products-path=/products
calculator.product.rates-path=/api/product-rate
calculator.product.categories-path=/api/customer-category
calculator.product.timeout-ms=3000

# ---------- Lab L11 Python report ----------
# Windows: python     Mac: python3
calculator.report.python-command=python
calculator.report.script-path=scripts/generate_fd_report.py
calculator.report.output-dir=reports

# ---------- JSON ----------
# Reject decimals where a whole number is expected (e.g. term value 12.5)
spring.jackson.deserialization.accept-float-as-int=false

# ---------- Swagger UI ----------
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.try-it-out-enabled=true
```

**Mac users:** again set `calculator.report.python-command=python3`.

**What the new settings mean:**
- `calculator.security.enabled=true`: only logged-in CUSTOMER users may calculate.
- `calculator.jwt.secret`: the secret used to sign and check tokens.
- `calculator.categories.source=PROFILE`: use the logged-in customer's categories.
- `calculator.customer.source=DEV`: take those categories from the demo users.
- `calculator.dev-auth.enabled=true`: switch on the dev login stand-in.

### 14c. Token handling
Create a folder `security` inside `src/main/java/com/banklab/calculator`.

**File:** `calculator-service/src/main/java/com/banklab/calculator/security/JwtService.java`
**Action:** Create this file

```java
package com.banklab.calculator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * Creates and checks JWT login tokens (Lab L3 section 4.4).
 * A token looks like xxxxx.yyyyy.zzzzz ; the middle part holds
 *   { "sub": "alice123", "role": "CUSTOMER", "exp": ... }
 * and the last part is a signature made with the shared secret, so nobody can fake it.
 */
@Service
public class JwtService {

    /** Who the token belongs to. */
    public record TokenUser(String username, String role) {
    }

    private final SecretKey key;
    private final long expirySeconds;

    public JwtService(@Value("${calculator.jwt.secret}") String secret,
                      @Value("${calculator.jwt.secret-base64:false}") boolean secretIsBase64,
                      @Value("${calculator.jwt.expiry-seconds:36000}") long expirySeconds) {
        byte[] bytes = secretIsBase64
                ? Decoders.BASE64.decode(secret)
                : secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "calculator.jwt.secret must be at least 32 characters (256 bits) long");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirySeconds = expirySeconds;
    }

    /** Makes a new token (used only by the dev login stand-in). */
    public String createToken(String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirySeconds * 1000))
                .signWith(key)
                .compact();
    }

    /**
     * Checks the signature and expiry of a token and reads the user from it.
     * Throws JwtException if the token is fake, damaged or expired.
     */
    public TokenUser parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new TokenUser(claims.getSubject(), extractRole(claims));
    }

    public long getExpirySeconds() {
        return expirySeconds;
    }

    /** Accepts "role": "CUSTOMER", "role": "ROLE_CUSTOMER" or "roles": ["CUSTOMER"]. */
    private static String extractRole(Claims claims) {
        Object role = claims.get("role");
        if (role == null) {
            Object roles = claims.get("roles");
            if (roles instanceof Collection<?> list && !list.isEmpty()) {
                role = list.iterator().next();
            } else if (roles instanceof String text) {
                role = text;
            }
        }
        if (role == null) {
            return null;
        }
        String r = role.toString().trim().toUpperCase(Locale.ROOT);
        return r.startsWith("ROLE_") ? r.substring(5) : r;
    }
}
```

**What it does:** creates tokens (for the dev login) and checks them (for every request).
- A token is signed with the secret, so it can't be faked.
- It accepts the role written as `CUSTOMER`, `ROLE_CUSTOMER` or `roles: [...]`, so it works with however Group 10 builds their tokens.

**File:** `calculator-service/src/main/java/com/banklab/calculator/security/JwtAuthFilter.java`
**Action:** Create this file

```java
package com.banklab.calculator.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Runs before every request. If the request carries
 *     Authorization: Bearer <token>
 * and the token is genuine, it tells Spring Security who the user is and
 * what role they have. If there is no token, or it is fake/expired, the user
 * stays "anonymous" and protected addresses answer 401.
 *
 * NOTE: this class is deliberately NOT a @Component. It is created inside
 * SecurityConfig so it runs exactly once, inside the security chain.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            try {
                JwtService.TokenUser user = jwtService.parse(token);
                if (user.username() != null && user.role() != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.username(), null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + user.role())));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException e) {
                // Fake, damaged or expired token: leave the user anonymous.
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
```

**What it does:** runs before every request.
- If there's a valid `Authorization: Bearer …` header, it tells Spring Security who the user is and what role they have.
- It's deliberately not marked `@Component`. If it were, Spring would run it twice, and the login would be lost on the second run.

### 14d. Security rules and Swagger "Authorize" button

**File:** `calculator-service/src/main/java/com/banklab/calculator/config/SecurityConfig.java`
**Action:** Create this file

```java
package com.banklab.calculator.config;

import com.banklab.calculator.dto.ErrorResponse;
import com.banklab.calculator.security.JwtAuthFilter;
import com.banklab.calculator.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lab L10: "Only authenticated users can simulate maturity estimates" and
 * "Access restricted to CUSTOMER role". Unauthorised users receive 403.
 *
 * Rules when calculator.security.enabled=true:
 *   - Swagger UI, health check and POST /api/auth/login : open to everyone
 *   - /api/fd/** and /api/calculator/**                  : CUSTOMER role only
 *   - no/invalid token -> 401, wrong role -> 403
 * When calculator.security.enabled=false, everything is open (like Phase A/B).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final boolean securityEnabled;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public SecurityConfig(@Value("${calculator.security.enabled:true}") boolean securityEnabled,
                          JwtService jwtService,
                          ObjectMapper objectMapper) {
        this.securityEnabled = securityEnabled;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                writeError(response, 401, "UNAUTHORIZED",
                                        "Please log in first. A valid login token is required."))
                        .accessDeniedHandler((request, response, e) ->
                                writeError(response, 403, "FORBIDDEN",
                                        "Access denied. The FD Calculator is only for customers (CUSTOMER role).")));

        if (!securityEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/actuator/health", "/actuator/health/**", "/error").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers("/api/fd/**", "/api/calculator/**").hasRole("CUSTOMER")
                    .anyRequest().authenticated());
        }
        return http.build();
    }

    private void writeError(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), new ErrorResponse(code, message, List.of()));
    }
}
```

**What it does:** the access rules of Lab L10.
- Swagger, the health check and login are open to everyone.
- `/api/fd/**` needs the **CUSTOMER** role.
- No token gives a JSON **401**; the wrong role gives a JSON **403**.

The rules are applied at the URL level, which does the same job as the manual's `@PreAuthorize("hasRole('CUSTOMER')")`.

**File:** `calculator-service/src/main/java/com/banklab/calculator/config/OpenApiConfig.java`
**Action:** Create this file

```java
package com.banklab.calculator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Adds an "Authorize" button to Swagger UI so you can paste a login token
 * and test the protected endpoints from the browser.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "FD Calculator Service", version = "1.0",
                description = "Banking Technology Lab (CSF 3142) - Group 6 - Labs L6, L10, L11"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
```

### 14e. Dev login stand-in
Create a folder `auth` inside `src/main/java/com/banklab/calculator`.

**File:** `calculator-service/src/main/java/com/banklab/calculator/auth/DevUserStore.java`
**Action:** Create this file

```java
package com.banklab.calculator.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Demo users for the DEV LOGIN STAND-IN.
 *
 * The real login belongs to Group 10. Until their service is running, these
 * users let you demonstrate "login page with FD Calculator integrated" (Lab L14).
 * Passwords are stored only as BCrypt hashes (Lab L1/L2 practice).
 *
 *   alice123 / Alice@123   CUSTOMER      categories SENIOR + EMPLOYEE      (L11 row 1)
 *   ravi     / Ravi@1234   CUSTOMER      categories EMPLOYEE + PREMIUM_USER (L11 row 2, capped)
 *   bob      / Bob@12345   CUSTOMER      category  GENERAL                 (no bonus)
 *   officer1 / Officer@123 BANK_OFFICER  -> must get 403 on the calculator (Lab L10)
 */
@Component
public class DevUserStore {

    public record DevUser(String username, String passwordHash, String role, List<String> categories) {
    }

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Map<String, DevUser> users = new LinkedHashMap<>();

    public DevUserStore() {
        add("alice123", "Alice@123", "CUSTOMER", List.of("SENIOR", "EMPLOYEE"));
        add("ravi", "Ravi@1234", "CUSTOMER", List.of("EMPLOYEE", "PREMIUM_USER"));
        add("bob", "Bob@12345", "CUSTOMER", List.of("GENERAL"));
        add("officer1", "Officer@123", "BANK_OFFICER", List.of());
    }

    private void add(String username, String password, String role, List<String> categories) {
        users.put(username.toLowerCase(Locale.ROOT),
                new DevUser(username, encoder.encode(password), role, categories));
    }

    /** Returns the user if the username exists and the password matches its BCrypt hash. */
    public Optional<DevUser> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        DevUser user = users.get(username.trim().toLowerCase(Locale.ROOT));
        if (user != null && encoder.matches(password, user.passwordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<DevUser> find(String username) {
        return username == null ? Optional.empty()
                : Optional.ofNullable(users.get(username.trim().toLowerCase(Locale.ROOT)));
    }

    public List<String> categoriesOf(String username) {
        return find(username).map(DevUser::categories).orElse(List.of());
    }
}
```

**File:** `calculator-service/src/main/java/com/banklab/calculator/auth/DevAuthController.java`
**Action:** Create this file

```java
package com.banklab.calculator.auth;

import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * DEV LOGIN STAND-IN - same request/response shape as Group 10's
 * POST /api/auth/login (cust_auth_swagger_api.yaml), so the Angular login page
 * works unchanged when you switch to their real service (Step 16).
 *
 * Exists only when calculator.dev-auth.enabled=true.
 */
@RestController
@ConditionalOnProperty(name = "calculator.dev-auth.enabled", havingValue = "true")
@Tag(name = "Dev login stand-in", description = "Temporary login until Group 10's auth service is running")
public class DevAuthController {

    public record LoginRequest(
            @NotBlank(message = "Username is required") String username,
            @NotBlank(message = "Password is required") String password) {
    }

    public record LoginResponse(String token, String tokenType, long expiresIn, String role, String username) {
    }

    public record CurrentUser(String username, String role, List<String> categories) {
    }

    private final DevUserStore users;
    private final JwtService jwtService;

    public DevAuthController(DevUserStore users, JwtService jwtService) {
        this.users = users;
        this.jwtService = jwtService;
    }

    @PostMapping("/api/auth/login")
    @Operation(summary = "Log in with a demo user and receive a JWT token")
    public LoginResponse login(@Valid @RequestBody LoginRequest body) {
        DevUserStore.DevUser user = users.authenticate(body.username(), body.password())
                .orElseThrow(() -> CalculationException.unauthorized(
                        "INVALID_CREDENTIALS", "Invalid username or password"));
        String token = jwtService.createToken(user.username(), user.role());
        return new LoginResponse(token, "Bearer", jwtService.getExpirySeconds(), user.role(), user.username());
    }

    @GetMapping("/api/auth/me")
    @Operation(summary = "Who am I? (needs a token)")
    public CurrentUser me(Principal principal) {
        if (principal == null) {
            throw CalculationException.unauthorized("UNAUTHORIZED", "Please log in first.");
        }
        DevUserStore.DevUser user = users.find(principal.getName())
                .orElseThrow(() -> CalculationException.notFound("USER_NOT_FOUND", "Unknown user"));
        return new CurrentUser(user.username(), user.role(), user.categories());
    }
}
```

**What they do:**
- The demo users' passwords are stored only as BCrypt hashes (Lab L1/L2 practice).
- `POST /api/auth/login` checks the password and returns a token in Group 10's `LoginResponse` shape.
- The whole controller disappears when `calculator.dev-auth.enabled=false`.

### 14f. Customer categories from the profile
Create a folder `client` inside `src/main/java/com/banklab/calculator`.

**File:** `calculator-service/src/main/java/com/banklab/calculator/client/JsonFields.java`
**Action:** Create this file

```java
package com.banklab.calculator.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Small helpers for reading JSON from other teams' services.
 * Your product spec uses UPPER_SNAKE names (BASE_RATE) while other code often
 * uses camelCase (baseRate), so every helper accepts several possible names.
 */
public final class JsonFields {

    private JsonFields() {
    }

    public static JsonNode field(JsonNode node, String... names) {
        if (node == null) {
            return null;
        }
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value != null && !value.isNull()) {
                return value;
            }
        }
        return null;
    }

    public static String text(JsonNode node, String... names) {
        JsonNode v = field(node, names);
        return v == null ? null : v.asText();
    }

    public static BigDecimal decimal(JsonNode node, String... names) {
        JsonNode v = field(node, names);
        if (v == null || v.asText().isBlank()) {
            return null;
        }
        return v.isNumber() ? v.decimalValue() : new BigDecimal(v.asText().trim());
    }

    public static Integer integer(JsonNode node, String... names) {
        BigDecimal d = decimal(node, names);
        return d == null ? null : d.intValue();
    }

    public static LocalDate date(JsonNode node, String... names) {
        String t = text(node, names);
        if (t == null || t.isBlank()) {
            return null;
        }
        return LocalDate.parse(t.length() > 10 ? t.substring(0, 10) : t);
    }

    /** Accepts a plain array, or a wrapper such as { "items": [...] } or { "content": [...] }. */
    public static List<JsonNode> items(JsonNode node) {
        List<JsonNode> list = new ArrayList<>();
        if (node == null) {
            return list;
        }
        JsonNode array = node.isArray() ? node : field(node, "items", "content", "data");
        if (array != null && array.isArray()) {
            array.forEach(list::add);
        }
        return list;
    }
}
```

**File:** `calculator-service/src/main/java/com/banklab/calculator/client/CustomerClient.java`
**Action:** Create this file

```java
package com.banklab.calculator.client;

import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.model.CategoryCodes;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab L10: "Calculator fetches logged-in user's category".
 * Calls Group 10's GET /api/customer/profile with the user's own token and reads
 * custCategoryCode (their spec: "drives rate/pricing selection in the FD module").
 * Used only when calculator.customer.source=REMOTE.
 */
@Component
public class CustomerClient {

    private final RestClient restClient;
    private final String baseUrl;
    private final String profilePath;

    public CustomerClient(@Value("${calculator.customer.base-url:http://localhost:8080}") String baseUrl,
                          @Value("${calculator.customer.profile-path:/api/customer/profile}") String profilePath,
                          @Value("${calculator.customer.timeout-ms:3000}") int timeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(timeoutMs));
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
        this.baseUrl = baseUrl;
        this.profilePath = profilePath;
    }

    public List<String> fetchCategories(String authorizationHeader) {
        JsonNode profile;
        try {
            RestClient.RequestHeadersSpec<?> call = restClient.get().uri(profilePath);
            if (authorizationHeader != null) {
                call = call.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
            }
            profile = call.retrieve().body(JsonNode.class);
        } catch (ResourceAccessException e) {
            throw CalculationException.serviceUnavailable("CUSTOMER_SERVICE_UNAVAILABLE",
                    "Customer service is not reachable at " + baseUrl + ". Is Group 10's service running?");
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw CalculationException.unprocessable("CUSTOMER_PROFILE_NOT_FOUND",
                        "No customer profile exists for your login. Ask a bank officer to create one.");
            }
            throw CalculationException.badGateway("CUSTOMER_SERVICE_ERROR",
                    "Customer service answered with HTTP " + e.getStatusCode().value() + ".");
        }

        List<String> result = new ArrayList<>();
        JsonNode list = JsonFields.field(profile, "categories", "custCategoryCodes");
        if (list != null && list.isArray()) {
            list.forEach(n -> result.add(n.asText()));
        }
        String single = JsonFields.text(profile, "custCategoryCode", "CUST_CATEGORY_CODE", "categoryCode",
                "classification", "customerClassification");
        if (single != null && !single.isBlank()) {
            result.add(single);
        }
        return CategoryCodes.normalizeAll(result);
    }
}
```

**What they do:**
- `CustomerClient` calls Group 10's `GET /api/customer/profile` with the user's own token and reads `custCategoryCode`. It's only used when `calculator.customer.source=REMOTE` (Step 16).
- `JsonFields` reads JSON field names in either UPPER_SNAKE or camelCase.

**File:** `calculator-service/src/main/java/com/banklab/calculator/service/CategorySelectionService.java`
**Action:** Replace the entire file

```java
package com.banklab.calculator.service;

import com.banklab.calculator.auth.DevUserStore;
import com.banklab.calculator.client.CustomerClient;
import com.banklab.calculator.model.CategoryCodes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

/**
 * Decides WHICH customer categories are used for a calculation (Phase C version).
 *
 * calculator.categories.source=REQUEST : categories ticked on the page are used.
 * calculator.categories.source=PROFILE : Lab L10 - "Calculator fetches logged-in user's
 *     category". Whatever the page sends is ignored; the logged-in customer's own
 *     categories are used instead, taken from:
 *       calculator.customer.source=DEV    -> demo users of the dev login stand-in
 *       calculator.customer.source=REMOTE -> Group 10's GET /api/customer/profile
 */
@Service
public class CategorySelectionService {

    private final String categorySource;
    private final String customerSource;
    private final DevUserStore devUsers;
    private final CustomerClient customerClient;

    public CategorySelectionService(@Value("${calculator.categories.source:REQUEST}") String categorySource,
                                    @Value("${calculator.customer.source:DEV}") String customerSource,
                                    DevUserStore devUsers,
                                    CustomerClient customerClient) {
        this.categorySource = categorySource.trim().toUpperCase(Locale.ROOT);
        this.customerSource = customerSource.trim().toUpperCase(Locale.ROOT);
        this.devUsers = devUsers;
        this.customerClient = customerClient;
    }

    /** "REQUEST" or "PROFILE". */
    public String categorySource() {
        return categorySource;
    }

    /** Categories to use for this calculation. */
    public List<String> resolve(List<String> requestedCategories, Principal principal,
                                HttpServletRequest httpRequest) {
        if (!"PROFILE".equals(categorySource) || principal == null) {
            return requestedCategories == null ? List.of() : requestedCategories;
        }
        return myCategories(principal, httpRequest);
    }

    /** Categories of the logged-in customer; GENERAL when they have none. */
    public List<String> myCategories(Principal principal, HttpServletRequest httpRequest) {
        if (principal == null) {
            return List.of();
        }
        List<String> categories;
        if ("REMOTE".equals(customerSource)) {
            categories = customerClient.fetchCategories(httpRequest.getHeader(HttpHeaders.AUTHORIZATION));
        } else {
            categories = CategoryCodes.normalizeAll(devUsers.categoriesOf(principal.getName()));
        }
        return categories.isEmpty() ? List.of("GENERAL") : categories;
    }
}
```

**What changed:** in PROFILE mode, whatever the page sends is ignored and the logged-in customer's own categories are used (Lab L10).

Notice that **no controller had to change**: they already asked this service.

### 14g. Run and test the security with Swagger
1. Start the backend:

```
Windows:  .\mvnw.cmd spring-boot:run
Mac:      ./mvnw spring-boot:run
```

   It downloads the new libraries first. You may see a line "Using generated security password: …". That's normal; ignore it.

2. Open **http://localhost:8082/swagger-ui.html**.
3. **No token → 401:** POST /api/fd/calculate → Try it out → paste Sample 1 → Execute. **Expected:** code **401**, `"code": "UNAUTHORIZED"`.
4. **Log in as a bank officer:** open the **Dev login stand-in** section → **POST /api/auth/login** → Try it out → body:

```json
{ "username": "officer1", "password": "Officer@123" }
```

   Execute. **Expected:** 200 with a long `"token": "eyJ…"` and `"role": "BANK_OFFICER"`. Copy the token value **without** the quotes.

5. Click the green **Authorize** button at the top of the page, paste the token, click **Authorize**, then **Close**.
6. Run POST /api/fd/calculate again. **Expected:** code **403**, `"code": "FORBIDDEN"`, "Access denied. The FD Calculator is only for customers (CUSTOMER role)." That's the Lab L10 requirement.
7. **Log in as a customer:** repeat step 4 with `{ "username": "alice123", "password": "Alice@123" }`. Click **Authorize** → **Logout**, paste the new token → **Authorize**.
8. Run the calculation again with Sample 1 but **"categories": []**. **Expected:** 200, `"categories": ["SENIOR", "EMPLOYEE"]` (taken from alice123's profile), `"maturityAmount": 108500.00`.
9. A wrong password gives 401 "Invalid username or password".

---

## Step 15: Frontend login page

Keep the backend running. In terminal 2, the frontend picks up file changes automatically, so there's no need to restart it.

In `banking-frontend/src/app` create a folder `auth`, and inside `auth` another folder `login`.

**File:** `banking-frontend/src/app/auth/auth.service.ts`
**Action:** Create this file

```typescript
import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map } from 'rxjs';

/** The logged-in user, read from the JWT token. */
export interface AuthUser {
  username: string;
  role: string;
  token: string;
  expiresAt: number;
}

/** Shape of POST /api/auth/login answer (Group 10 spec: LoginResponse). */
interface LoginResponse {
  token: string;
  tokenType?: string;
  expiresIn?: number;
  role?: string;
  username?: string;
}

const STORAGE_KEY = 'banklab_token';

/**
 * Handles login and logout (Labs L2, L3, L10).
 * The token is kept in the browser's localStorage (Lab L2: "Store JWT token in local storage")
 * so the user stays logged in after a page refresh, until the token expires.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly user = signal<AuthUser | null>(null);

  constructor() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      const u = decodeToken(saved);
      if (u && u.expiresAt > Date.now()) {
        this.user.set(u);
      } else {
        localStorage.removeItem(STORAGE_KEY);
      }
    }
  }

  login(username: string, password: string): Observable<AuthUser> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password }).pipe(
      map((res) => {
        const u = decodeToken(res.token, res);
        if (!u) {
          throw new Error('The login service returned an invalid token.');
        }
        localStorage.setItem(STORAGE_KEY, res.token);
        this.user.set(u);
        return u;
      }),
    );
  }

  logout(goToLogin = true): void {
    localStorage.removeItem(STORAGE_KEY);
    this.user.set(null);
    if (goToLogin) {
      this.router.navigate(['/login']);
    }
  }

  /** The token to send to the backend, or null if not logged in / expired. */
  token(): string | null {
    const u = this.user();
    if (!u) {
      return null;
    }
    if (u.expiresAt <= Date.now()) {
      this.logout(false);
      return null;
    }
    return u.token;
  }

  isLoggedIn(): boolean {
    return this.token() !== null;
  }

  hasAnyRole(roles: string[]): boolean {
    const u = this.user();
    return !!u && roles.includes(u.role);
  }
}

/**
 * Reads the middle part of a JWT (the "payload") without any extra library.
 * (Lab L4 section 6.2 uses jwtDecode(); this does the same job.)
 */
function decodeToken(token: string, res?: LoginResponse): AuthUser | null {
  try {
    const part = token.split('.')[1];
    const base64 = part.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
    const payload = JSON.parse(atob(padded));
    const rawRole =
      payload.role ?? (Array.isArray(payload.roles) ? payload.roles[0] : payload.roles) ?? res?.role ?? '';
    const role = String(rawRole).toUpperCase().replace(/^ROLE_/, '');
    const expiresAt =
      typeof payload.exp === 'number' ? payload.exp * 1000 : Date.now() + (res?.expiresIn ?? 3600) * 1000;
    return { username: payload.sub ?? res?.username ?? '', role, token, expiresAt };
  } catch {
    return null;
  }
}
```

**What it does:**
- Logs in and stores the token in the browser's localStorage (Lab L2), so a page refresh keeps you logged in until the token expires.
- Reads the username and role from inside the token.
- Logs you out.

**File:** `banking-frontend/src/app/auth/auth.interceptor.ts`
**Action:** Create this file

```typescript
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Lab L9: "Use HttpInterceptor to auto-attach JWT token in all requests".
 * Adds  Authorization: Bearer <token>  to every call, and sends the user back
 * to the login page if the backend answers 401 (token missing or expired).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const isLoginCall = req.url.includes('/api/auth/login');

  const token = isLoginCall ? null : auth.token();
  const request = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(request).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse && err.status === 401 && !isLoginCall) {
        auth.logout(false);
        router.navigate(['/login'], { queryParams: { returnUrl: router.url, expired: 1 } });
      }
      return throwError(() => err);
    }),
  );
};
```

**What it does:** Lab L9's "HttpInterceptor". It automatically adds the token to every request, and on a 401 (expired token) sends you to the login page.

**File:** `banking-frontend/src/app/auth/auth.guard.ts`
**Action:** Create this file

```typescript
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Protects pages (Lab L4 section 6.3 "Conditionally render routes").
 *  - not logged in         -> go to /login
 *  - logged in, wrong role -> go to Home with an "Access denied" message
 * The page's allowed roles are written in app.routes.ts as data: { roles: [...] }.
 * (The backend checks the role again - the frontend check is only for convenience.)
 */
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }
  const roles = route.data?.['roles'] as string[] | undefined;
  if (roles && roles.length > 0 && !auth.hasAnyRole(roles)) {
    return router.createUrlTree(['/'], { queryParams: { denied: 1 } });
  }
  return true;
};
```

**What it does:** stops the calculator page from opening unless you're logged in as a CUSTOMER. The backend checks again anyway, because the manual says "always verify at backend".

**File:** `banking-frontend/src/app/auth/login/login.ts`
**Action:** Create this file

```typescript
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { describeHttpError } from '../../calculator/fd-calculator.service';
import { AuthService } from '../auth.service';

/** Login page (Lab L2: masked password; Lab L14: "login page with FD Calculator integrated"). */
@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  username = '';
  password = '';
  showPassword = false;

  readonly busy = signal(false);
  readonly error = signal<string | null>(null);
  readonly expired = this.route.snapshot.queryParamMap.has('expired');

  submit(): void {
    this.error.set(null);
    if (!this.username.trim() || !this.password) {
      this.error.set('Please enter both username and password.');
      return;
    }
    this.busy.set(true);
    this.auth.login(this.username.trim(), this.password).subscribe({
      next: (user) => {
        this.busy.set(false);
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        this.router.navigateByUrl(returnUrl || (user.role === 'CUSTOMER' ? '/fd-calculator' : '/'));
      },
      error: (err) => {
        this.busy.set(false);
        if (err instanceof HttpErrorResponse && err.status === 401) {
          this.error.set('Invalid username or password.');
        } else if (err instanceof HttpErrorResponse && err.status === 404) {
          this.error.set('Login service not found. Check that the login service is running (see Step 16).');
        } else {
          this.error.set(describeHttpError(err)[0]);
        }
      },
    });
  }
}
```

**File:** `banking-frontend/src/app/auth/login/login.html`
**Action:** Create this file

```html
<div class="container">
  <form class="card login-card" (ngSubmit)="submit()" novalidate>
    <h1 class="card-title">Log in to BankLab</h1>

    @if (expired) {
      <p class="notice">Your session has expired. Please log in again.</p>
    }

    <label class="field">
      <span class="label">Username</span>
      <input type="text" name="username" [(ngModel)]="username" autocomplete="username" />
    </label>

    <label class="field">
      <span class="label">Password</span>
      <div class="password-row">
        <input [type]="showPassword ? 'text' : 'password'" name="password" [(ngModel)]="password"
               autocomplete="current-password" />
        <button type="button" class="btn btn-secondary" (click)="showPassword = !showPassword">
          {{ showPassword ? 'Hide' : 'Show' }}
        </button>
      </div>
    </label>

    @if (error()) {
      <p class="error-box">{{ error() }}</p>
    }

    <button type="submit" class="btn btn-primary full" [disabled]="busy()">
      {{ busy() ? 'Logging in...' : 'Log in' }}
    </button>
  </form>
</div>
```

**File:** `banking-frontend/src/app/auth/login/login.css`
**Action:** Create this file

```css
.login-card {
  max-width: 420px;
  margin: 40px auto;
}

.password-row {
  display: flex;
  gap: 8px;
}

.password-row input {
  flex: 1;
}

.full {
  width: 100%;
  margin-top: 6px;
}
```

**What they do:** the login page. The password is **masked** (Lab L2), with a Show/Hide button. After a successful login it opens the calculator.

Now connect everything by replacing four existing files.

**File:** `banking-frontend/src/app/app.config.ts`
**Action:** Replace the entire file

```typescript
import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { authInterceptor } from './auth/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // HTTP calls, with the login token attached automatically (Lab L9)
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
```

**File:** `banking-frontend/src/app/app.routes.ts`
**Action:** Replace the entire file

```typescript
import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { Login } from './auth/login/login';
import { FdCalculator } from './calculator/fd-calculator';
import { Home } from './home/home';

/** Which page is shown for which address. */
export const routes: Routes = [
  { path: '', component: Home, title: 'Home | BankLab' },
  { path: 'login', component: Login, title: 'Login | BankLab' },
  {
    path: 'fd-calculator',
    component: FdCalculator,
    canActivate: [authGuard],
    data: { roles: ['CUSTOMER'] }, // Lab L10: "Access restricted to CUSTOMER role"
    title: 'FD Calculator | BankLab',
  },
  { path: '**', redirectTo: '' },
];
```

**File:** `banking-frontend/src/app/app.ts`
**Action:** Replace the entire file

```typescript
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './auth/auth.service';

/** The frame around every page: top navigation bar (with login status) + footer. */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly auth = inject(AuthService);

  logout(): void {
    this.auth.logout();
  }
}
```

**File:** `banking-frontend/src/app/app.html`
**Action:** Replace the entire file

```html
<header class="navbar">
  <div class="navbar-inner">
    <a routerLink="/" class="brand">
      <span class="brand-logo">₹</span>
      <span>BankLab</span>
    </a>
    <nav class="nav-links">
      <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Home</a>
      @if (auth.user()?.role === 'CUSTOMER') {
        <a routerLink="/fd-calculator" routerLinkActive="active">FD Calculator</a>
      }
    </nav>
    <div class="user-area">
      @if (auth.user(); as u) {
        <span>{{ u.username }}</span>
        <span class="role">{{ u.role }}</span>
        <button type="button" class="nav-button" (click)="logout()">Log out</button>
      } @else {
        <a routerLink="/login" class="nav-button">Log in</a>
      }
    </div>
  </div>
</header>

<main>
  <router-outlet />
</main>

<footer class="footer">
  Banking Technology Lab (CSF 3142) · Group 6 · FD Calculator Module
</footer>
```

**What changed:**
- The interceptor is switched on.
- `/login` exists, and `/fd-calculator` is protected for CUSTOMER.
- The top bar shows who is logged in, with a Log out button. The FD Calculator link appears only for customers.

`app.css`, the calculator page and the home page stay unchanged.

### 15a. Test the login flow (this is your L14 demo)
Save all files. Terminal 2 should print "Application bundle generation complete" again. Open **http://localhost:4200**.

1. The top bar shows **Log in** and no FD Calculator link.
2. Type **http://localhost:4200/fd-calculator** in the address bar. You're sent to the login page (the guard works).
3. Enter `alice123` / `wrong` → "Invalid username or password."
4. Enter `alice123` / `Alice@123` → you land on the calculator. The top bar shows **alice123 CUSTOMER**. The category box now reads "Taken automatically from your customer profile: Senior Citizen +1.00%, Bank Employee +1.00%".
5. Enter `100000`, 12 Months, 6.5, **Simple** → **Calculate** → **₹1,08,500.00**.
6. Click **Add to report (CSV)** → *Row 1 added to fd_report_alice123.csv: alice123,100000,12M,6.5,2.0,8.5,108500*. This is **exactly the manual's L11 CSV line**.
7. **Log out**, then log in as `ravi` / `Ravi@1234`. The same inputs give ₹1,08,500.00 with the yellow "cap applied" notice (3% requested → 2% applied).
8. Log out, then log in as `bob` / `Bob@12345`. The same inputs give **₹1,06,500.00** (6.5%, no bonus).
9. Log out, then log in as `officer1` / `Officer@123`. You land on Home, with no FD Calculator link. Type **/fd-calculator** in the address bar → Home with "Access denied…". The backend would also answer 403 (Step 14g).
10. Refresh the page while logged in: you stay logged in. Click **Log out**: you're back at the login page.

**Phase C core is complete. This is your Lab L10 and L14 deliverable.**

---

## Step 16: Switch to Group 10's real login and customer service

Do this when Group 10's service is running on port 8080. First, **agree three things with Group 10:**

1. **The token secret.** Ask them for their JWT secret text.
   - If their code signs with the old style `signWith(SignatureAlgorithm.HS256, SECRET_KEY)` as shown in manual L3 §4.4 (JJWT 0.9), that library treats the secret as **Base64**, so you'll set `secret-base64=true`.
   - If they use the newer `Keys.hmacShaKeyFor(secret.getBytes())` style, keep it `false`.
2. **Token contents.** The token's `sub` must be the username, and it must include the role as `"role": "CUSTOMER"`. `ROLE_CUSTOMER` or a `roles` list also works.
3. **Profile field.** Their `GET /api/customer/profile` must return the customer's category. Your calculator reads the first of these field names it finds: `custCategoryCode` (Group 10's written spec), `CUST_CATEGORY_CODE`, `categoryCode`, `classification` or `customerClassification`. The value can be written any of these ways:

| Value they send | Your calculator reads it as |
|---|---|
| `GENERAL`, `REGULAR`, `NORMAL` | GENERAL (no bonus) |
| `SENIOR`, `SENIOR_CITIZEN`, `Senior Citizen`, `SR_CITIZEN` | SENIOR (+1.00%) |
| `EMPLOYEE`, `STAFF`, `BANK_EMPLOYEE` | EMPLOYEE (+1.00%) |
| `PREMIUM`, `PREMIUM_USER` | PREMIUM_USER (+2.00%) |
| `SUPER_SENIOR`, `SUPER_SENIOR_CITIZEN` | SUPER_SENIOR (+1.50%) |

Anything else — `VIP`, for instance — is rejected with a clear message naming the unknown value, deliberately, so a category never silently earns nothing. If their module uses a category not in this list, add it to `LocalRateProvider` (Step 5) or, better, to the rate matrix in product-service.

4. **Port.** Their service must not use **8082**, because your calculator is on it. The seniors' customer-service uses 8082, so if Group 10 copied it, one of you has to change. The easy fix is theirs (`server.port` in their config); if it has to be yours, change `server.port` in `application.properties`, both `target` lines for 8082 in `proxy.conf.json`, and the Swagger address you type in the browser.

**If Group 10 built on the seniors' Credexa customer-service** (likely — same assignment, same college), the answers to all four questions are already known, from the ZIP you uploaded:

| Question | Credexa's answer |
|---|---|
| JWT secret | `mySecretKeyForCredexaApplicationMustBe256BitsLongForHS256AlgorithmToWorkProperly` |
| Base64? | **No** — it's plain text, so `secret-base64=false` |
| Profile address | `GET /api/customer/profile` — matches the default already in your settings |
| Category field | `classification`, with values `REGULAR`, `PREMIUM`, `VIP`, `SENIOR_CITIZEN`, `SUPER_SENIOR` |
| Their port | **8082** — clashes with your calculator, so one of you must change |

Confirm each of these with Group 10 rather than assuming; they may have changed them.

**Backend changes** (edit these lines in `calculator-service/src/main/resources/application.properties`, then save):

| Setting | Change to |
|---|---|
| `calculator.jwt.secret` | Group 10's secret |
| `calculator.jwt.secret-base64` | `true` or `false` (see point 1 above) |
| `calculator.dev-auth.enabled` | `false` |
| `calculator.customer.source` | `REMOTE` |
| `calculator.customer.base-url` | `http://localhost:8080` (or wherever their service runs) |

**Frontend change:** in `banking-frontend/proxy.conf.json`, inside the `"/api/auth"` block, change

```json
    "target": "http://localhost:8082",
```

to

```json
    "target": "http://localhost:8080",
```

Save, then restart **both**: backend (`Ctrl+C`, then run again) and frontend (`Ctrl+C`, then `npm start`). The proxy file is read only at start-up.

**Test:**
1. Log in on http://localhost:4200 with a CUSTOMER account created through Group 10's registration.
2. The calculator shows that customer's profile category.
3. A BANK_OFFICER account gets "Access denied".

**If login works but the calculator gives 401:** the secret or the Base64 setting doesn't match. Paste a token into https://jwt.io to see its contents, and compare the secret settings.

**If you get "No customer profile exists for your login":** a bank officer must create a customer profile for that user in Group 10's module.

To go back to the demo users at any time, undo these changes.

---

## Step 17: Live base rates from your teammates' product-service

Lab L6 says to "integrate with Product and Pricing module for fetching base rates", and L11 says to "cap the effective rate based on product-defined maximums". Do this when Ananya's and Lavanya's product-service is running on port 8081.

### 17a. Two new backend files

**File:** `calculator-service/src/main/java/com/banklab/calculator/client/ProductClient.java`
**Action:** Create this file

```java
package com.banklab.calculator.client;

import com.banklab.calculator.exception.CalculationException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.List;

/**
 * Talks to your teammates' product-service (Lab L6: "integrate with Product and
 * Pricing module for fetching base rates"). Paths come from application.properties
 * so they can be matched to Ananya's/Lavanya's real code without changing Java.
 *
 * Team rule (from your plan): 3-second timeout, and if product-service is down
 * we answer 503 - we NEVER silently fall back to a made-up rate.
 */
@Component
public class ProductClient {

    private final RestClient restClient;
    private final String baseUrl;
    private final String productsPath;
    private final String ratesPath;
    private final String categoriesPath;

    public ProductClient(@Value("${calculator.product.base-url:http://localhost:8081}") String baseUrl,
                         @Value("${calculator.product.products-path:/products}") String productsPath,
                         @Value("${calculator.product.rates-path:/api/product-rate}") String ratesPath,
                         @Value("${calculator.product.categories-path:/api/customer-category}") String categoriesPath,
                         @Value("${calculator.product.timeout-ms:3000}") int timeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(timeoutMs));
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
        this.baseUrl = baseUrl;
        this.productsPath = productsPath;
        this.ratesPath = ratesPath;
        this.categoriesPath = categoriesPath;
    }

    public JsonNode getProduct(String productCode) {
        return get(productsPath + "/" + productCode, "Product " + productCode);
    }

    public List<JsonNode> listProducts() {
        return JsonFields.items(get(productsPath + "?limit=500", "Product list"));
    }

    public List<JsonNode> listRates() {
        return JsonFields.items(get(ratesPath + "?limit=500", "Rate matrix"));
    }

    public List<JsonNode> listCategories() {
        return JsonFields.items(get(categoriesPath + "?limit=500", "Customer categories"));
    }

    private JsonNode get(String path, String what) {
        try {
            RestClient.RequestHeadersSpec<?> call = restClient.get().uri(path);
            String auth = currentAuthorizationHeader();
            if (auth != null) {
                call = call.header(HttpHeaders.AUTHORIZATION, auth);
            }
            return call.retrieve().body(JsonNode.class);
        } catch (ResourceAccessException e) {
            throw CalculationException.serviceUnavailable("PRODUCT_SERVICE_UNAVAILABLE",
                    "Product service is not reachable at " + baseUrl + " (waited 3 seconds). Is it running?");
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            if (status == 404) {
                throw CalculationException.notFound("PRODUCT_NOT_FOUND", what + " was not found in product-service.");
            }
            throw CalculationException.badGateway("PRODUCT_SERVICE_ERROR",
                    "Product service answered HTTP " + status + " for " + what + ".");
        }
    }

    /** Passes the logged-in user's token on to product-service (its GET endpoints need a token). */
    private static String currentAuthorizationHeader() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest request = attrs.getRequest();
            return request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }
}
```

**What it does:**
- Calls product-service with the user's token, waiting at most 3 seconds.
- If product-service is down it answers **503**. It never makes up a rate (your team's rule).

**File:** `calculator-service/src/main/java/com/banklab/calculator/rates/ProductRateProvider.java`
**Action:** Create this file

```java
package com.banklab.calculator.rates;

import com.banklab.calculator.client.JsonFields;
import com.banklab.calculator.client.ProductClient;
import com.banklab.calculator.dto.CategoryInfo;
import com.banklab.calculator.dto.ProductInfo;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.model.CategoryCodes;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Rates from your teammates' product-service (Lab L6: "integrate with Product and
 * Pricing module for fetching base rates"; Lab L11: "cap the effective rate based
 * on product-defined maximums").
 *
 * Uses the tables from your Product & Pricing ERD:
 *   PRODUCTS            -> STATUS, MIN_AMOUNT, MAX_AMOUNT, MAX_RATE
 *   PRODUCT_RATE_MATRIX -> PRODUCT_CODE, CATEGORY_CODE, MIN/MAX_TENURE_MONTHS,
 *                          MIN/MAX_AMOUNT, BASE_RATE, ADDITIONAL_RATE_PERCENT,
 *                          EFFECTIVE_DATE, EXPIRY_DATE
 *   CUSTOMER_CATEGORY   -> CATEGORY_CODE, CATEGORY_NAME
 *
 * Active only when calculator.rates.source=PRODUCT.
 */
@Component
@ConditionalOnProperty(name = "calculator.rates.source", havingValue = "PRODUCT")
public class ProductRateProvider implements RateProvider {

    private static final ZoneId INDIA = ZoneId.of("Asia/Kolkata");

    private final ProductClient productClient;
    private final BigDecimal addOnCap;

    public ProductRateProvider(ProductClient productClient,
                               @Value("${calculator.rates.add-on-cap:2.00}") BigDecimal addOnCap) {
        this.productClient = productClient;
        this.addOnCap = addOnCap;
    }

    @Override
    public String sourceName() {
        return "PRODUCT";
    }

    @Override
    public boolean usesRequestBaseRate() {
        return false;
    }

    @Override
    public BigDecimal addOnCap() {
        return addOnCap;
    }

    @Override
    public List<ProductInfo> listProducts() {
        List<ProductInfo> result = new ArrayList<>();
        for (JsonNode p : productClient.listProducts()) {
            String type = JsonFields.text(p, "PRODUCT_TYPE", "productType");
            String status = JsonFields.text(p, "STATUS", "status");
            boolean isFd = type == null || type.toUpperCase(Locale.ROOT).contains("FD")
                    || type.toUpperCase(Locale.ROOT).contains("FIXED");
            boolean active = status == null || status.equalsIgnoreCase("ACTIVE");
            if (isFd && active) {
                result.add(toProductInfo(p));
            }
        }
        return result;
    }

    @Override
    public List<CategoryInfo> listCategories(String productCode) {
        Map<String, String> names = new LinkedHashMap<>();
        for (JsonNode c : productClient.listCategories()) {
            String code = CategoryCodes.normalize(JsonFields.text(c, "CATEGORY_CODE", "categoryCode", "code"));
            if (code != null) {
                names.put(code, JsonFields.text(c, "CATEGORY_NAME", "categoryName", "name"));
            }
        }
        Map<String, BigDecimal> addOns = new LinkedHashMap<>();
        if (productCode != null && !productCode.isBlank()) {
            for (JsonNode row : activeRows(productCode)) {
                String code = CategoryCodes.normalize(JsonFields.text(row, "CATEGORY_CODE", "categoryCode"));
                if (code != null && !addOns.containsKey(code)) {
                    BigDecimal add = JsonFields.decimal(row, "ADDITIONAL_RATE_PERCENT", "additionalRatePercent");
                    addOns.put(code, add == null ? BigDecimal.ZERO : add);
                }
            }
        }
        List<CategoryInfo> result = new ArrayList<>();
        names.forEach((code, name) -> result.add(new CategoryInfo(code, name == null ? code : name, addOns.get(code))));
        return result;
    }

    @Override
    public RateInputs getRateInputs(String productCode,
                                    BigDecimal requestedBaseRate,
                                    List<String> categoryCodes,
                                    BigDecimal principal,
                                    int tenureMonths) {
        if (productCode == null || productCode.isBlank()) {
            throw CalculationException.badRequest("PRODUCT_REQUIRED",
                    "Please choose an FD product (rates come from product-service).");
        }
        JsonNode product = productClient.getProduct(productCode);

        String status = JsonFields.text(product, "STATUS", "status");
        if (status != null && !status.equalsIgnoreCase("ACTIVE")) {
            throw CalculationException.unprocessable("PRODUCT_NOT_ACTIVE",
                    "Product " + productCode + " is not active (status " + status + ").");
        }
        BigDecimal minAmount = JsonFields.decimal(product, "MIN_AMOUNT", "minAmount");
        BigDecimal maxAmount = JsonFields.decimal(product, "MAX_AMOUNT", "maxAmount");
        if (minAmount != null && principal.compareTo(minAmount) < 0) {
            throw CalculationException.unprocessable("AMOUNT_OUT_OF_RANGE",
                    "Minimum deposit for " + productCode + " is " + minAmount.toPlainString() + ".");
        }
        if (maxAmount != null && principal.compareTo(maxAmount) > 0) {
            throw CalculationException.unprocessable("AMOUNT_OUT_OF_RANGE",
                    "Maximum deposit for " + productCode + " is " + maxAmount.toPlainString() + ".");
        }

        // Rows for this product whose date window, term band and amount band fit this deposit
        List<JsonNode> rows = new ArrayList<>();
        for (JsonNode row : activeRows(productCode)) {
            if (fits(row, tenureMonths, principal)) {
                rows.add(row);
            }
        }
        if (rows.isEmpty()) {
            throw CalculationException.unprocessable("NO_RATE_CONFIGURED",
                    "No interest rate is configured in product " + productCode + " for "
                            + tenureMonths + " months and this amount.");
        }

        // Base rate: the GENERAL (or category-less) row; otherwise the first matching row
        JsonNode baseRow = rows.get(0);
        for (JsonNode row : rows) {
            String code = CategoryCodes.normalize(JsonFields.text(row, "CATEGORY_CODE", "categoryCode"));
            if (code == null || code.equals("GENERAL")) {
                baseRow = row;
                break;
            }
        }
        BigDecimal baseRate = JsonFields.decimal(baseRow, "BASE_RATE", "baseRate");

        // Add-on for every selected category
        Map<String, BigDecimal> addOns = new LinkedHashMap<>();
        List<String> notOffered = new ArrayList<>();
        for (String category : categoryCodes) {
            if (category.equals("GENERAL")) {
                addOns.put(category, BigDecimal.ZERO);
                continue;
            }
            BigDecimal add = null;
            boolean found = false;
            for (JsonNode row : rows) {
                String code = CategoryCodes.normalize(JsonFields.text(row, "CATEGORY_CODE", "categoryCode"));
                if (category.equals(code)) {
                    add = JsonFields.decimal(row, "ADDITIONAL_RATE_PERCENT", "additionalRatePercent");
                    found = true;
                    break;
                }
            }
            if (found) {
                addOns.put(category, add == null ? BigDecimal.ZERO : add);
            } else {
                notOffered.add(category);
            }
        }
        if (!notOffered.isEmpty()) {
            throw CalculationException.unprocessable("CATEGORY_NOT_OFFERED",
                    "Product " + productCode + " has no rate for category " + String.join(", ", notOffered)
                            + " for this term and amount.");
        }

        BigDecimal maxRate = JsonFields.decimal(product, "MAX_RATE", "maxRate");
        return new RateInputs(baseRate, addOns, addOnCap, maxRate, "PRODUCT:" + productCode);
    }

    private List<JsonNode> activeRows(String productCode) {
        LocalDate today = LocalDate.now(INDIA);
        List<JsonNode> result = new ArrayList<>();
        for (JsonNode row : productClient.listRates()) {
            String code = JsonFields.text(row, "PRODUCT_CODE", "productCode");
            if (code == null || !code.equalsIgnoreCase(productCode)) {
                continue;
            }
            LocalDate from = JsonFields.date(row, "EFFECTIVE_DATE", "effectiveDate");
            LocalDate to = JsonFields.date(row, "EXPIRY_DATE", "expiryDate");
            if ((from == null || !from.isAfter(today)) && (to == null || !to.isBefore(today))) {
                result.add(row);
            }
        }
        return result;
    }

    private static boolean fits(JsonNode row, int tenureMonths, BigDecimal principal) {
        Integer minT = JsonFields.integer(row, "MIN_TENURE_MONTHS", "minTenureMonths");
        Integer maxT = JsonFields.integer(row, "MAX_TENURE_MONTHS", "maxTenureMonths");
        BigDecimal minA = JsonFields.decimal(row, "MIN_AMOUNT", "minAmount");
        BigDecimal maxA = JsonFields.decimal(row, "MAX_AMOUNT", "maxAmount");
        return (minT == null || tenureMonths >= minT)
                && (maxT == null || tenureMonths <= maxT)
                && (minA == null || principal.compareTo(minA) >= 0)
                && (maxA == null || principal.compareTo(maxA) <= 0);
    }

    private static ProductInfo toProductInfo(JsonNode p) {
        return new ProductInfo(
                JsonFields.text(p, "PRODUCT_CODE", "productCode"),
                JsonFields.text(p, "PRODUCT_NAME", "productName"),
                JsonFields.decimal(p, "MIN_RATE", "minRate"),
                JsonFields.decimal(p, "MAX_RATE", "maxRate"),
                JsonFields.decimal(p, "MIN_AMOUNT", "minAmount"),
                JsonFields.decimal(p, "MAX_AMOUNT", "maxAmount"),
                JsonFields.text(p, "DEFAULT_COMPOUNDING_FREQUENCY", "defaultCompoundingFrequency"));
    }
}
```

**What it does:**
- Uses `PRODUCTS`, `PRODUCT_RATE_MATRIX` and `CUSTOMER_CATEGORY` from your ERD.
- Picks the matrix rows for the chosen product whose date window, term band and amount band fit the deposit.
- Takes the base rate from the GENERAL row, and each category's `ADDITIONAL_RATE_PERCENT`.
- Caps the final rate at the product's `MAX_RATE`.

### 17b. Agree the data with your teammates
Product-service should contain, for example, product **FD001** (STATUS `ACTIVE`, MIN_AMOUNT 1000, MAX_AMOUNT 100000000, MAX_RATE 8.50) with these rate-matrix rows (MIN_TENURE_MONTHS 1, MAX_TENURE_MONTHS 120, EFFECTIVE_DATE in the past, EXPIRY_DATE empty):

| CATEGORY_CODE | BASE_RATE | ADDITIONAL_RATE_PERCENT |
|---|---|---|
| GENERAL | 6.50 | 0.00 |
| SENIOR | 6.50 | 1.00 |
| EMPLOYEE | 6.50 | 1.00 |
| PREMIUM_USER | 6.50 | 2.00 |

**Important — permissions:** your product spec (`x-role-access`) allows only BANK_OFFICER/ADMIN to read `/product-rates`. The calculator forwards the logged-in **customer's** token, so product-service would refuse it with 403, and the page would show "Product service answered HTTP 403 for Rate matrix". Ask your teammates to allow **CUSTOMER** for **GET** on the rate matrix. It's read-only, and customers need it to see rates.

Also confirm the **exact addresses** of their three list endpoints (products, rate matrix, categories). Their YAML shows `/products`, `/api/product-rate` and `/api/customer-category`, but their real code may differ.

### 17c. Switch on product rates
In `application.properties`:

| Setting | Change to |
|---|---|
| `calculator.rates.source` | `PRODUCT` (capital letters) |
| `calculator.product.base-url` | `http://localhost:8081` |
| `calculator.product.products-path` / `rates-path` / `categories-path` | Their real paths |

Save and restart the backend.

**Test:**
1. Refresh the calculator. A **FD product** dropdown appears, and the base-rate box is replaced by "The base rate comes from the selected product".
2. Choose FD001, enter `100000`, 12 Months, Simple → as alice123 **₹1,08,500.00**, the same as before, now driven by product data. "Rate source" shows `PRODUCT:FD001`.
3. Stop product-service and calculate again → "Product service is not reachable at http://localhost:8081 (waited 3 seconds)…".

To go back to the built-in table, set `calculator.rates.source=LOCAL` and restart.


---

## Step 18 — Final testing checklist

### 18.1 What must be running

| Terminal | Folder | Command | Must show |
|---|---|---|---|
| 1 | `banking-lab/calculator-service` | Windows `.\mvnw.cmd spring-boot:run` · Mac `./mvnw spring-boot:run` | `Started CalculatorServiceApplication` |
| 2 | `banking-lab/banking-frontend` | `npm start` | `Local: http://localhost:4200/` |
| (Step 16 only) | Group 10's project | their start command | Running on 8080 |
| (Step 17 only) | Product-service project | their start command | Running on 8081 |

### 18.2 Addresses

| URL | What |
|---|---|
| http://localhost:4200 | The app (Home) |
| http://localhost:4200/login | Login page (Phase C) |
| http://localhost:4200/fd-calculator | FD Calculator |
| http://localhost:8082/swagger-ui.html | Backend test page |
| http://localhost:8082/actuator/health | Backend health: `{"status":"UP"}` |

### 18.3 Functional tests (Phase C settings, logged in as the user shown)

| # | User | Inputs | Click | Expected |
|---|---|---|---|---|
| 1 | alice123 | 100000, 12 Months, 6.5, Simple | Calculate | ₹1,08,500.00; interest ₹8,500.00; rate 8.50%; categories Senior + Employee |
| 2 | ravi | 100000, 12 Months, 6.5, Simple | Calculate | ₹1,08,500.00 + yellow notice "add-ons total +3.00% … +2.00% was applied" |
| 3 | bob | 100000, 12 Months, 6.5, Compound, Quarterly | Calculate | ₹1,06,660.16; APY 6.66% |
| 4 | bob | 100000, 1 Month, 6.6, Compound, Monthly | Calculate | Interest ₹550.00 |
| 5 | bob | 25000, 18 Months, 7.25, Simple | Calculate | ₹27,718.75 |
| 6 | bob | 100000, 5 Months, 7.0, Compound, Quarterly | Calculate | ₹1,02,937.08 |
| 7 | alice123 | 50000, 24 Months, 7.0, Compound, Monthly | Calculate | Rate 9.00% (7 + 2), ₹59,820.68, interest ₹9,820.68* |
| 8 | alice123 | after test 1 | Add to report | Green box with `alice123,100000,12M,6.5,2.0,8.5,108500` |
| 9 | alice123 | – | Download my report | CSV file with header + rows |

\*Test 7 uses alice123's two categories. **Sample 4 (₹58,644.40)** needs Senior only. Reproduce it in Phase A/B mode, or with `calculator.categories.source=REQUEST` (Step 18.6).

### 18.4 Invalid input tests

| Input | Expected message |
|---|---|
| Amount empty | Please enter the deposit amount. |
| Amount `-5000` | Deposit amount must be greater than 0. |
| Amount `500` | Minimum deposit amount is ₹1,000. |
| Amount `100000.555` | Deposit amount can have at most 2 decimal places (paise). |
| Term `0` Months or `200` Months | For MONTHS, the term must be between 1 and 120. |
| Term `3` Days | For DAYS, the term must be between 7 and 3650. |
| Term `2.5` | Term must be a whole number. |
| Base rate empty / `0` / `40` | Please enter the base rate. / must be greater than 0. / cannot be more than 15% per year. |
| Wrong password | Invalid username or password. |
| officer1 opens /fd-calculator | Access denied … only available to customers |
| Backend stopped | Cannot reach the FD Calculator service. Is the backend running on port 8082? |

### 18.5 Security tests (Swagger, Step 14g)

| Call | Expected |
|---|---|
| POST /api/fd/calculate, no token | 401 UNAUTHORIZED |
| Same, officer1 token | 403 FORBIDDEN |
| Same, alice123 token | 200, categories from profile |
| Token edited by one letter | 401 |

### 18.6 Useful setting combinations

| Situation | `security.enabled` | `categories.source` | `dev-auth.enabled` | `customer.source` | `rates.source` |
|---|---|---|---|---|---|
| L6 demo, no login | `false` | `REQUEST` | `true` | `DEV` | `LOCAL` |
| L10/L14 demo, demo users | `true` | `PROFILE` | `true` | `DEV` | `LOCAL` |
| Real Group 10 login | `true` | `PROFILE` | `false` | `REMOTE` | `LOCAL` |
| Full integration | `true` | `PROFILE` | `false` | `REMOTE` | `PRODUCT` |

All setting names start with `calculator.` in `application.properties`. Restart the backend after any change.

When `security.enabled=false`, the Angular route guard still asks for login, which is fine for the demo. The backend simply doesn't require it.

### 18.7 Does the new module break anything else?

- The calculator is a **separate service on its own port** with **no database**, so it can't change other teams' data or tables.
- Its addresses (`/api/fd/calculate`, `/api/fd/categories`, `/api/fd/products`, `/api/fd/my-categories`, `/api/fd/report…`, `/api/calculator/fd`) are its own. **Tell the FD Account team (Group 2)** that the calculator uses `/api/fd/calculate` and these other `/api/fd/…` addresses, so their future account endpoints don't reuse them. Their spec already uses `/fd-accounts` and `/api/fd/account/…`, which don't collide.
- It only **reads** from Group 10 and product-service, using the customer's own token.
- Regression check after every change:
  1. `mvnw test` still shows **23 tests, 0 failures**.
  2. The Swagger health check is UP.
  3. Samples 1 and 3 still give ₹1,08,500.00 and ₹1,06,660.16.

---

## Step 19 — Troubleshooting

### Backend (terminal 1)

| Symptom | Cause | Fix |
|---|---|---|
| `COMPILATION ERROR … cannot find symbol` | A file is missing, unsaved, or in the wrong folder | Check the file exists at the exact path in the guide, and save all (`Ctrl+K` then `S` saves all in VS Code) |
| `class X is public, should be declared in a file named X.java` | File name doesn't match the class | Rename the file exactly, including capital letters |
| `package com.banklab.calculator.xyz does not exist` | Folder name wrong (e.g. `Model` instead of `model`) | Rename the folder to lowercase exactly as in the guide |
| `Port 8082 was already in use` | Backend already running in another terminal | Close the other terminal (trash icon) or stop it with `Ctrl+C` |
| `required a bean of type 'RateProvider' that could not be found` | `calculator.rates.source` misspelled | Use exactly `LOCAL` or `PRODUCT` |
| `calculator.jwt.secret must be at least 32 characters` | Secret too short | Use a longer secret (agree it with Group 10) |
| `Could not resolve placeholder 'calculator.jwt.secret'` | Phase C Java files added but properties not replaced | Do Step 14b |
| `Could not start Python using the command 'python'` | Wrong Python command | Mac: `python3`; Windows: reinstall Python with "Add to PATH" |
| `Python report script not found` | Script not in `calculator-service/scripts/` or backend started from another folder | Move the file; start the backend from inside `calculator-service` |
| Tests fail only in `contextLoads` | The backend was running during `mvnw test` | Stop it first, then rerun the tests |
| Download errors on first run | Network blocks Maven | Try another network (mobile hotspot) |

### Frontend (terminal 2 and browser)

| Symptom | Cause | Fix |
|---|---|---|
| `Cannot reach the FD Calculator service…` | Backend not running, or frontend started with `ng serve` instead of `npm start` | Start the backend; restart the frontend with `npm start` |
| Terminal 2 shows `[vite] http proxy error … ECONNREFUSED` | Same as above | Start the backend |
| `NG8001: 'calc-fd-calculator' is not a known element` or `Cannot find module './calculator/fd-calculator'` | File name or path typo | File must be `src/app/calculator/fd-calculator.ts` exactly |
| `TS2307: Cannot find module './auth/auth.interceptor'` | Phase C frontend files missing | Create all files in Step 15 |
| Page is blank | An error happened while starting the app | Press `F12` → **Console** tab, read the red message, and check the file it names |
| Login says "Login service not found" | Proxy points to a service without `/api/auth/login` | Dev login: `dev-auth.enabled=true` and proxy `/api/auth` → 8082. Real login: proxy → 8080 |
| Logged in, but calculator shows "Please log in first" | Token secret mismatch (Step 16) | Match `calculator.jwt.secret` and `secret-base64` with Group 10 |
| Changes to `proxy.conf.json` have no effect | Proxy is read only at start | Stop terminal 2 (`Ctrl+C`) and run `npm start` again |
| `ng: command not found` / `ng.ps1 cannot be loaded` | See Step 1 | Reinstall the CLI / fix the execution policy |

### How to read an error
1. Read the **first** red line, not the last one.
2. Look for a **file name and line number**, e.g. `FdCalculatorService.java:[120,17]` means line 120, character 17.
3. Open that file and compare it with the guide. Most errors are a missing `}`, a missing file, or an unsaved file.
4. For browser problems, press `F12` → **Console** and **Network** tabs. Click the failed request to see the status code and the JSON error message.


---

## Appendix A — What every file does

| File | Phase | Purpose |
|---|---|---|
| `pom.xml` | A, C | Libraries and versions |
| `application.properties` | A, B, C | All settings: port, cap, security, sources, Python |
| `CalculatorServiceApplication.java` | A | Starts the backend |
| `model/*` | A | Interest types, frequencies, term units, category-name cleanup |
| `dto/*` | A, B | JSON shapes in and out |
| `exception/CalculationException.java` | A | Our error type with HTTP status |
| `exception/GlobalExceptionHandler.java` | A | Turns every error into friendly JSON |
| `rates/RateProvider.java`, `RateInputs.java` | A | The "plug" for rate data |
| `rates/LocalRateProvider.java` | A | Built-in rate table (Senior +1, Employee +1, Premium +2, Super Senior +1.5, cap 2) |
| `rates/ProductRateProvider.java` | C | Rates from product-service |
| `service/FdCalculatorService.java` | A | Validation, cap, formulas (the core) |
| `service/CategorySelectionService.java` | A → C | Which categories are used (page vs profile) |
| `controller/FdCalculatorController.java` | A | API doors for the calculator |
| `controller/FdReportController.java` | B | API doors for the CSV report |
| `report/FdReportService.java` | B | Starts the Python script |
| `scripts/generate_fd_report.py` | B | Writes the CSV row (Lab L11) |
| `config/CorsConfig.java` | A | Allows the Angular address |
| `config/SecurityConfig.java` | C | CUSTOMER-only rules, 401/403 answers |
| `config/OpenApiConfig.java` | C | Swagger "Authorize" button |
| `security/JwtService.java`, `JwtAuthFilter.java` | C | Create and check login tokens |
| `auth/DevUserStore.java`, `DevAuthController.java` | C | Dev login stand-in |
| `client/CustomerClient.java` | C | Reads the customer's category from Group 10 |
| `client/ProductClient.java`, `JsonFields.java` | C | Reads product-service data |
| `FdCalculatorServiceTest.java` | B | 22 automatic checks of the maths, validation and category mapping |
| `proxy.conf.json` | A | Forwards `/api/...` from 4200 to 8082 |
| `styles.css`, `app.css` | A | Shared look and navigation bar |
| `calculator/*` | A | The calculator page, its service and data shapes |
| `home/*` | A | Dashboard |
| `auth/*` | C | Login page, token storage, interceptor, route guard |

---

## Appendix B — Viva preparation

**Why BigDecimal and not double?** `double` stores numbers in binary and can't represent 0.1 exactly, so money totals drift by fractions of a paisa. `BigDecimal` does exact decimal arithmetic.

**Why sum the add-ons first and cap once?** The manual says the *combined* additional percentage must not exceed the cap. Capping each category separately would allow 1.5 + 1.5 = 3%.

**Why does 5 months with quarterly compounding use simple interest for part of it?** 5 months = 1 full quarter + ⅔ of a quarter. Only full periods can compound, and the leftover earns simple interest on the grown balance, as Indian banks do.

**Why no database?** Calculation is stateless maths (the FD team's spec agrees). Rates belong to product-service, and the L11 report is a CSV file.

**Where is authorization enforced?** On the **backend** (SecurityConfig: CUSTOMER only; 401 without a token; 403 for other roles). The Angular guard only improves the experience, following the manual's "API must always reject unauthorized access even if frontend UI is bypassed".

**How does the calculator know the customer's category?** From the token it knows the username. It then reads the customer's profile (Group 10's `/api/customer/profile` → `custCategoryCode`), or the demo user store in dev mode.

**What happens if product-service is down?** 503 with a clear message after a 3-second timeout, and never a made-up rate. The seniors' project silently fell back to a hard-coded rate; we deliberately don't.

**Why does the manual's L6 sample say ₹107000 and yours ₹108500?** The L6 sample contradicts its own rule (Senior +1%). Our result matches the L11 table and CSV row (8.5%, ₹108500).

**What is APY?** The effective yearly yield of a compound rate: (1 + r/n)^n − 1. 6.5% compounded quarterly is worth 6.66% a year.

---

## Appendix C — Saving your work with Git (optional)

Git keeps a history of your files, so you can undo mistakes and share code with your team.

1. Install Git from https://git-scm.com (accept the defaults), then restart VS Code.
2. In a terminal inside `banking-lab`, run once:

```
git init
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

3. Create a file `banking-lab/.gitignore` so downloaded libraries and build output aren't saved, with this content:

```
node_modules/
dist/
.angular/
target/
reports/
```

4. Save a snapshot whenever something works:

```
git add .
git commit -m "Phase A: FD calculator working"
```

5. To share with your team, create an empty repository on GitHub, then follow the "push an existing repository" commands GitHub shows you.

---

*End of guide.*
