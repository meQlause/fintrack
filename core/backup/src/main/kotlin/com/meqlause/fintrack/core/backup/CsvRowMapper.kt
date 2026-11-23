package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// FR-113, DOC-11: turns one transaction into the cells of the CSV columns, in column order
internal class CsvRowMapper(
    private val walletNames: Map<WalletId, String>,
    private val categoryNames: Map<CategoryId, String>,
    private val personNames: Map<LoanId, String>,
    private val zone: ZoneId,
) {
    /** DOC-11: tanggal, waktu, jenis, jumlah, kategori, dompet, dompet_tujuan, biaya, catatan, merchant, sumber. */
    fun cells(transaction: Transaction): List<String> {
        val movement = transaction.movement
        val routing = transaction.routing
        val time = transaction.timing.occurredAt.atZone(zone)
        return listOf(
            DATE_FORMAT.format(time),
            TIME_FORMAT.format(time),
            typeLabel(movement.type),
            movement.amount.rupiah.toString(),
            categoryCell(transaction),
            walletNames[routing.walletId].orEmpty(),
            routing.targetWalletId?.let { walletNames[it] }.orEmpty(),
            movement.feeAmount.rupiah.toString(),
            transaction.memo.note,
            transaction.memo.merchant,
            sourceLabel(transaction.origin.source),
        )
    }

    // DOC-11: for loan rows the kategori cell holds the person's name
    private fun categoryCell(transaction: Transaction): String {
        val loanId = transaction.routing.loanId
        if (loanId != null) return personNames[loanId].orEmpty()
        return transaction.routing.categoryId
            ?.let { categoryNames[it] }
            .orEmpty()
    }

    private companion object {
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}

// DOC-11: the jenis values written to the CSV, in Indonesian
private fun typeLabel(type: TransactionType): String =
    when (type) {
        TransactionType.EXPENSE -> "Pengeluaran"
        TransactionType.INCOME -> "Pemasukan"
        TransactionType.TRANSFER -> "Transfer"
        TransactionType.LEND -> "Pinjamkan"
        TransactionType.BORROW -> "Pinjam"
        TransactionType.COLLECT -> "Terima cicilan pinjaman"
        TransactionType.REPAY -> "Bayar pinjaman"
    }

// DOC-11: the sumber values written to the CSV
private fun sourceLabel(source: TransactionSource): String =
    when (source) {
        TransactionSource.MANUAL -> "Manual"
        TransactionSource.NOTIFICATION -> "Notifikasi"
    }
