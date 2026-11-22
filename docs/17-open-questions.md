# 17 — Open Questions

Agents: if your task depends on an **open** question, stop and ask the owner. When answered, move it to
"Answered" with the answer and date, and update the affected docs.

## Open

| ID | Question | Affects | Proposed default |
|----|----------|---------|------------------|
| Q-004 | Verify package names of supported apps on a real device | 07 | — |

## Answered

| ID | Question | Answer | Date |
|----|----------|--------|------|
| — | Platform? | Android only (ADR-001) | 2026-11-22 |
| — | Online or offline? | Fully offline (ADR-002) | 2026-11-22 |
| — | Notification detection? | Yes, with Inbox confirmation (ADR-003) | 2026-11-22 |
| — | Git host / identity? | GitHub `meQlause`, `ardialbrian@gmail.com`, no AI co-author (ADR-012) | 2026-11-22 |
| Q-001 | App name? | **Fintrack** | 2026-11-22 |
| Q-002 | applicationId / root package? | **`com.meqlause.fintrack`** (final, never change) | 2026-11-22 |
| Q-003 | First banks / e-wallets to support? | **BCA (myBCA + BCA mobile), GoPay, DANA**; others follow in P5 | 2026-11-22 |
| Q-005 | Accent color and style? | **Teal, calm minimal, dark-first** (08); may be refined after P0 mockups | 2026-11-22 |
| Q-006 | Repository visibility / APK sharing? | **Public** GitHub repo; signed APK attached to GitHub Releases (ADR-013) | 2026-11-22 |
| Q-007 | Gradle dependency verification when? | After P1 is stable (add as first task of P2) | 2026-11-22 |
| Q-008 | PIN length? | **6 digits** | 2026-11-22 |
| Q-009 | "Copy sample" button for unsupported notifications? | **Yes**, copies anonymized text to clipboard only (no network) | 2026-11-22 |
| Q-010 | When does an instalment purchase count as spending? | **Full amount at purchase** (ADR-015); "Cicilan bulan ini" shown separately | 2026-11-22 |
| Q-011 | Support pay-later (SPayLater, GoPayLater, Kredivo)? | **Yes**, wallet type PAYLATER, same model as credit cards | 2026-11-22 |
| Q-012 | Track minimum payment? | **No**; assume full payment, partial payments allowed | 2026-11-22 |
| Q-013 | Bill/loan reminder timing? | **D−3 and D−1 at 09:00** (BR-045) | 2026-11-22 |
| Q-014 | Interest / fees? | **No interest**; optional one-off admin fee on instalments (BR-038) | 2026-11-22 |
| Q-015 | Investment detail level? | **Simple**: per account, money put in vs manually entered value (ADR-017) | 2026-11-22 |
| Q-016 | Count investment gains as income? | **No**, investment screens only (BR-052) | 2026-11-22 |
| Q-017 | Investment reminder? | **Optional monthly "update nilai" reminder**, default off (BR-055) | 2026-11-22 |
