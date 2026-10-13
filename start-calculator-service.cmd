@echo off
REM ============================================================
REM  Starts calculator-service (FD Calculator backend) on port 8082.
REM  It has no database. It reads its JWT secret from
REM  local-config\dev-secrets.properties and fetches products and
REM  rates from product-service (port 8083) - start that first.
REM  Stop it: press Ctrl + C in this terminal.
REM ============================================================
cd /d "%~dp0calculator-service"
if not exist "..\local-config\dev-secrets.properties" (
  echo WARNING: local-config\dev-secrets.properties not found - the test-only placeholder
  echo          secret will be used and product-service will reject the calculator's calls.
  echo          Create it with:  python integration\setup_local_config.py
)
call "%~dp0calculator-service\mvnw.cmd" spring-boot:run
