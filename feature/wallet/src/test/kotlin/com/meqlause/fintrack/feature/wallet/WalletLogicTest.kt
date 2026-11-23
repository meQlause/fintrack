package com.meqlause.fintrack.feature.wallet

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import org.junit.Test
import java.time.Instant

// FR-010, FR-011, FR-139, FR-170, S-09: the pure rules of the wallet editor, its labels and its mapping
class WalletLogicTest {
    private val stamp = Instant.parse("2026-11-23T00:00:00Z")

    private fun storedWallet(
        type: WalletType,
        credit: CreditTerms? = null,
        invest: InvestKind? = null,
    ): Wallet =
        Wallet(
            walletId = WalletId(4L),
            kind = WalletKind(type, invest),
            terms = WalletTerms(Money(150_000L), credit),
            profile = WalletProfile("Dompet", Appearance("wallet", "blue"), sortOrder = 2),
            status = WalletStatus(isIncludedInTotal = false),
            audit = Audit(Instant.EPOCH, Instant.EPOCH),
        )

    /** S-09: every wallet type, group and investment kind has a label, a group and an appearance. */
    @Test
    fun `given every type when labelled and grouped then each is covered (S-09)`() {
        val labels = WalletType.entries.map { WalletLabels.type(it) }
        assertThat(labels.toSet().size).isEqualTo(WalletType.entries.size)
        val groups = WalletGroupKey.entries.map { WalletLabels.group(it) }
        assertThat(groups.toSet().size).isEqualTo(WalletGroupKey.entries.size)
        val kinds = InvestKind.entries.map { WalletLabels.investKind(it) }
        assertThat(kinds.toSet().size).isEqualTo(InvestKind.entries.size)
        WalletType.entries.forEach { type ->
            assertThat(WalletGroupKey.forType(type) in WalletGroupKey.entries).isTrue()
            assertThat(WalletAppearance.forType(type).iconKey.isNotEmpty()).isTrue()
        }
    }

    /** FR-011: the draft of a stored credit wallet carries its limit, days and flags. */
    @Test
    fun `given a stored credit wallet when its draft is made then it carries the stored terms (FR-011)`() {
        val credit = CreditTerms(Money(5_000_000L), statementDay = 20, dueDay = 5)
        val draft = WalletEditor.forWallet(WalletItem(storedWallet(WalletType.CREDIT_CARD, credit), Money(0L)))
        assertThat(draft.isCredit).isTrue()
        assertThat(draft.creditLimit).isEqualTo("5000000")
        assertThat(draft.statementDay).isEqualTo(20)
        assertThat(draft.dueDay).isEqualTo(5)
        assertThat(draft.includeInTotal).isFalse()
        assertThat(draft.initialBalance).isEqualTo("150000")
        assertThat(draft.isInvestment).isFalse()
    }

    /** FR-010: a new draft starts as cash with day defaults; every setter changes only its own part. */
    @Test
    fun `given a new draft when its parts are set then each setter changes its own part (FR-010)`() {
        val draft = WalletEditor.forNew()
        assertThat(draft.type).isEqualTo(WalletType.CASH)
        assertThat(draft.withName("Tabungan").name).isEqualTo("Tabungan")
        assertThat(draft.withInitialBalance("1a2b").initialBalance).isEqualTo("12")
        assertThat(draft.withCreditLimit("9x9").creditLimit).isEqualTo("99")
        assertThat(draft.withIncludeInTotal(false).includeInTotal).isFalse()
        assertThat(draft.withType(WalletType.INVESTMENT).isInvestment).isTrue()
        assertThat(draft.withInvestKind(InvestKind.GOLD).investKind).isEqualTo(InvestKind.GOLD)
        assertThat(draft.withDay(WalletPicker.STATEMENT_DAY, 3).statementDay).isEqualTo(3)
        assertThat(draft.withDay(WalletPicker.DUE_DAY, 4).dueDay).isEqualTo(4)
        assertThat(draft.withDay(WalletPicker.TYPE, 5)).isEqualTo(draft)
        assertThat(draft.withDay(null, 5)).isEqualTo(draft)
        val failed = draft.withErrors(WalletNameError.BLANK, limitProblem = true)
        assertThat(failed.nameError).isEqualTo(WalletNameError.BLANK)
        assertThat(failed.limitError).isTrue()
        assertThat(failed.withName("Ada").nameError).isNull()
        assertThat(failed.withCreditLimit("1").limitError).isFalse()
    }

    /** FR-010, FR-011: a new wallet and an edit keep the right parts of the draft and the stored wallet. */
    @Test
    fun `given drafts when mapped then new wallets and edits keep the right parts (FR-011)`() {
        val credit = CreditTerms(Money(5_000_000L), statementDay = 20, dueDay = 5)
        val draft =
            WalletEditor
                .forNew()
                .withName(
                    "  Kartu ",
                ).withType(WalletType.CREDIT_CARD)
                .withCreditLimit("2000000")
        val created = WalletDraftMapper.newWallet(draft, sortOrder = 7, currentTime = stamp)
        assertThat(created.profile.name).isEqualTo("Kartu")
        assertThat(created.profile.sortOrder).isEqualTo(7)
        assertThat(created.terms.credit?.creditLimit).isEqualTo(Money(2_000_000L))
        val stored = storedWallet(WalletType.CREDIT_CARD, credit)
        val edited = WalletDraftMapper.applyTo(stored, draft.withName("Baru"), stamp)
        assertThat(edited.walletId).isEqualTo(stored.walletId)
        assertThat(edited.profile.sortOrder).isEqualTo(2)
        assertThat(edited.audit.updatedAt).isEqualTo(stamp)
        val fund = WalletDraftMapper.newWallet(WalletEditor.forNew().withType(WalletType.INVESTMENT), 0, stamp)
        assertThat(fund.kind.investKind).isEqualTo(InvestKind.MUTUAL_FUND)
        val cash = WalletDraftMapper.newWallet(WalletEditor.forNew().withName("Tunai"), 0, stamp)
        assertThat(cash.kind.investKind).isNull()
        assertThat(cash.terms.credit).isNull()
        assertThat(WalletDraftMapper.nextSortOrder(emptyList())).isEqualTo(0)
        assertThat(WalletDraftMapper.nextSortOrder(listOf(stored))).isEqualTo(3)
        assertThat(WalletDraftMapper.archived(stored, archived = true, currentTime = stamp).status.isArchived).isTrue()
    }

    /** S-09: the callbacks of a screen without wiring do nothing and never fail. */
    @Test
    fun `given default callbacks when invoked then they do nothing (S-09)`() {
        val callbacks = WalletCallbacks()
        callbacks.onAdd()
        callbacks.onOpenDetail(WalletId(1L), WalletType.CASH)
        callbacks.onOpenLoans()
        callbacks.onToggleArchived()
        callbacks.onNameChange("x")
        callbacks.onInitialBalanceChange("1")
        callbacks.onCreditLimitChange("1")
        callbacks.onIncludeInTotalChange(true)
        callbacks.onOpenPicker(WalletPicker.TYPE)
        callbacks.onDismissPicker()
        callbacks.onPickType(WalletType.CASH)
        callbacks.onPickInvestKind(InvestKind.GOLD)
        callbacks.onPickDay(1)
        callbacks.onSetArchivedEditing(true)
        callbacks.onSave()
        callbacks.onDismissEditor()
        assertThat(WalletPicker.entries.size).isEqualTo(4)
    }
}
