"""Print a short-lived local JWT for the Product & Pricing demo profile."""

import base64
import hashlib
import hmac
import json
import os
import sys
import time


def encoded(value):
    data = json.dumps(value, separators=(",", ":")).encode("utf-8")
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode("ascii")


role = sys.argv[1].upper() if len(sys.argv) > 1 else "ADMIN"
secret = os.environ.get("JWT_SECRET", "change-me-in-all-services-32-chars-minimum")
if len(secret) < 32:
    raise SystemExit("JWT_SECRET must be at least 32 characters")

now = int(time.time())
header = encoded({"alg": "HS256", "typ": "JWT"})
payload = encoded({"sub": "demo-professor", "role": role, "iat": now, "exp": now + 4 * 60 * 60})
signing_input = f"{header}.{payload}".encode("ascii")
signature = base64.urlsafe_b64encode(
    hmac.new(secret.encode("utf-8"), signing_input, hashlib.sha256).digest()
).rstrip(b"=").decode("ascii")
sys.stdout.write(f"{header}.{payload}.{signature}")
