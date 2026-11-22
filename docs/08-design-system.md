# 08 — Design System

Lives in `core:designsystem`. Feature modules **only** use these tokens and components — no raw colors,
dp values or font sizes outside this module (Konsist-enforced).

## Design principles

1. **Numbers are the hero.** Amounts are large, bold, tabular. Everything else is calm and secondary.
2. **Calm, not noisy.** Lots of whitespace, soft surfaces, one accent color. Color carries meaning (income/expense/category), not decoration.
3. **Fast paths are obvious.** The add button is always reachable; the most likely choice is preselected.
4. **Dark mode is first-class.** Designed and screenshot-tested in both themes.
5. **Delight in motion.** Short, purposeful animations and haptic feedback on save/confirm.

## Color tokens

App name: **Fintrack**. Brand accent: **Teal** (Q-005; may be refined after P0 mockups). Dynamic color (Material You) is **off** by default to keep the brand look;
optional toggle in Settings could come later.

| Token | Light | Dark | Use |
|-------|-------|------|-----|
| `primary` | `#0D9488` | `#2DD4BF` | Accent, FAB, selected states |
| `onPrimary` | `#FFFFFF` | `#042F2E` | Content on primary |
| `primaryContainer` | `#CCFBF1` | `#134E4A` | Tinted chips, selected cards |
| `onPrimaryContainer` | `#042F2E` | `#CCFBF1` | |
| `background` | `#F6F7F9` | `#0B0F14` | Screen background |
| `surface` | `#FFFFFF` | `#141A21` | Cards, sheets |
| `surfaceVariant` | `#EEF1F4` | `#1C232C` | Inputs, keypad keys |
| `onSurface` | `#0F172A` | `#E6EAF0` | Primary text |
| `onSurfaceVariant` | `#64748B` | `#94A3B8` | Secondary text |
| `outline` | `#E2E8F0` | `#2A333E` | Dividers, borders |
| `income` | `#16A34A` | `#4ADE80` | Income amounts |
| `expense` | `#E11D48` | `#FB7185` | Expense amounts |
| `transfer` | `#2563EB` | `#60A5FA` | Transfer amounts |
| `warning` | `#D97706` | `#FBBF24` | Budget 80–99% |
| `error` | `#DC2626` | `#F87171` | Budget exceeded, errors |

Custom tokens (`income`, `expense`, `transfer`, `warning`) are provided through a `FintrackColors`
`CompositionLocal`, accessed as `FintrackTheme.colors.income`.

### Category / wallet color catalog (`colorKey`)

Each key has a light and dark variant (dark = lighter tint). Used for category icons (icon on a 16%-alpha circle of the color).

| key | Light | Dark |
|-----|-------|------|
| orange | `#EA580C` | `#FB923C` |
| blue | `#2563EB` | `#60A5FA` |
| pink | `#DB2777` | `#F472B6` |
| purple | `#9333EA` | `#C084FC` |
| violet | `#7C3AED` | `#A78BFA` |
| red | `#DC2626` | `#F87171` |
| indigo | `#4F46E5` | `#818CF8` |
| brown | `#92400E` | `#D6A36A` |
| cyan | `#0891B2` | `#22D3EE` |
| rose | `#E11D48` | `#FB7185` |
| green | `#16A34A` | `#4ADE80` |
| teal | `#0D9488` | `#2DD4BF` |
| amber | `#D97706` | `#FBBF24` |
| gray | `#64748B` | `#94A3B8` |

## Typography

Font: **Plus Jakarta Sans** (bundled, weights 400/500/600/700/800). Amounts use `fontFeatureSettings = "tnum"` (tabular numbers).

| Token | Size / line height | Weight | Use |
|-------|-------------------|--------|-----|
| `amountHero` | 40 / 48 | 800 | Total balance, keypad display |
| `amountLarge` | 28 / 36 | 700 | Card totals |
| `amountMedium` | 18 / 24 | 700 | List amounts |
| `headlineSmall` | 24 / 32 | 700 | Screen titles |
| `titleMedium` | 16 / 24 | 600 | Card titles, list primary text |
| `bodyLarge` | 16 / 24 | 400 | Body |
| `bodyMedium` | 14 / 20 | 400 | Secondary text |
| `labelLarge` | 14 / 20 | 600 | Buttons |
| `labelMedium` | 12 / 16 | 500 | Chips, captions |

## Spacing (4 dp grid)

`FintrackTheme.spacing`: `xxs 2`, `xs 4`, `sm 8`, `md 12`, `lg 16`, `xl 24`, `xxl 32`, `xxxl 48`.
Screen horizontal padding: `lg` (16). Card internal padding: `lg`. Gap between cards: `md`.

## Shapes

| Token | Radius | Use |
|-------|--------|-----|
| `small` | 8 | chips, small buttons |
| `medium` | 16 | cards, inputs |
| `large` | 24 | bottom sheets (top corners), hero card |
| `full` | 50% | FAB, avatar/icon circles, keypad keys |

## Elevation

Flat design: cards use `surface` color on `background` with **no shadow** in dark mode and a very soft shadow
(1 dp) in light mode. Bottom sheet and FAB: 3 dp.

## Motion

| Token | Duration | Easing | Use |
|-------|----------|--------|-----|
| `quick` | 120 ms | standard | press states, toggles |
| `medium` | 250 ms | emphasized decelerate | sheet open, list item enter |
| `slow` | 400 ms | emphasized | chart draw, number count-up |

- Balance changes animate with a count-up (`slow`).
- Saving a transaction: haptic `HapticFeedbackType.Confirm` (or `LongPress` fallback) + sheet dismiss + new row animates in.
- Swipe on inbox items reveals green (confirm) / gray (dismiss) background with icon.
- Respect system "remove animations" setting (durations → 0).

## Icons

Material Symbols Rounded, bundled as vector drawables (offline). Icon catalog (`iconKey` → drawable) in
`core:designsystem/icon/FintrackIcons.kt`. Category icons are shown 24 dp inside a 40 dp circle.

## Core components (core:designsystem)

| Component | Description |
|-----------|-------------|
| `FintrackTheme` | Wraps MaterialTheme + custom tokens (colors, spacing, motion, amount typography) |
| `FintrackCard` | Rounded `medium` surface card with optional onClick |
| `FintrackButton` / `FintrackTextButton` / `FintrackOutlinedButton` | Buttons, 48 dp min height |
| `FintrackTopBar` | Title + optional actions |
| `FintrackBottomBar` | 4 destinations + center FAB notch |
| `FintrackFab` | Circular primary add button |
| `FintrackChip` | Filter / selection chip |
| `FintrackTextField` | Filled rounded field with error text slot |
| `FintrackBottomSheet` | Modal sheet with drag handle, `large` top radius |
| `FintrackDialog` | Confirmation dialog |
| `IconCircle` | Icon on tinted circle (category/wallet) |
| `ProgressBar` | Rounded budget bar with NORMAL/WARNING/EXCEEDED colors |
| `EmptyState` | Illustration icon + title + body + optional action |
| `NumericKeypad` | 4×4 keypad: 1–9, `000`, 0, backspace; large `full`-shaped keys |

## Shared UI components (core:ui)

| Component | Description |
|-----------|-------------|
| `MoneyText` | Formats `Money` as `Rp25.000`, color by `TransactionType`, sign prefix (`−`/`+`), hide mode `Rp•••••` |
| `MoneyFormatter` | `Locale("id","ID")` formatting, no decimals |
| `TransactionRow` | Icon circle, title (category or merchant), subtitle (wallet · time), amount |
| `WalletCard` | Wallet name, type icon, balance |
| `CategoryDonut` | Vico donut + legend |
| `DateFormatter` | "Hari ini", "Kemarin", "Sen, 18 Nov" |

## Formatting rules

| What | Format | Example |
|------|--------|---------|
| Amount | `Rp` + thousands with `.` | `Rp1.250.000` |
| Negative / expense in lists | prefix `−` (U+2212) | `−Rp25.000` |
| Income in lists | prefix `+` | `+Rp5.000.000` |
| Compact (charts axis) | `rb` / `jt` / `M` | `25rb`, `1,2jt` |
| Date header | relative or `EEE, d MMM` (id) | `Hari ini`, `Sen, 18 Nov` |
| Time | `HH:mm` (24h) | `14:05` |
| Percentage | integer + `%` | `82%` |
