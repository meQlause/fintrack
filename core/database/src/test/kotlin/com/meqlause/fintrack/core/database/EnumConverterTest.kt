package com.meqlause.fintrack.core.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.database.converter.CoreEnumConverters
import com.meqlause.fintrack.core.database.converter.PlanEnumConverters
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletType
import org.junit.Test

/** DOC-02: enums are stored by name; an absent investment kind stays null. */
class EnumConverterTest {
    private val core = CoreEnumConverters()
    private val plan = PlanEnumConverters()

    /** DOC-02: wallet, investment, category, transaction and inbox enums round-trip through their names. */
    @Test
    fun `core enums round trip through their names (DOC-02)`() {
        WalletType.entries.forEach { assertThat(core.nameToWalletType(core.walletTypeToName(it))).isEqualTo(it) }
        InvestKind.entries.forEach { assertThat(core.nameToInvestKind(core.investKindToName(it))).isEqualTo(it) }
        CategoryKind.entries.forEach { assertThat(core.nameToCategoryKind(core.categoryKindToName(it))).isEqualTo(it) }
        TransactionType.entries.forEach {
            assertThat(core.nameToTransactionType(core.transactionTypeToName(it))).isEqualTo(it)
        }
        TransactionSource.entries.forEach {
            assertThat(core.nameToTransactionSource(core.transactionSourceToName(it))).isEqualTo(it)
        }
        MoneyDirection.entries.forEach {
            assertThat(core.nameToMoneyDirection(core.moneyDirectionToName(it))).isEqualTo(it)
        }
        InboxStatus.entries.forEach { assertThat(core.nameToInboxStatus(core.inboxStatusToName(it))).isEqualTo(it) }
    }

    /** DOC-02: installment and loan enums round-trip through their names. */
    @Test
    fun `plan enums round trip through their names (DOC-02)`() {
        InstallmentStatus.entries.forEach {
            assertThat(plan.nameToInstallmentStatus(plan.installmentStatusToName(it))).isEqualTo(it)
        }
        LoanDirection.entries.forEach {
            assertThat(
                plan.nameToLoanDirection(plan.loanDirectionToName(it)),
            ).isEqualTo(it)
        }
        LoanStatus.entries.forEach { assertThat(plan.nameToLoanStatus(plan.loanStatusToName(it))).isEqualTo(it) }
    }

    /** DOC-02: a wallet without an investment kind stores NULL, not a name. */
    @Test
    fun `absent investment kind stays null (DOC-02)`() {
        assertThat(core.investKindToName(null)).isNull()
        assertThat(core.nameToInvestKind(null)).isNull()
    }
}
