# 11 — Backup, Restore & Export

Module: `core:backup`. Requirements: FR-110 … FR-113.
Because the app is fully offline, **backup is the only protection against losing the phone.** It must be dependable.

## Backup file

- Extension: `.fintrack`  — suggested name: `fintrack-backup-2026-11-22.fintrack`
- Saved anywhere the user picks via `ACTION_CREATE_DOCUMENT` (Downloads, Google Drive app, USB, etc.).
  The **user** moves it off-device (e.g. via the Drive app); our app never uses the network.

### Binary layout

```
offset  size  content
0       8     magic "FINTRACK" (ASCII)
8       2     container format version (UInt16, big-endian) = 1
10      16    PBKDF2 salt
26      12    AES-GCM IV (nonce)
38      4     PBKDF2 iteration count (UInt32, BE) = 310000
42      ...   AES-256-GCM ciphertext of GZIP( UTF-8 JSON payload ), with 16-byte auth tag appended
```

- Key = `PBKDF2WithHmacSHA256(password, salt, iterations, 256 bits)`.
- The 42-byte header is passed as **AAD** to AES-GCM, so tampering with the header fails decryption.
- Password: minimum 8 characters, entered twice. Not stored anywhere. Warning: "Tanpa kata sandi ini, cadangan tidak bisa dipulihkan."

### JSON payload (schema version 1)

```json
{
  "schemaVersion": 1,
  "appVersionName": "1.0.0",
  "databaseVersion": 1,
  "createdAt": "2026-11-22T08:00:00Z",
  "wallets":        [ { "walletId": 1, "name": "Tunai", "type": "CASH", "initialBalance": 0, "...": "all columns" } ],
  "categories":     [ ... ],
  "transactions":   [ ... ],
  "budgets":        [ ... ],
  "budgetAlertLogs":[ ... ],
  "merchantRules":  [ ... ],
  "monitoredApps":  [ ... ],
  "inboxItems":     [ ... only PENDING items, raw text included ],
  "installmentPlans": [ ... ],
  "loans":          [ ... ],
  "settings":       { "themeMode": "DARK", "periodStartDay": 25, "...": "non-secret settings only" }
}
```

- Serialized with kotlinx-serialization using dedicated `@Serializable` DTOs in `core:backup` (not Room entities),
  so the database schema can evolve independently of the backup schema.
- **Excluded:** PIN hash/salt, lock state, DB passphrase, `lastBackupAt`.
- Field names = domain field names (02). Enums as strings. Times as ISO-8601 UTC strings. Money as integer rupiah.

## Create backup flow (FR-110)

1. Settings → "Buat cadangan" → password dialog (twice).
2. SAF create-document picker.
3. On `@IoDispatcher`: read all tables inside **one read transaction** (consistent snapshot) → DTOs → JSON → GZIP → encrypt → write stream.
4. Verify: re-open the written file, decrypt and parse it fully (self-check). Only then report success.
5. Update `lastBackupAt`. Snackbar "Cadangan berhasil disimpan".
6. Errors → dialog with a human message; the partial file is deleted via `DocumentsContract.deleteDocument` when possible.

## Restore flow (FR-111)

1. Settings → "Pulihkan" → SAF open-document picker.
2. Size check: reject files > 50 MB.
3. Header check: magic + supported container version → else "File bukan cadangan Fintrack" / "Versi cadangan tidak didukung, perbarui aplikasi".
4. Password dialog → decrypt (wrong password = GCM auth failure → "Kata sandi salah").
5. Parse + **validate everything** before touching the database:
   - `schemaVersion` ≤ current supported version (older versions are migrated in memory by `BackupMigrator`).
   - All BR rules (amount range, transfer rules, FK references exist, unique names, enum values valid).
   - On any violation: abort with "Cadangan rusak" + no data changed.
6. Show summary: "12 dompet, 1.540 transaksi, dibuat 20 Nov 2026" + warning "Semua data saat ini akan diganti."
7. Confirm → in **one database transaction**: delete all rows, insert all rows preserving ids. Failure → rollback (old data intact).
8. Restore settings (non-secret). PIN stays as currently configured on this device.
9. Restart the activity/navigate to Dashboard.

## Backup reminder (FR-112)

- Periodic WorkManager job, once a day.
- If `backupReminderDays > 0` and (`lastBackupAt` is null and ≥ 1 transaction exists, or `now − lastBackupAt ≥ backupReminderDays`):
  post on channel `backup_reminder`: "Sudah 7 hari sejak cadangan terakhir. Cadangkan sekarang?" → opens backup screen.
- At most one reminder per day.

## CSV export (FR-113)

- Settings → "Ekspor CSV" → date range (default current period) → SAF create document `fintrack-2026-11.csv`.
- UTF-8 **with BOM** (so Excel opens Indonesian text correctly), separator `,`, RFC 4180 quoting, CRLF line endings.
- Columns:

`jenis` values: Pengeluaran, Pemasukan, Transfer, Pinjamkan, Pinjam, Terima cicilan pinjaman, Bayar pinjaman.
For loan rows, `kategori` holds the person name.

```
tanggal,waktu,jenis,jumlah,kategori,dompet,dompet_tujuan,biaya,catatan,merchant,sumber
2026-11-20,14:05,Pengeluaran,25000,Makanan & Minuman,GoPay,,0,Kopi,KOPI KENANGAN,Notifikasi
```

- `jumlah` and `biaya` are plain integers (no `Rp`, no separators) so spreadsheets can sum them.
- CSV is **not encrypted** — the UI warns: "File CSV tidak terenkripsi."
- CSV is export-only (no import in v1).

## Tests required

- Round-trip: build data → backup → restore into empty DB → all tables equal.
- Wrong password, truncated file, modified header byte, modified ciphertext byte → each fails cleanly, DB unchanged.
- Schema validation: each BR violation in a crafted payload is rejected.
- CSV: quoting of commas, quotes and newlines in notes; BOM present; correct column order.
