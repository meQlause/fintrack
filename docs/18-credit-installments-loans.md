# 18 — Credit Cards, Pay-later, Instalments & Loans

Feature overview with worked examples. The **normative** definitions live in:
requirements `FR-130 … FR-165` (01), entities and rules `BR-030 … BR-049` (02), screens `S-14 … S-17` (09),
decisions `ADR-015`, `ADR-016` (16). If this document and those disagree, those win.

No interest is ever calculated. The only optional cost is a one-off **admin fee** on an instalment plan.

---

## 1. The golden rule (ADR-015)

> **Spending is recognised once — at the moment of purchase. Paying a bill or an instalment is a transfer
> between your own wallets, never a second expense.**

| Event | Recorded as | Counts in statistics? |
|-------|-------------|-----------------------|
| Buy shoes Rp500.000 with BCA Card | EXPENSE from wallet "BCA Card" | Yes, category Belanja |
| Buy phone Rp3.000.000 on 6× instalment | EXPENSE Rp3.000.000 from "BCA Card" + InstallmentPlan | Yes, Rp3.000.000 in the purchase month |
| Admin fee Rp50.000 of that plan | EXPENSE from "BCA Card", category "Biaya & Admin" | Yes |
| Pay card bill Rp1.000.000 from BCA | TRANSFER BCA → BCA Card | No |
| Lend Andi Rp200.000 from Tunai | LEND from Tunai | No |
| Andi pays back Rp100.000 into GoPay | COLLECT into GoPay | No |
| Borrow Rp500.000 from Sari into BCA | BORROW into BCA | No |
| Repay Sari Rp500.000 from BCA | REPAY from BCA | No |

## 2. Credit wallets (credit card & pay-later)

A wallet of type `CREDIT_CARD` or `PAYLATER` with three extra fields: `creditLimit`, `statementDay`, `dueDay`.

- **Balance** follows the normal formula (BR-004 family). For credit wallets it is usually **negative** = debt.
- **Outstanding** = `max(0, −balance)`. **Available limit** = `creditLimit − outstanding` (BR-044).
- A positive balance (overpayment) is allowed and shown as "Kelebihan bayar".

### Statement cycle (BR-032)

Example: `statementDay = 25`, `dueDay = 10`.

```
Statement "Nov 2026": cut-off 25 Nov 2026, covers 26 Oct – 25 Nov, due 10 Dec 2026
Statement "Dec 2026": cut-off 25 Dec 2026, covers 26 Nov – 25 Dec, due 10 Jan 2027
```

Due date = `dueDay` of the cut-off month if `dueDay > statementDay`, else `dueDay` of the following month.

### Statement amount (BR-033)

```
statementAmount(S) = outstanding at cut-off(S)
                   − unbilled instalment principal at cut-off(S)      // future portions are not due yet
paidSinceCutoff(S) = Σ transfers INTO the card after cut-off(S) up to now
remainingDue(S)    = max(0, statementAmount(S) − paidSinceCutoff(S))
```

Unpaid amounts simply roll into the next statement's outstanding (no interest, no penalty).

Status (BR-034): `NOT_ISSUED` (before first cut-off), `PAID` (remainingDue = 0), `DUE` (remainingDue > 0 and today ≤ due date),
`OVERDUE` (remainingDue > 0 and today > due date).

### Worked example

```
Limit Rp10.000.000, statement day 25, due day 10.
20 Nov  Shoes            −500.000      (EXPENSE)
22 Nov  Phone 6× plan  −3.000.000      (EXPENSE, plan: 500.000 × 6, first portion on statement Nov 2026)
25 Nov  cut-off → outstanding 3.500.000, unbilled plan principal 2.500.000
        statementAmount = 3.500.000 − 2.500.000 = 1.000.000   (shoes + 1st phone portion)
        available limit = 10.000.000 − 3.500.000 = 6.500.000
 1 Dec  Pay bill 1.000.000 (TRANSFER BCA → BCA Card) → remainingDue 0 → PAID
25 Dec  cut-off → outstanding 2.500.000, unbilled 2.000.000 → statementAmount 500.000
```

### Paying a bill (FR-132, FR-133)

"Bayar tagihan" opens a prefilled TRANSFER: source = last used bank/e-wallet, target = the card,
amount = `remainingDue`. The user may change the amount (partial payment). Paying more than remainingDue is allowed
(it reduces outstanding further).

## 3. Instalments (cicilan 0%)

Created from the add sheet when the chosen wallet is a credit wallet: toggle **"Cicilan"** → tenor chips
`3 · 6 · 12 · Lainnya (2–36)` → optional admin fee.

- Portion amount (BR-035): `base = floor(principal / tenor)`; portions 1…tenor−1 = base; last portion = `principal − base × (tenor − 1)`.
  Example Rp1.000.000 / 3 → 333.333, 333.333, 333.334.
- Schedule (BR-036): portion *k* is billed on statement `firstStatement + (k − 1)` months.
  `firstStatement` defaults to the **first statement whose cut-off is on or after the purchase date** (editable).
- Early payoff (BR-037): all remaining portions are billed on the next statement; plan status → `PAID_OFF`.
- Deleting the purchase deletes the plan (confirm dialog). Editing the amount or tenor recomputes portions.
- Plan progress shows "2/6 · sisa Rp2.000.000"; portions billed on statements whose due date has passed and were paid count as paid.

"Cicilan bulan ini" (FR-146) = Σ portions billed on statements due in the current period, across all plans.

## 4. Loans — hutang & piutang (ADR-016)

| Direction | Created by | Effect on wallet | Repaid by |
|-----------|-----------|------------------|-----------|
| `LENT` (piutang — they owe me) | LEND transaction | money out | COLLECT transactions (money in) |
| `BORROWED` (hutang — I owe them) | BORROW transaction | money in | REPAY transactions (money out) |

- `outstanding = principal − Σ repayments` (BR-041). When it reaches 0 → status `SETTLED`.
  Repayment above outstanding is rejected with "Melebihi sisa pinjaman".
- Person = free-text name (no contacts permission). Names are suggested from previous loans.
- Optional due date → reminder at 3 days and 1 day before (BR-045).
- **Write-off** ("Ikhlaskan", LENT only): closes the loan as `WRITTEN_OFF`; no money moves and statistics are not changed (BR-042).
- Loan transactions never appear in income/expense statistics (BR-040), but they do appear in History with their own icon.

## 5. Balances shown to the user (BR-043)

| Name (UI) | Definition |
|-----------|-----------|
| **Saldo** (Home hero) | Σ balances of non-credit wallets included in total |
| **Utang kartu** | Σ outstanding of credit wallets |
| **Piutang** | Σ outstanding of `LENT` loans (open) |
| **Hutang** | Σ outstanding of `BORROWED` loans (open) |
| **Kekayaan bersih** (Wallets tab) | Saldo + Piutang − Utang kartu − Hutang |

## 6. Notifications & detection

- **Our reminders** (channel `bill_reminder`): card statement due at D−3 and D−1 at 09:00 if remainingDue > 0;
  loan due dates likewise. Lock-screen text never shows amounts when a PIN is set.
- **Detection** (07): credit card and pay-later transaction notifications are parsed like others; the parser may return an
  `instrumentHint` (`CARD`, `PAYLATER`, `ACCOUNT`) and the last 4 card digits. MonitoredApp mapping becomes
  `(packageName, instrumentHint, last4?) → wallet`. A bank debit + card payment notification pair is suggested as a bill-payment TRANSFER.

## 7. Out of scope (v1)

Interest and late fees · minimum-payment tracking · multi-currency card spending · cash advance · reward points ·
automatic statement import · splitting a bill between friends (planned later as an extension of loans).
