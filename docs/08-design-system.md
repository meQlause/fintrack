# 08 — Design System

Lives in `core:designsystem`. Feature modules **only** use these tokens and components — no raw colors,
dp values or font sizes outside this module (Konsist-enforced).

## Design principles

1. **Numbers are the hero.** Amounts are large, bold, tabular. Everything else is calm and secondary.
2. **Calm, not noisy.** Lots of whitespace, soft surfaces, one accent color. Color carries meaning (income/expense/category), not decoration.
3. **Fast paths are obvious.** The add button is always reachable; the most likely choice is preselected.
4. **Dark mode is first-class.** Designed and screenshot-tested in both themes.
5. **Delight in motion.** Short, purposeful animations and haptic feedback on save/confirm.
6. **Choices live in drawers, not chip piles.** Any selection from a list (wallet, wallet type, investment kind, tenor, due date,
   shortcut, day of month…) is shown in a form as **one compact row** (`Label ……… Value ›`) that opens the shared picker drawer (S-18):
   one option per line with icon, name, a short hint and a radio. Exceptions: 2–4 mutually exclusive modes use a **segmented control**
   (e.g. Keluar/Masuk/Transfer/Pinjaman, Daftar/Statistik); category selection keeps its icon carousel.
7. **Simple surface, details on tap.** Home shows one headline number per block. Anything with more than ~3 figures is reached by
   tapping a clearly tappable element (pill or card with a `›` chevron and a press state) that opens an info drawer with the full
   breakdown and one primary action.

## Logo

`design/assets/fintrack-logo.svg`: a 32×32 rounded square (radius 10) in `primary`, with an "F" drawn as two strokes and a coin dot
in `onPrimary` (stroke 2.6, round caps). Used at 32 dp in the Beranda header and as the base for the launcher icon (adaptive icon foreground).

## Color tokens

App name: **Fintrack**. Brand accent: **Teal** (Q-005; may be refined after P0 mockups). Dynamic color (Material You) is **off** by default to keep the brand look;
optional toggle in Settings could come later.

| Token | Light | Dark | Use |
|-------|-------|------|-----|
| `background` | `#F6F6F3` | `#0E1013` | Screen background |
| `surface` | `#FFFFFF` | `#16191E` | Cards, sheets |
| `surfaceVariant` | `#F1F1EE` | `#1E2228` | Inputs, keypad keys |
| `onSurface` | `#18181B` | `#ECEDEF` | Primary text |
| `onSurfaceVariant` | `#71717A` | `#9BA1AA` | Secondary text (muted) |
| `faint` | `#A1A1AA` | `#6B7280` | Faint text (tertiary) |
| `outline` | `#E8E8E4` | `#262A31` | Dividers, card hairline border |
| `primary` | `#0B7F74` | `#34D1BF` | Accent, FAB, selected states |
| `onPrimary` | `#FFFFFF` | `#062A26` | Content on primary |
| `primaryContainer` | `#E3F4F1` | `#11332F` | Tinted chips, selected cards |
| `onPrimaryContainer` | `#0B5F57` | `#B9F3EA` | Content on primary container |
| `income` | `#15803D` | `#4ADE80` | Income amounts |
| `expense` | `#D6325A` | `#FB7A95` | Expense amounts |
| `transfer` | `#2563EB` | `#7AB3FF` | Transfer amounts |
| `warning` | `#B45309` | `#FBBF24` | Budget 80–99% |
| `error` | `#C2410C` | `#FB923C` | Budget exceeded, errors |

Values are the final prototype palette (`design/project/Main.dc.html`, `.app.light` / `.app.dark`).

Custom tokens (`income`, `expense`, `transfer`, `warning`) are provided through a `FintrackColors`
`CompositionLocal`, accessed as `FintrackTheme.colors.income`.

### Category colour catalogue (`colorKey`)

Copied from `this.COLORS` in the prototype (`design/project/Main.dc.html`): light = first value, dark = second. Used for category icons (icon on a 16%-alpha circle of the color).

| key | Light | Dark |
|-----|-------|------|
| orange | `#E8833A` | `#F5A66B` |
| blue | `#3B7BEA` | `#7FAAF5` |
| pink | `#DD5C9B` | `#F08DBE` |
| purple | `#9466DB` | `#B99BEC` |
| violet | `#6E66DE` | `#A19CF0` |
| red | `#DE5656` | `#F08A8A` |
| cyan | `#1FA2BF` | `#62C9DE` |
| rose | `#E0567A` | `#F28AA5` |
| green | `#2E9F62` | `#6CCB95` |
| teal | `#14A394` | `#5ED3C5` |
| gray | `#9CA3AF` | `#8B929C` |
| indigo | `#5A6BDB` | `#919DEB` |
| amber | `#D99A2B` | `#F0BE63` |
| slate | `#475569` | `#A3B1C2` |

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

Flat design: cards use `surface` color on `background` with a **1 dp hairline border in `outline`** and **no shadow**,
in both light and dark mode. Bottom sheet and FAB: 3 dp.

## Motion

| Token | Duration | Easing | Use |
|-------|----------|--------|-----|
| `quick` | 120 ms | standard | press states, toggles |
| `medium` | 250 ms | emphasized decelerate | sheet open, list item enter |
| `slow` | 400 ms | emphasized | chart draw, number count-up |

- Balance changes animate with a count-up (`slow`).
- Saving a transaction: haptic `HapticFeedbackType.Confirm` (or `LongPress` fallback) + sheet dismiss + new row animates in.
- The bottom bar slides down and fades out when it hides and rises back when it returns (`medium`).
- Undo bars grow in and fade (`medium`), run a countdown line over `UndoWindow.MILLIS` (3 s) and shrink away; a deleted row fades and shrinks (`medium`).
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
| `FintrackBottomSheet` | Modal sheet with one drag handle, `large` top radius; a sheet opened over another hides it (`PickerDrawer` caps at 75% of the screen, `FintrackSizes.drawerMaxFraction`) |
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
