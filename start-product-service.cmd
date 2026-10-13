@echo off
REM ============================================================
REM  Starts your teammates' product-service for FD Calculator integration.
REM
REM  - Port 8083 (calculator-service is 8082, Angular is 4200)
REM  - Default: MySQL database product_db_dev (data is SAVED between restarts).
REM    The tables come from product-service's own 9 Flyway migrations.
REM  - "demo": old in-memory H2 database (data is LOST when it stops;
REM    run integration\seed_product_data.py after each demo start).
REM  - Secrets (JWT secret, MySQL user/password) are read from
REM    local-config\dev-secrets.properties, which is never shared.
REM  - Built with Java 17 (their pom.xml asks for 21; their code does not need it).
REM
REM  Usage:   start-product-service.cmd                 MySQL (normal)
REM           start-product-service.cmd demo            in-memory demo database (fallback)
REM           start-product-service.cmd rebuild         rebuild after code changes, then MySQL
REM           start-product-service.cmd demo rebuild    rebuild, then demo
REM  Stop it: press Ctrl + C in this terminal.
REM ============================================================
cd /d "%~dp0"

set "SECRETS=local-config\dev-secrets.properties"
if not exist "%SECRETS%" (
  echo ERROR: %SECRETS% not found.
  echo        It holds the JWT secret and MySQL password for this laptop.
  echo        Create it with:  python integration\setup_local_config.py   - see README_INTEGRATION.md section 5
  exit /b 1
)

set "JWT_SECRET="
set "DB_URL="
set "DB_USERNAME="
set "DB_PASSWORD="
for /f "usebackq tokens=1,* delims==" %%A in ("%SECRETS%") do (
  if "%%A"=="calculator.jwt.secret" set "JWT_SECRET=%%B"
  if "%%A"=="product.db.url" set "DB_URL=%%B"
  if "%%A"=="product.db.username" set "DB_USERNAME=%%B"
  if "%%A"=="product.db.password" set "DB_PASSWORD=%%B"
)
if not defined JWT_SECRET (
  echo ERROR: calculator.jwt.secret is missing from %SECRETS%
  exit /b 1
)

set SERVER_PORT=8083
set "MODE=mysql"
set "REBUILD="
if /i "%~1"=="demo" set "MODE=demo"
if /i "%~2"=="demo" set "MODE=demo"
if /i "%~1"=="rebuild" set "REBUILD=1"
if /i "%~2"=="rebuild" set "REBUILD=1"

if "%MODE%"=="demo" (
  set SPRING_PROFILES_ACTIVE=demo
  set "DB_URL="
  set "DB_USERNAME="
  set "DB_PASSWORD="
) else (
  set "SPRING_PROFILES_ACTIVE="
  if not defined DB_URL (
    echo ERROR: product.db.url is missing from %SECRETS%
    exit /b 1
  )
)

set JAR=product-service\target\product-service-1.0.0.jar
if defined REBUILD goto build
if exist "%JAR%" goto run

:build
echo Building product-service with Java 17 (first time takes a minute)...
call "%~dp0calculator-service\mvnw.cmd" -q -B -f product-service\pom.xml -Djava.version=17 -DskipTests package
if errorlevel 1 (
  echo ERROR: product-service build failed.
  exit /b 1
)

:run
if "%MODE%"=="demo" (
  echo Starting product-service on http://localhost:8083 with the in-memory DEMO database ...
  echo Data is lost when it stops. Run: python integration\seed_product_data.py
) else (
  echo Starting product-service on http://localhost:8083 with MySQL database product_db_dev ...
)
java -jar "%JAR%"
