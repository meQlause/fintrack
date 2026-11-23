package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

// FR-113, DOC-11: the CSV cells of a transaction follow the fixed column order and Indonesian labels

/** Csv row mapper tests. */
class CsvRowMapperTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val mapper =
        CsvRowMapper(
            walletNames = mapOf(WalletId(1) to "GoPay", WalletId(2) to "BCA"),
            categoryNames = mapOf(CategoryId(1) to "Makanan & Minuman"),
            personNames = emptyMap(),
            zone = zone,
        )

    /** Given expense when cells then matches doc example. */
    @Test
    fun givenExpenseWhenCellsThenMatchesDocExample() {
        val expense =
            transaction(
                Movement(TransactionType.EXPENSE, Money(25_000), Money(0)),
                Memo("Kopi", "KOPI KENANGAN"),
                WalletId(1),
                null,
            )
        assertThat(mapper.cells(expense).joinToString(",")).isEqualTo(
            "2026-11-20,14:05,Pengeluaran,25000,Makanan & Minuman,GoPay,,0,Kopi,KOPI KENANGAN,Notifikasi",
        )
    }

    /** Given transfer when cells then fills target wallet and fee. */
    @Test
    fun givenTransferWhenCellsThenFillsTargetWalletAndFee() {
        val transfer =
            transaction(
                Movement(TransactionType.TRANSFER, Money(50_000), Money(1_000)),
                Memo(),
                WalletId(1),
                WalletId(2),
            )
        val cells = mapper.cells(transfer)
        assertThat(cells[5]).isEqualTo("GoPay")
        assertThat(cells[6]).isEqualTo("BCA")
        assertThat(cells[7]).isEqualTo("1000")
        assertThat(cells[2]).isEqualTo("Transfer")
    }

    /** Every transaction type has its Indonesian label in the jenis column. */
    @Test
    fun everyTypeHasItsIndonesianLabel() {
        val expected =
            mapOf(
                TransactionType.EXPENSE to "Pengeluaran",
                TransactionType.INCOME to "Pemasukan",
                TransactionType.TRANSFER to "Transfer",
                TransactionType.LEND to "Pinjamkan",
                TransactionType.BORROW to "Pinjam",
                TransactionType.COLLECT to "Terima cicilan pinjaman",
                TransactionType.REPAY to "Bayar pinjaman",
            )
        for ((type, label) in expected) {
            val sample = transaction(Movement(type, Money(1_000), Money(0)), Memo(), WalletId(1), null)
            assertThat(mapper.cells(sample)[2]).isEqualTo(label)
        }
    }

    /** A manual transaction is labelled Manual and a loan row shows the person's name in the kategori column. */
    @Test
    fun manualSourceAndLoanRowsUseTheirLabels() {
        val plain = transaction(Movement(TransactionType.EXPENSE, Money(1_000), Money(0)), Memo(), WalletId(1), null)
        val manualRow = plain.copy(origin = Origin(TransactionSource.MANUAL, null))
        assertThat(mapper.cells(manualRow)[10]).isEqualTo("Manual")
        val loanRow = plain.copy(routing = Routing(WalletId(1), null, null, LoanId(1)))
        val withPerson = CsvRowMapper(emptyMap(), emptyMap(), mapOf(LoanId(1) to "Budi"), zone)
        assertThat(withPerson.cells(loanRow)[4]).isEqualTo("Budi")
    }

    private fun transaction(
        movement: Movement,
        memo: Memo,
        wallet: WalletId,
        target: WalletId?,
    ): Transaction {
        val occurred = Instant.parse("2026-11-20T07:05:00Z")
        val category = if (movement.type == TransactionType.EXPENSE) CategoryId(1) else null
        return Transaction(
            TransactionId(1),
            movement,
            Routing(wallet, target, category, null),
            memo,
            Origin(TransactionSource.NOTIFICATION, null),
            Timing(occurred, Audit(occurred, occurred)),
        )
    }
}
