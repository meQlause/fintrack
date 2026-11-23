package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
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
class CsvRowMapperTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val mapper =
        CsvRowMapper(
            walletNames = mapOf(WalletId(1) to "GoPay", WalletId(2) to "BCA"),
            categoryNames = mapOf(CategoryId(1) to "Makanan & Minuman"),
            personNames = emptyMap(),
            zone = zone,
        )

    @Test
    fun givenExpenseWhenCellsThenMatchesDocExample() {
        val expense = transaction(TransactionType.EXPENSE, 25_000, 0, Memo("Kopi", "KOPI KENANGAN"), WalletId(1), null)
        assertThat(mapper.cells(expense).joinToString(",")).isEqualTo(
            "2026-11-20,14:05,Pengeluaran,25000,Makanan & Minuman,GoPay,,0,Kopi,KOPI KENANGAN,Notifikasi",
        )
    }

    @Test
    fun givenTransferWhenCellsThenFillsTargetWalletAndFee() {
        val transfer = transaction(TransactionType.TRANSFER, 50_000, 1_000, Memo(), WalletId(1), WalletId(2))
        val cells = mapper.cells(transfer)
        assertThat(cells[5]).isEqualTo("GoPay")
        assertThat(cells[6]).isEqualTo("BCA")
        assertThat(cells[7]).isEqualTo("1000")
        assertThat(cells[2]).isEqualTo("Transfer")
    }

    private fun transaction(
        type: TransactionType,
        amount: Long,
        fee: Long,
        memo: Memo,
        wallet: WalletId,
        target: WalletId?,
    ): Transaction {
        val occurred = Instant.parse("2026-11-20T07:05:00Z")
        val category = if (type == TransactionType.EXPENSE) CategoryId(1) else null
        return Transaction(
            TransactionId(1),
            Movement(type, Money(amount), Money(fee)),
            Routing(wallet, target, category, null),
            memo,
            Origin(TransactionSource.NOTIFICATION, null),
            Timing(occurred, Audit(occurred, occurred)),
        )
    }
}
