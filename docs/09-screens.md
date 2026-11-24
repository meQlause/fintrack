# 09 — Screens

Every screen lists: purpose, entry points, layout (top → bottom), states, actions, and requirements covered.
UI text shown here is the **Indonesian string** to put in `strings.xml`.

## Navigation map

```
[Splash] ─► (first launch?) ─► Onboarding ─► Dashboard
        └─► (PIN set & locked?) ─► Lock ─► Dashboard

Bottom bar:  Beranda | Riwayat | (+) | Investasi | Dompet
             The bar slides down and fades out (`medium`) when it hides (sub-screens, onboarding, an open sheet) and rises back the same way.
             (Statistics live inside Riwayat as the "Statistik" view; the Investasi tab is S-08.)
Every tab header: 40px row, 32px icon tile + 20px/700 title (Beranda uses the Fintrack logo, others a soft-teal tile with the tab icon).

Dashboard ─► Settings (top-bar gear)
          ─► Inbox (top-bar bell with badge / banner)
          ─► Transaction detail (tap row)
          ─► Budget (tap warning card)
(+) FAB   ─► Add Transaction sheet (from any bottom-bar screen)
Settings  ─► Categories, Budgets, Monitored apps, Security, Backup & Export, Appearance, Period, About
```

---

## S-01 Onboarding (`feature:onboarding`) — FR-001..004

Horizontal pager, 4 pages, dot indicator, "Lewati" (skip) top-right, primary button bottom.

1. **Welcome** — "Catat keuangan tanpa ribet" / "Semua data tersimpan hanya di HP kamu. Tanpa akun, tanpa internet."
2. **First wallet** — form: name (default "Tunai"), type chips, initial balance (keypad). Button "Lanjut". Required step (cannot skip).
3. **Notification detection** — explanation + illustration. "Aktifkan" → prominent disclosure dialog (see
   "Notification access disclosure" below) → system settings only after "Setuju"; "Nanti saja".
4. **Security** — "Kunci dengan PIN?" → PIN setup flow; "Nanti saja". Button "Mulai".

States: per page; returning from system settings re-checks access and shows ✓.

## S-02 Lock (`feature:lock`) — FR-100..103

- App icon/name, "Masukkan PIN", 6 dots, `NumericKeypad` (no `000` key; biometric key instead if enabled).
- Biometric prompt auto-shows on open if enabled.
- Wrong PIN: dots shake + haptic. After 5 failures: "Coba lagi dalam 30 detik" with countdown.

## S-03 Dashboard / Beranda (`feature:dashboard`) — FR-040..045

Layout:
1. **Top bar:** greeting "Halo 👋" + period label "1 – 30 Nov"; actions: bell (Inbox, badge count), gear (Settings).
2. **Hero card:** "Total Saldo", `amountHero`, eye icon toggles hidden amounts. Below: small row of wallet chips (name + balance), horizontally scrollable.
3. **Inbox banner** (only if pending > 0): "3 transaksi terdeteksi menunggu konfirmasi" → Inbox.
4. **Period summary card:** two columns "Pemasukan" (income color) and "Pengeluaran" (expense color), net below: "Selisih +Rp1.200.000".
5. **Budget warnings** (only if any ≥ 80%): compact list with progress bars.
6. **Top categories card:** donut (top 5 + "Lainnya") + legend with amounts and %.
7. **Recent transactions:** 5 `TransactionRow`s + "Lihat semua" → Riwayat.

States:
- Loading: skeleton placeholders (shimmer) for cards.
- Empty (no transactions yet): hero card + `EmptyState` "Belum ada transaksi" / "Tekan + untuk mencatat transaksi pertamamu".
- Success: as above.

## S-04 Add / Edit Transaction sheet (`feature:transaction`) — FR-030..035

Modal bottom sheet, opens to ~90% height, keypad visible immediately.

1. **Type segmented control:** Pengeluaran | Pemasukan | Transfer (preselected: last used).
2. **Amount display:** `Rp0` placeholder → live formatted `Rp25.000`, `amountHero`, colored by type. Error text below if invalid.
3. **Category row** (EXPENSE/INCOME): horizontal scroll of `IconCircle` + name; most-used first; "Semua" opens full grid. For TRANSFER: "Dari" wallet and "Ke" wallet pickers + "Biaya admin" optional field.
4. **Details row:** chips — wallet (preselected last used), date ("Hari ini, 14:05" → date/time picker), note ("Catatan" → text field).
5. **Numeric keypad** + big **"Simpan"** button (enabled when amount > 0 and required fields set).

Edit mode: same sheet prefilled, title "Ubah Transaksi", extra "Hapus" action (with confirm dialog → undo snackbar).

Validation messages:
- `amount_required` "Masukkan jumlah"
- `amount_too_large` "Jumlah terlalu besar"
- `category_required` "Pilih kategori"
- `same_wallet` "Dompet asal dan tujuan harus berbeda"

## S-05 History / Riwayat (`feature:transaction`) — FR-036..038

1. **Top bar:** "Riwayat", search icon (expands to search field).
2. **Month selector:** "< November 2026 >" (respects period start day).
3. **Filter chips:** Semua | Pengeluaran | Pemasukan | Transfer, + "Filter" (wallet, category bottom sheet).
4. **Summary strip:** income / expense totals for the filter.
5. **List:** sticky day headers ("Hari ini", "Kemarin", "Sen, 18 Nov") with day net total; `TransactionRow`s.
   Rows from notifications show a small "Otomatis" badge.

States: Loading, Empty ("Tidak ada transaksi di periode ini"), Empty-search ("Tidak ditemukan"), Success.

## S-06 Transaction detail (`feature:transaction`)

Big amount, type, category with icon, wallet(s), date-time, note, merchant, source ("Dicatat manual" / "Terdeteksi dari BCA").
Actions: "Ubah" (opens S-04 edit), "Catat lagi" (FR-038), "Hapus".

## S-07 Inbox / Kotak Masuk (`feature:inbox`) — FR-084..091

1. **Top bar:** "Kotak Masuk", overflow: "Abaikan semua".
2. **Info card** (dismissible, first time): "Transaksi dari notifikasi bank & e-wallet muncul di sini. Geser kanan untuk catat, kiri untuk abaikan."
3. **List of cards**, newest first. Each card:
   - Source app label + time ("BCA · 14:05")
   - Amount (colored by direction) + merchant
   - Suggested category chip (tap to change) + suggested wallet chip (tap to change)
   - Buttons: "Abaikan" (text) / "Catat" (primary)
   - Swipe right = Catat, swipe left = Abaikan (with undo snackbar)
4. **Paired card** (FR-091): "Transfer BCA → GoPay · Rp100.000" with "Catat sebagai Transfer" and "Catat terpisah".
5. **Section "Selesai"** collapsed: recent confirmed/dismissed (last 7 days).

States: Loading, Empty ("Semua beres! ✨" / "Belum ada transaksi baru terdeteksi"), Access-off ("Deteksi notifikasi belum aktif" + "Aktifkan" button), Success.

> **Change (2026-11-22):** the content below is now the **"Statistik" view inside S-05 Riwayat** (segmented "Daftar | Statistik"
> under the month strip; it follows the selected month). The S-08 tab is now **Investasi**: total value, gain, allocation by kind,
> account list (→ S-20), recent investment activity, "+" to add an investment account, empty state "Belum ada investasi".

## S-08 Statistics / Statistik (`feature:statistics`) — FR-060..064

1. Month selector (same as S-05).
2. Toggle: Pengeluaran | Pemasukan.
3. **Donut chart** with total in the center.
4. **Ranked category list:** icon, name, amount, %, change vs previous period (▲ 12% red for expense increase, ▼ green for decrease).
5. **Daily bar chart** ("Pengeluaran harian").
6. **Trend card** (S): 6 months income vs expense lines.

Tap category → History filtered (FR-064). States: Loading, Empty, Success.

## S-09 Wallets / Dompet (`feature:wallet`) — FR-010..016

1. Total balance header.
2. Wallet cards grouped by type (Tunai, Bank, E-Wallet, Kartu Kredit, Lainnya), each with balance.
3. "+ Tambah Dompet" button.
4. Archived section collapsed at bottom ("Diarsipkan (2)").

Wallet detail: balance, this-period in/out, transaction list for this wallet, edit/archive/delete actions.
Wallet edit form: name, type chips, initial balance, icon picker grid, color picker row, "Hitung di total saldo" switch.

## S-10 Categories (`feature:category`) — FR-020..023

Tabs Pengeluaran | Pemasukan. Grid/list of categories with reorder handles. Edit form: name, icon grid, color row.
Archive/delete with BR-008 handling.

## S-11 Budgets (`feature:budget`) — FR-070..074

1. Overall budget card (if set): spent / limit, progress bar, "Sisa Rp…" / "Lebih Rp…".
2. Per-category budget list: icon, name, progress bar with state color, "Rp750.000 / Rp1.000.000".
3. "+ Atur Anggaran" → pick category → enter limit (keypad).

## S-12 Settings / Pengaturan (`feature:settings`) — FR-120..124

Grouped list:
- **Umum:** Tampilan (Sistem/Terang/Gelap), Awal periode (tanggal 1–28), Sembunyikan saldo di beranda.
- **Data:** Dompet, Kategori, Anggaran.
- **Deteksi otomatis:** status access (Aktif ✓ / Tidak aktif → Aktifkan), Aplikasi dipantau (S-13), Tampilkan notifikasi konfirmasi (switch), Bantuan optimasi baterai.
- **Keamanan:** PIN (Atur/Ubah/Hapus), Biometrik, Kunci otomatis setelah…, Sembunyikan dari aplikasi terbaru.
- **Cadangan & Ekspor:** Buat cadangan, Pulihkan, Pengingat cadangan (hari), Ekspor CSV. Shows "Cadangan terakhir: 3 hari lalu".
- **Tentang:** versi, lisensi open source, pernyataan privasi, baris "Kebijakan privasi" (opens
  `https://github.com/meQlause/fintrack/blob/main/docs/play-store/privacy-policy.md` in the browser through an
  `ACTION_VIEW` intent; the app itself has no INTERNET permission, NFR-001; when no browser exists a toast
  says "Tidak ada aplikasi browser" and nothing else happens).

## Notification access disclosure (shared, `core:ui`) — FR-003, FR-081, FR-084, Google Play User Data policy

One dialog, shown **before every place that opens the system notification-listener settings**: onboarding page 3
("Aktifkan"), S-13 ("Aktifkan" access button) and S-07 (inbox "Aktifkan akses" button). Title "Izinkan Fintrack
membaca notifikasi?". It states what is read (title and text of notifications from the banking and e-wallet apps
the user selects: amount, sender or receiver, time), what is ignored (all other apps), that nothing leaves the
device (no internet permission) and that new transactions are recorded only after the user confirms, that raw
notification text is kept at most 30 days (FR-092) and that access can be turned off any time in Android settings.
Buttons "Setuju" (opens the system settings) and "Tidak sekarang" (closes the dialog, nothing opens; the user can
try again later). Back or tapping outside counts as "Tidak sekarang".

## S-13 Monitored apps (`feature:settings`) — FR-081, FR-082, FR-090

List of supported apps that are **installed** (query by package via `<queries>` manifest entries). Each row:
app name, switch (enabled), "Dompet default" picker, "Terakhir diterima: 2 jam lalu".
Section "Belum didukung?" explaining how to send sample notifications to the developer. Button "Salin contoh"
copies the latest unrecognized notification text from that app to the clipboard, with digits of account/reference
numbers masked as `XXXX` (Q-009). Offline only; the user shares it manually.

## Credit, instalments & loans additions

Flows and examples: `18-credit-installments-loans.md`.

### Home simplification (2026-11-22, supersedes the hero line and donut card below)
- Hero: "Saldo" + balance, then a single pill **"+1,5jt bulan ini ›"** (net of the period). Tap → **Ringkasan** drawer:
  Kekayaan bersih on top; rows Pemasukan, Pengeluaran, Saldo, Investasi, Piutang, Utang kartu, Hutang; action "Lihat dompet".
- Category card: tappable card "Pengeluaran Nov" + total + `›`, one slim stacked bar, top 3 categories with %.
  Tap → **Kategori** drawer: every category (icon, %, number of transactions, amount); action "Buka statistik".

### Changes to existing screens
- **S-03 Beranda:** each Tagihan row has a `large` (16 dp) vertical inset and a 4 dp gap between the amount and its status chip (T9.4d).
- **Investasi baru sheet (S-08, FR-170):** laid out like the new wallet sheet: "Nama" label + field, one "Jenis ... Pilih jenis ›" value row
  (opens the shared options drawer), "Modal awal" label + amount card row, the keypad and "Simpan". The sheet is a PickerDrawer, so the
  body never holds its own scroll.
- **S-03 Beranda:** hero label "Saldo" (BR-043). If non-zero, one line under it: "Utang kartu Rp3,5jt · Piutang Rp300rb · Hutang Rp500rb".
  New **"Tagihan"** card (FR-137) after the inbox row: nearest bill (card name, amount, "jatuh tempo 3 hari", status colour),
  "Cicilan bulan ini Rp1,5jt", and receivable/debt totals; tapping opens the relevant screen. Overdue → error colour (FR-136).
- **S-04 Add sheet:** type tabs become **Keluar · Masuk · Transfer · Pinjaman**.
  - **Keluar** shows one **"Bayar pakai · <group>"** row (icon, wallet name, balance or remaining limit, ⌄). Tapping it opens the
    **wallet picker drawer** (S-18). **Masuk** uses the same row labelled "Masuk ke" (non-credit wallets only).
  - **Transfer** is laid out left to right: **[Dari] ⇄ [Ke]** cards with a swap button; tapping a side opens the drawer for that side;
    the source can never be a credit wallet (BR-039) and the wallet chosen on the other side is shown as "Sudah dipilih".
  - **Pinjaman** has a "Jatuh tempo" row (FR-158) and a wallet row, both opening drawers.

### Shortcuts (FR-039)
- **S-03 Beranda:** "Catat cepat" row under the hero — horizontal tiles (category icon, shortcut name, amount) + a dashed "Baru" tile that opens the dedicated **"Pintasan baru" form** (a sheet that only saves a shortcut, never a transaction): Keluar/Masuk switch, Nama (1–24), Jumlah, Kategori (kind of the type, BR-006), Dompet (Masuk: no credit wallet), Catatan (optional, 0–120). "Atur" opens the manager drawer: one card of rows (category icon, name, wallet, amount, trash), tapping a row edits it in the same form, a dashed "Pintasan baru" button under the card, delete shows the undo bar.
- **S-04 Add sheet:** a "⚡ Pintasan" button in the sheet header opens the **shortcut drawer**: a 3-column grid of tiles (icon, title, amount, wallet);
  picking one fills the sheet and closes the drawer (still editable). "Atur pintasan" at the bottom switches the drawer to the manage/delete list.
  A "Simpan sebagai pintasan" checkbox above "Simpan" (hidden when editing or when a shortcut was applied); ticking it shows a Nama field, and the transaction note becomes the shortcut note. Applying a shortcut fills the sheet note from the shortcut note, not its name. The manage view here only deletes.

### S-20 Investment detail (`feature:investment`) — FR-173
Push screen: header (name + kind badge); "Nilai sekarang" large; gain line "+Rp80.000 (+4,0%)" (income/expense colour);
"Terakhir update 20 Nov"; 2 stats (Modal, Untung/Rugi); actions **Update nilai** (keypad sheet, prefilled with current value) ·
**Tambah dana** (TRANSFER into it, prefilled) · **Tarik** (TRANSFER out to a bank); timeline of transfers and valuations.
Dompet tab gets an **"Investasi"** group (value + gain %); the net-worth breakdown becomes a list: Saldo · Investasi · Piutang · Utang kartu · Hutang.

### S-19 Wallet detail (`feature:wallet`) — FR-017
Push screen: header (name + type badge); icon + balance; 3-column stats (Masuk / Keluar / Transaksi this period);
two quick actions **Pakai** (EXPENSE with this wallet pre-selected) · **Transfer** (from this wallet); "Keluar per hari" mini bars
for the period (today highlighted, future dimmed); "Terbaru" list (8 rows) with **"Lihat semua (N)"** that expands the full list
in place (toggle "Ringkas") — it never navigates away to the Riwayat tab.

### S-18 Wallet picker drawer (shared component in `core:ui`)
A second bottom sheet opened over the current sheet. Only the newest sheet is drawn (one drag handle, one scrim); the sheet
under it stays composed but hidden, so its input survives, and it reappears when the drawer closes. Every sheet and drawer
shares one panel (full width, screen gutter on both sides, large top radius) and a drawer is as tall as its content, at most
75% of the screen height, scrolling only beyond that. Sections in this order, each with an icon header:
**Tunai** · **Debit (bank & e-wallet)** · **Kartu kredit** · **Paylater** (credit sections hidden where not allowed).
Rows: icon, name, balance (or "Sisa limit" for credit), radio indicator. Picking closes the drawer.
The same drawer has two other modes: a **1–28 day grid** (statement/due day, FR-138) and an **options list** (loan due date, FR-158).
  - When the selected wallet is a credit wallet (Keluar): a **"Cicilan"** switch appears; on → tenor chips `3 · 6 · 12 · Lainnya`
    and an optional "Biaya admin" field; a preview line "Rp500.000 × 6 bulan · mulai tagihan Nov" (FR-140).
  - **Pinjaman** tab: segmented "Saya meminjamkan" / "Saya meminjam", person name field with suggestions (FR-157),
    wallet chips, optional due date chip (FR-150, FR-151).
- **S-05 Riwayat:** loan transactions show a person icon and label ("Pinjamkan ke Andi", "Andi bayar"); card payments show "Bayar BCA Card".
- **S-09 Dompet:** header shows **Kekayaan bersih** with a 4-part breakdown (FR-161). New group **"Kredit"** (credit wallets:
  outstanding, thin available-limit bar, "jatuh tempo 10 Des") and a **"Pinjaman"** row → S-17.

### S-14 Credit wallet detail (`feature:credit`) — FR-131 … FR-136
1. Header: card name + type badge (Kartu kredit / Paylater).
2. **Bill card:** "Tagihan Nov" amount (remainingDue), due date + countdown, status chip (Belum terbit / Belum dibayar / Lunas / Terlambat),
   primary button **"Bayar tagihan"** → S-15.
3. **Limit:** outstanding / limit with a bar; "Sisa limit Rp6,5jt" (or "Melebihi limit").
4. **Siklus:** "Tutup buku · Tanggal 15 (tiap bulan)" and "Jatuh tempo · Tanggal 1 (bulan berikutnya)" rows → day grid drawer (FR-138).
5. **Cicilan aktif:** compact list (title, "2/6", portion) → S-16.
5. **Transaksi:** this cycle's purchases (since last cut-off).
6. **Riwayat tagihan** (S): last 6 statements.
States: NOT_ISSUED (first cycle), DUE, PAID, OVERDUE.

### S-15 Pay bill sheet — FR-132, FR-133
Amount (prefilled remainingDue, editable with keypad), "Dari" wallet chips (non-credit only), quick chips "Lunas" / "Sebagian".
Save → TRANSFER; success toast "Tagihan dibayar"; if remainingDue becomes 0, the bill card animates to "Lunas" with a check.

### S-16 Instalments (`feature:credit`) — FR-142 … FR-146
List of plans: title, card, "Rp500.000/bln", progress ring "2/6", remaining principal, next billing month. Filter: Aktif / Selesai.
Detail: schedule (each portion with statement month and Lunas/Belum), actions "Lunasi sekarang" (FR-144) and "Hapus" (FR-145).

### S-17 Loans (`feature:loan`) — FR-150 … FR-157
Tabs **Piutang** (they owe me) / **Hutang** (I owe). Summary total at top. Rows grouped by person: name avatar (initial),
outstanding, progress bar, due date (red if overdue). Detail: timeline of transactions, buttons **"Catat pembayaran"**
(amount ≤ outstanding) and, for Piutang, **"Ikhlaskan"** (confirm dialog). Settled loans move to a collapsed "Selesai" section.

## Global UI behaviours

- Snackbars for undoable actions (delete, dismiss) — one shared `UndoSnackbar` for every flow (FR-035, FR-039, FR-087):
  - The window is **3 s** (`UndoWindow.MILLIS`), never the long Material default. A thin line under the bar shrinks to
    zero over the window so the user sees how long is left; a new delete restarts it.
  - The bar grows in and fades in (`medium`) and shrinks away the same way when the window ends or Urungkan is tapped (`UndoSlot`).
  - The deleted row leaves at once: it fades and shrinks (`AnimatedRows` in "Atur pintasan", `animateItem` for Kotak Masuk cards).
    Urungkan brings it back with the reverse animation.
  - The bar sits where the user is looking: under the list inside the shortcut drawer, under the form in the add sheet,
    under the facts in the detail sheet, at the bottom of Kotak Masuk.
  - "Remove animations" in system settings makes the motion instant; the countdown line still runs because it is information.
- All destructive actions have confirmation dialogs except those with undo.
- Back from the add sheet with unsaved amount asks "Buang perubahan?".
- Pull-to-refresh is **not** used (data is live via Flow).
