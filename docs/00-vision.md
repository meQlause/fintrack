# 00 — Product Vision

## One-line summary

A **fully offline, private, beautiful** personal finance tracker for Android that makes recording a
transaction take **under 5 seconds**, and can **detect transactions automatically from bank and
e-wallet notifications**.

## Problem

People in Indonesia use many money sources at once: cash, several bank accounts, and several
e-wallets (GoPay, OVO, DANA, ShopeePay). They lose track of where money goes because:

1. Recording every transaction manually is tedious, so people stop after 1–2 weeks.
2. Existing apps require accounts, cloud sync or ads, and people do not trust them with financial data.
3. Many apps look cluttered and are not pleasant to open daily.

## Solution pillars

| Pillar | Meaning | How we measure it |
|--------|---------|-------------------|
| **Fast** | Manual entry in under 5 seconds; detected transactions confirmed in one tap | Taps from app open to saved transaction ≤ 4 |
| **Automatic** | Read bank/e-wallet notifications on-device and suggest transactions | % of real transactions captured from notifications |
| **Private** | No internet permission, no account, encrypted local database | Merged manifest has no `INTERNET` permission |
| **Beautiful** | Modern, calm, card-based UI with excellent dark mode | Owner and friends enjoy opening it |
| **Reliable** | Modular, strictly linted, well-tested code | `./gradlew check` always green |

## Target users

- **Primary:** the owner (Android user in Indonesia, uses multiple banks and e-wallets).
- **Secondary:** a small group of the owner's friends (5–20 people), all on Android.
- Distribution: signed APK shared directly (not Play Store, at least initially).

### Persona

> **Rina, 26, office worker in Jakarta.** Salary goes to BCA. Pays daily with GoPay and ShopeePay,
> sometimes cash. Wants to know "where did my money go this month?" and to stop overspending on
> food delivery. Will not type every coffee purchase manually. Does not want her finances on someone's server.

## Goals (version 1)

1. Record income, expenses and transfers across multiple wallets.
2. Show a clear monthly overview: total balance, income vs expense, spending per category.
3. Detect transactions from notifications of supported apps and queue them for one-tap confirmation.
4. Monthly budgets per category with warnings.
5. Protect data with PIN/biometrics and an encrypted database.
6. Encrypted backup and restore to a user-chosen file; CSV export.

## Non-goals (version 1)

These are explicitly **out of scope**. Do not implement them unless the roadmap changes.

- iOS, web or desktop versions.
- Any network feature: cloud sync, login, analytics, crash reporting to a server, ads, remote config.
- Multi-currency (IDR only).
- Shared wallets between users.
- ~~Investments, stocks, crypto tracking.~~ Superseded by ADR-017: **simple** investment tracking (money put in vs manually entered value) is in scope; live prices and per-holding tracking remain out of scope.
- Reading SMS (notifications only).
- Bank API integration / open banking.
- AI/LLM features (may come later, must also be on-device).

## Version 2 candidates (not now)

- Recurring transactions (subscriptions, salary, installments).
- Savings goals with progress.
- Debts and receivables ("A owes me 200k").
- Home-screen widget for quick entry.
- Smart text input ("kopi 25rb" → Expense, Food & Drink, Rp25.000).
- Receipt scanning with on-device OCR.

## Success criteria for version 1

- The owner uses it daily for 30 consecutive days.
- At least 3 friends install it and keep using it after 2 weeks.
- At least 70% of the owner's real transactions are captured via notification detection.
- Zero data-loss incidents (backup/restore verified).
