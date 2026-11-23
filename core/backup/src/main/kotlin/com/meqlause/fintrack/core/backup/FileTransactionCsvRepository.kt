package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.port.BackupFiles
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionCsvRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

private val CSV_HEADER =
    listOf(
        "tanggal",
        "waktu",
        "jenis",
        "jumlah",
        "kategori",
        "dompet",
        "dompet_tujuan",
        "biaya",
        "catatan",
        "merchant",
        "sumber",
    )

/** FR-113, DOC-11: writes the chosen date range as UTF-8 CSV with a byte order mark; the file is not encrypted */
public class FileTransactionCsvRepository(
    private val transactions: TransactionRepository,
    private val wallets: WalletRepository,
    private val categories: CategoryRepository,
    private val loans: LoanRepository,
    private val files: BackupFiles,
    private val ioDispatcher: CoroutineDispatcher,
) : TransactionCsvRepository {
    override suspend fun export(
        fileUri: String,
        start: LocalDate,
        endDate: LocalDate,
        zone: ZoneId,
    ) {
        val text =
            withContext(ioDispatcher) {
                val mapper = CsvRowMapper(walletNames(), categoryNames(), personNames(), zone)
                val rows = inRange(start, endDate, zone)
                val body = rows.joinToString("") { CsvText.record(mapper.cells(it)) }
                CsvText.BYTE_ORDER_MARK + CsvText.record(CSV_HEADER) + body
            }
        files.write(fileUri, text.toByteArray(Charsets.UTF_8))
    }

    private suspend fun inRange(
        start: LocalDate,
        endDate: LocalDate,
        zone: ZoneId,
    ): List<Transaction> =
        transactions
            .observeAll()
            .first()
            .filter {
                it.timing.occurredAt
                    .atZone(zone)
                    .toLocalDate() in start..endDate
            }.sortedBy { it.timing.occurredAt }

    private suspend fun walletNames() = wallets.observeAll().first().associate { it.walletId to it.profile.name }

    private suspend fun categoryNames() = categories.observeAll().first().associate { it.categoryId to it.profile.name }

    private suspend fun personNames() = loans.observeAll().first().associate { it.loanId to it.agreement.personName }
}
