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
    """6.50 -> 6.5 ; 2.00 -> 2.0 ; 10.00 -> 10.0 ; 7.25 -> 7.25 (same style as the manual)."""
    value = Decimal(text).normalize()
    if value == value.to_integral_value():
        # format(..., "f") avoids scientific notation such as 1E+1 for 10
        return format(value.to_integral_value(), "f") + ".0"
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
