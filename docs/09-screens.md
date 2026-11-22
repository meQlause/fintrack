# 09 — Screens

Every screen lists: purpose, entry points, layout (top → bottom), states, actions, and requirements covered.
UI text shown here is the **Indonesian string** to put in `strings.xml`.

## Navigation map

```
[Splash] ─► (first launch?) ─► Onboarding ─► Dashboard
        └─► (PIN set & locked?) ─► Lock ─► Dashboard

Bottom bar:  Beranda | Riwayat | (+) | Statistik | Dompet

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
3. **Notification detection** — explanation + illustration. "Aktifkan" → system settings; "Nanti saja".
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
- **Tentang:** versi, lisensi open source, pernyataan privasi.

## S-13 Monitored apps (`feature:settings`) — FR-081, FR-082, FR-090

List of supported apps that are **installed** (query by package via `<queries>` manifest entries). Each row:
app name, switch (enabled), "Dompet default" picker, "Terakhir diterima: 2 jam lalu".
Section "Belum didukung?" explaining how to send sample notifications to the developer. Button "Salin contoh"
copies the latest unrecognized notification text from that app to the clipboard, with digits of account/reference
numbers masked as `XXXX` (Q-009). Offline only; the user shares it manually.

## Global UI behaviours

- Snackbars for undoable actions (delete, dismiss) — 5 s.
- All destructive actions have confirmation dialogs except those with undo.
- Back from the add sheet with unsaved amount asks "Buang perubahan?".
- Pull-to-refresh is **not** used (data is live via Flow).
