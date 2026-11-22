# 19 — Investments (simple mode)

Normative IDs: `FR-170 … FR-177` (01), `BR-050 … BR-055` (02), screen `S-20` (09), decision `ADR-017` (16).

The app is offline (ADR-002), so it never fetches prices. The user enters the **current value** manually.

## Model

An investment account is a wallet of type `INVESTMENT` with an `investKind`:
Reksa dana · Saham · Emas · Deposito · Kripto · Lainnya (e.g. "Bibit", "Stockbit", "Emas Pegadaian").

| Event | Recorded as | Statistics |
|-------|-------------|-----------|
| Put money in (top up / buy) | TRANSFER BCA → Bibit | Not spending |
| Take money out (sell / withdraw) | TRANSFER Bibit → BCA | Not income |
| Check the app, value is now Rp2.080.000 | ValuationEntry(value, date) | Nothing |

## Numbers (BR-051 … BR-053)

```
modal (invested)  = initialBalance + Σ transfers in − Σ transfers out
current value     = last valuation value
                    + Σ transfers in after that valuation − Σ transfers out after that valuation
                    (no valuation yet → current value = modal)
gain              = current value − modal           gain % = gain / modal
```

Example: top up 1.000.000 (2 Oct) and 1.000.000 (2 Nov) → modal 2.000.000.
Update value 20 Nov: 2.080.000 → gain +80.000 (+4,0%). Withdraw 500.000 on 21 Nov → modal 1.500.000, value 1.580.000, gain still +80.000.

## Rules

- Gains are shown **only** on the investment screens; never as income in statistics or budgets (BR-052).
- Investment wallets are excluded from **Saldo** and included in **Kekayaan bersih** at current value (BR-053, extends BR-043).
- Investment wallets cannot be used for EXPENSE, INCOME or loans — only transfers (BR-050).
- Optional reminder (default off): monthly on the 1st at 09:00, "Update nilai investasi" (BR-055).

## Out of scope (simple mode)

Units × price per holding, live prices, dividends, fees, tax, XIRR/time-weighted returns, automatic deposito interest.
These can come later as a "detailed" mode (per holding).
