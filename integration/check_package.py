#!/usr/bin/env python3
"""
check_package.py - confirms that the extracted package is complete and unchanged.

Run from the extracted project folder:
    python integration\\check_package.py

It reads PACKAGE_MANIFEST.txt (one line per file: SHA-256, two spaces, path)
and checks that every listed file exists and has exactly the listed content.
It also prints how many files are in the key folders, so you can compare them
with README_INTEGRATION.md section 0.

Exit code 0 = complete, 1 = something is missing or different.
Files you create yourself (local-config\\dev-secrets.properties, node_modules,
target, reports) are not part of the package and are ignored.
"""
import hashlib
import os
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MANIFEST = os.path.join(ROOT, "PACKAGE_MANIFEST.txt")

KEY_FOLDERS = [
    ("calculator-service/src/main/java", ".java"),
    ("calculator-service/src/test/java", ".java"),
    ("product-service/src/main/java", ".java"),
    ("product-service/src/test/java", ".java"),
    ("product-service/src/main/resources/db/migration", ".sql"),
    ("banking-frontend/src", ""),
    ("integration", ".py"),
    ("database", ".sql"),
]


def sha256(path):
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(65536), b""):
            h.update(chunk)
    return h.hexdigest().upper()


def same_except_line_endings(path, digest):
    """True if the file only differs in Windows/Unix line endings (Git may convert
    them on clone, e.g. *.cmd files), not in its actual content."""
    with open(path, "rb") as f:
        data = f.read()
    lf = data.replace(b"\r\n", b"\n")
    for variant in (lf, lf.replace(b"\n", b"\r\n")):
        if hashlib.sha256(variant).hexdigest().upper() == digest:
            return True
    return False


def main():
    if not os.path.exists(MANIFEST):
        print("PACKAGE_MANIFEST.txt not found - run this from the extracted package folder.")
        return 1
    entries = []
    with open(MANIFEST, encoding="utf-8") as f:
        for line in f:
            line = line.rstrip("\n")
            if not line or line.startswith("#"):
                continue
            digest, rel = line.split("  ", 1)
            entries.append((digest, rel))

    missing, changed, line_endings = [], [], []
    for digest, rel in entries:
        path = os.path.join(ROOT, *rel.split("/"))
        if not os.path.isfile(path):
            missing.append(rel)
        elif sha256(path) != digest:
            if same_except_line_endings(path, digest):
                line_endings.append(rel)
            else:
                changed.append(rel)

    print(f"Files listed in PACKAGE_MANIFEST.txt: {len(entries)}")
    print(f"  present and unchanged: {len(entries) - len(missing) - len(changed)}")
    if line_endings:
        print(f"    (of which {len(line_endings)} differ only in line endings - normal after git clone:"
              f" {', '.join(line_endings)})")
    print(f"  MISSING: {len(missing)}")
    print(f"  CHANGED: {len(changed)}")
    for rel in missing[:50]:
        print(f"    missing  {rel}")
    for rel in changed[:50]:
        print(f"    changed  {rel}")

    print()
    print("Files in key folders (on disk):")
    for folder, ext in KEY_FOLDERS:
        base = os.path.join(ROOT, *folder.split("/"))
        count = 0
        for dirpath, _, files in os.walk(base):
            count += sum(1 for name in files if name.endswith(ext))
        label = f"{folder} (*{ext})" if ext else folder
        print(f"  {label:<62} {count}")

    print()
    if missing or changed:
        print("RESULT: INCOMPLETE - get a fresh copy (git clone again, or the ZIP) and check again.")
        return 1
    print("RESULT: COMPLETE - every packaged file is present and unchanged.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
