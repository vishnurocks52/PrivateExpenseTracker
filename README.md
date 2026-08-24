# PrivateExpenseTracker

Privacy-first Android expense tracker starter.

Financial data is intended to remain on-device. No financial-data API or cloud database is included.

## Planned next stages

1. Robust bank SMS parser.
2. CSV/PDF bank statement import.
3. Deduplication and cross-source reconciliation.
4. Low-confidence/manual categorization queue.
5. Weekly/monthly/yearly analytics.
6. Secure local storage/export.
7. Gmail import designed around Android/Google authorization while keeping imported financial data local.

## Build

GitHub Actions builds a debug APK automatically on pushes to `main` and via manual workflow dispatch.

Build output: `PrivateExpenseTracker-debug/app-debug.apk`.
