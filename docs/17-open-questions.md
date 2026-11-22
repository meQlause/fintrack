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
