package com.meqlause.fintrack.feature.credit.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.meqlause.fintrack.core.domain.credit.CreditAccountFactory
import com.meqlause.fintrack.core.domain.credit.StatementCalculator
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.transaction.DraftRouting
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.transaction.TransactionDraft
import com.meqlause.fintrack.core.domain.usecase.SaveTransactionUseCase
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.credit.instalment.InstalmentRow
import com.meqlause.fintrack.feature.credit.instalment.toRow
import com.meqlause.fintrack.feature.credit.navigation.CreditDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

// FR-131 to FR-138, S-14, S-15: drives the credit wallet detail and the pay sheet; statement math stays in core:domain
@HiltViewModel
internal class CreditDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val wallets: WalletRepository,
        private val transactions: TransactionRepository,
        private val plans: InstallmentPlanRepository,
        private val saveTransaction: SaveTransactionUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val walletId: WalletId = WalletId(savedStateHandle.toRoute<CreditDetailDestination>().walletId)
        private val session = MutableStateFlow(Session())

        // S-14: Loading until the first lists arrive, then Missing or Success
        val state: StateFlow<CreditDetailUiState> =
            combine(
                wallets.observeAll(),
                transactions.observeAll(),
                plans.observeAll(),
                session,
            ) { all, rows, planRows, current ->
                buildState(all, rows, planRows, current)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), CreditDetailUiState.Loading)

        // FR-132: opens the pay sheet with an empty amount; the first non-credit wallet is the default source
        fun openPay() {
            session.update { it.copy(pay = PayDraft(amount = "", sourceId = null, failed = false)) }
        }

        fun dismissPay() {
            session.update { it.copy(pay = null) }
        }

        // FR-133: only digits are kept so the amount is always a whole rupiah value
        fun setPayAmount(text: String) {
            session.update { s -> s.withPay { it.copy(amount = text.filter(Char::isDigit), failed = false) } }
        }

        fun pickSource(source: WalletId) {
            session.update { s -> s.withPay { it.copy(sourceId = source, failed = false) } }
        }

        // FR-132, FR-133: "Lunas" fills the remaining due, "Sebagian" clears the amount for a partial payment
        fun fillRemaining() {
            val remaining = (state.value as? CreditDetailUiState.Success)?.statement?.breakdown?.remainingDue
            val text = remaining?.rupiah?.toString().orEmpty()
            session.update { s -> s.withPay { it.copy(amount = text, failed = false) } }
        }

        fun clearAmount() {
            session.update { s -> s.withPay { it.copy(amount = "", failed = false) } }
        }

        // FR-132, BR-039: the bill is a TRANSFER from a non-credit wallet, never an EXPENSE; the save rules decide
        fun payBill() {
            val success = state.value as? CreditDetailUiState.Success ?: return
            val draft = success.pay ?: return
            val source = draft.sourceId ?: success.sources.firstOrNull()?.walletId
            val amount = draft.amount.toLongOrNull()
            if (source == null || amount == null) {
                markFailed()
                return
            }
            viewModelScope.launch { store(source, amount) }
        }

        private suspend fun store(
            source: WalletId,
            amount: Long,
        ) {
            val draft =
                TransactionDraft(
                    existing = null,
                    movement = Movement(TransactionType.TRANSFER, Money(amount)),
                    routing = DraftRouting(walletId = source, targetWalletId = walletId, categoryId = null),
                    memo = Memo(),
                    occurredAt = Instant.now(clock),
                )
            if (saveTransaction(draft) is SaveTransactionResult.Saved) {
                session.update { it.copy(pay = null) }
            } else {
                markFailed()
            }
        }

        fun openPicker(day: CreditDay) {
            session.update { it.copy(picker = day) }
        }

        fun dismissPicker() {
            session.update { it.copy(picker = null) }
        }

        // FR-138: the new day is stored at once; the bill recalculates from the stream of wallets
        fun pickDay(day: Int) {
            val picker = session.value.picker ?: return
            viewModelScope.launch {
                val wallet = wallets.observeAll().first().firstOrNull { it.walletId == walletId } ?: return@launch
                wallets.update(withDay(wallet, picker, day))
                session.update { it.copy(picker = null) }
            }
        }

        private fun markFailed() {
            session.update { s -> s.withPay { it.copy(failed = true) } }
        }

        private fun withDay(
            wallet: Wallet,
            picker: CreditDay,
            day: Int,
        ): Wallet {
            val credit = requireNotNull(wallet.terms.credit)
            val updated =
                if (picker == CreditDay.STATEMENT) credit.copy(statementDay = day) else credit.copy(dueDay = day)
            return wallet.copy(
                terms = wallet.terms.copy(credit = updated),
                audit = Audit(createdAt = wallet.audit.createdAt, updatedAt = Instant.now(clock)),
            )
        }

        private fun buildState(
            all: List<Wallet>,
            rows: List<Transaction>,
            planRows: List<InstallmentPlan>,
            current: Session,
        ): CreditDetailUiState {
            val wallet = all.firstOrNull { it.walletId == walletId } ?: return CreditDetailUiState.Missing
            val terms = wallet.terms.credit ?: return CreditDetailUiState.Missing
            val today = LocalDate.now(clock)
            val account = CreditAccountFactory.build(wallet, rows, planRows, clock.zone)
            return CreditDetailUiState.Success(
                name = wallet.profile.name,
                type = wallet.kind.type,
                terms = terms,
                statement = StatementCalculator.current(account, today),
                outstanding = StatementCalculator.outstandingOn(account, today),
                availableLimit = StatementCalculator.availableLimit(account, today),
                today = today,
                sources = all.filter { isPayable(it) }.map { PaySource(it.walletId, it.profile.name) },
                pay = current.pay,
                picker = current.picker,
                instalments = activeInstalments(wallet, terms, planRows, rows, today),
            )
        }

        // S-14: the plans of this card that still have portions to bill, shown in the Cicilan aktif section
        private fun activeInstalments(
            wallet: Wallet,
            terms: CreditTerms,
            planRows: List<InstallmentPlan>,
            rows: List<Transaction>,
            today: LocalDate,
        ): List<InstalmentRow> =
            planRows
                .filter { it.purchase.walletId == walletId }
                .mapNotNull { it.toRow(wallet.profile.name, rows, terms.statementDay, today, clock.zone) }
                .filterNot { it.isFinished }

        // BR-039: a credit wallet can never be the source of a bill payment, so only other wallets are offered
        private fun isPayable(wallet: Wallet): Boolean =
            wallet.kind.type != WalletType.CREDIT_CARD && wallet.kind.type != WalletType.PAYLATER

        private data class Session(
            val pay: PayDraft? = null,
            val picker: CreditDay? = null,
        ) {
            fun withPay(change: (PayDraft) -> PayDraft): Session = pay?.let { copy(pay = change(it)) } ?: this
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
