# Fintrack - Kebijakan Privasi / Privacy Policy

> **Hosting note (not part of the policy; delete this block when publishing elsewhere).**
> The repository is public, so this file can serve as the Play Console privacy policy URL as-is, for example
> `https://github.com/meQlause/fintrack/blob/main/docs/play-store/privacy-policy.md`
> (or the `raw` / GitHub Pages equivalent). The URL must be publicly reachable, must not be a PDF, and must
> not be editable by others. The app's About screen links to this URL.

**Tanggal berlaku / Effective date: 24 November 2026**

---

## Bahasa Indonesia

### 1. Ringkasan

Fintrack adalah aplikasi pencatat keuangan pribadi yang berjalan sepenuhnya di perangkatmu. Aplikasi ini
**tidak memiliki izin internet**, tidak memerlukan akun, tidak memakai iklan, dan tidak memakai analitik atau
pelaporan kerusakan. Data keuanganmu tidak pernah dikirim ke kami maupun ke pihak lain.

### 2. Data yang diproses aplikasi

Semua data berikut dibuat, disimpan, dan diproses **hanya di perangkatmu**:

- **Data yang kamu masukkan:** dompet (nama, jenis, saldo awal), transaksi (jumlah, kategori, catatan,
  tanggal), anggaran, serta data cicilan, pinjaman, dan investasi bila kamu memakai fitur tersebut.
- **Teks notifikasi dari aplikasi bank dan e-wallet yang kamu pilih**, bila kamu mengaktifkan deteksi
  otomatis (lihat bagian 3).
- **Pengaturan aplikasi**, termasuk PIN (disimpan hanya sebagai hash, bukan teks asli) dan pilihan biometrik.

Kami tidak meminta nama, email, nomor telepon, lokasi, kontak, foto, atau pengenal iklan.

### 3. Akses notifikasi

Fintrack dapat membaca notifikasi dari aplikasi bank dan e-wallet untuk mengusulkan transaksi yang kamu
konfirmasi sendiri. Hal ini hanya terjadi jika kamu menyalakan "Akses notifikasi" untuk Fintrack di
pengaturan sistem Android. Kamu dapat mematikannya kapan saja di sana.

- Aplikasi hanya memproses notifikasi dari aplikasi yang kamu aktifkan di Fintrack. Notifikasi dari
  aplikasi lain diabaikan dan tidak disimpan.
- Teks dibaca di perangkat untuk mencari jumlah, arah (masuk/keluar), nama merchant, dan waktu. Notifikasi
  yang bukan transaksi (misalnya OTP, promosi, info saldo) dibuang dan teksnya tidak disimpan.
- Transaksi yang terdeteksi masuk ke Kotak Masuk dan baru dicatat setelah kamu mengonfirmasinya. Teks asli
  notifikasi yang tersimpan di Kotak Masuk dihapus otomatis 30 hari setelah item diselesaikan.
- Tidak ada teks notifikasi yang dikirim keluar perangkat.

### 4. Penyimpanan dan keamanan

- Database lokal dienkripsi dengan SQLCipher. Kunci enkripsinya dilindungi oleh Android Keystore dan tidak
  dapat diekspor dari perangkat.
- Kamu dapat mengunci aplikasi dengan PIN 6 digit dan/atau biometrik.
- Pencadangan awan Android dan transfer perangkat otomatis dinonaktifkan untuk aplikasi ini, sehingga data
  tidak ikut tersalin ke Google atau perangkat lain tanpa tindakanmu.
- Karena tidak ada akun, kami tidak dapat memulihkan PIN atau data yang hilang.

### 5. Cadangan dan ekspor

- **Cadangan (`.fintrack`):** dibuat hanya saat kamu memintanya, dienkripsi dengan kata sandi pilihanmu
  (AES-256), dan disimpan ke lokasi yang kamu pilih lewat pemilih file Android. Aplikasi tidak mengunggah
  file ini ke mana pun. Jika kamu menyimpannya ke layanan pihak ketiga (misalnya Google Drive), layanan
  itu tunduk pada kebijakan privasinya sendiri. Kata sandi tidak disimpan dan tidak bisa dipulihkan.
- **Ekspor CSV:** dibuat hanya saat kamu memintanya dan **tidak dienkripsi**. Siapa pun yang memegang
  file tersebut dapat membacanya. Jaga file CSV dengan hati-hati.

### 6. Berbagi data

Kami tidak mengumpulkan, menjual, atau membagikan datamu kepada siapa pun, karena aplikasi tidak
mengirim data apa pun keluar perangkat. Aplikasi tidak memuat SDK pihak ketiga yang mengumpulkan data.

### 7. Kendali dan penghapusan data

- Kamu dapat mengedit atau menghapus data kapan saja di dalam aplikasi.
- **Menghapus semua data:** copot pemasangan Fintrack, atau buka Pengaturan Android > Aplikasi > Fintrack >
  Penyimpanan > Hapus data. Karena data hanya ada di perangkatmu, kami tidak memiliki salinan untuk dihapus.
- File cadangan dan CSV yang sudah kamu simpan berada di luar aplikasi; hapus sendiri dari lokasi
  penyimpanannya.
- Mencabut akses notifikasi: Pengaturan Android > Notifikasi > Akses notifikasi.

### 8. Anak-anak

Fintrack tidak ditujukan untuk anak di bawah 13 tahun dan tidak dengan sengaja mengumpulkan data dari
anak-anak. Karena aplikasi tidak mengirim data apa pun, tidak ada data anak yang kami terima.

### 9. Perubahan kebijakan

Jika kebijakan ini berubah, versi baru akan dipublikasikan di alamat yang sama dengan tanggal berlaku yang
diperbarui. Perubahan material, misalnya bila aplikasi suatu saat mengirim data keluar perangkat, akan
diumumkan di dalam aplikasi sebelum berlaku.

### 10. Kontak

Pertanyaan tentang privasi: **ardialbrian@gmail.com**

---

## English

### 1. Summary

Fintrack is a personal finance tracker that runs entirely on your device. It has **no internet permission**,
needs no account, shows no ads, and uses no analytics or crash reporting. Your financial data is never sent
to us or to anyone else.

### 2. Data the app handles

All of the following is created, stored and processed **only on your device**:

- **Data you enter:** wallets (name, type, opening balance), transactions (amount, category, note, date),
  budgets, and instalment, loan and investment records if you use those features.
- **Notification text from the banking and e-wallet apps you select**, if you turn on automatic detection
  (see section 3).
- **App settings**, including the PIN (stored only as a hash, never as plain text) and your biometric choice.

We do not ask for your name, email, phone number, location, contacts, photos or advertising ID.

### 3. Notification access

Fintrack can read notifications from banking and e-wallet apps to suggest transactions that you confirm
yourself. This happens only if you switch on "Notification access" for Fintrack in Android system settings.
You can switch it off there at any time.

- The app processes only notifications from apps you enable inside Fintrack. Notifications from all other
  apps are ignored and not stored.
- Text is read on the device to find the amount, direction (money in/out), merchant name and time.
  Notifications that are not transactions (for example OTPs, promotions, balance info) are discarded and
  their text is not stored.
- Detected transactions go to an Inbox and are recorded only after you confirm them. The raw notification
  text held in the Inbox is deleted automatically 30 days after the item is resolved.
- No notification text leaves the device.

### 4. Storage and security

- The local database is encrypted with SQLCipher. Its key is protected by the Android Keystore and cannot be
  exported from the device.
- You can lock the app with a 6-digit PIN and/or biometrics.
- Android cloud backup and automatic device transfer are disabled for this app, so data is not copied to
  Google or another device without an action from you.
- Because there is no account, we cannot recover a forgotten PIN or lost data.

### 5. Backup and export

- **Backup (`.fintrack`):** created only when you ask for it, encrypted (AES-256) with a password you choose,
  and saved to a location you pick through the Android file picker. The app never uploads this file. If you
  save it to a third-party service (for example Google Drive), that service is governed by its own privacy
  policy. The password is not stored and cannot be recovered.
- **CSV export:** created only when you ask for it and is **not encrypted**. Anyone holding the file can read
  it. Handle it with care.

### 6. Sharing

We do not collect, sell or share your data with anyone, because the app transmits nothing off the device.
The app contains no third-party SDK that collects data.

### 7. Your control and deletion

- You can edit or delete your data inside the app at any time.
- **To delete all data:** uninstall Fintrack, or go to Android Settings > Apps > Fintrack > Storage > Clear
  data. Because data exists only on your device, we hold no copy to delete.
- Backup and CSV files you saved live outside the app; delete them yourself from where you stored them.
- To revoke notification access: Android Settings > Notifications > Notification access.

### 8. Children

Fintrack is not directed at children under 13 and does not knowingly collect data from children. Because the
app transmits no data, we receive no data about anyone, children included.

### 9. Changes to this policy

If this policy changes, the new version will be published at the same address with an updated effective
date. Material changes, for example if the app ever sends data off the device, will be announced in the app
before they take effect.

### 10. Contact

Privacy questions: **ardialbrian@gmail.com**
