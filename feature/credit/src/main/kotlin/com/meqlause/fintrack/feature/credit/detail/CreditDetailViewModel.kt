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
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.credit.instalment.InstalmentRow
import com.meqlause.fintrack.feature.credit.instalment.StatementCutOff
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
            session.update { it.copy(payDraft = PayDraft(typedDigits = "", sourceId = null, failed = false)) }
        }

        fun dismissPay() {
            session.update { it.copy(payDraft = null) }
        }

        // FR-133: only digits are kept so the amount is always a whole rupiah value
        fun setPayAmount(text: String) {
            session.update { s -> s.withPay { it.copy(typedDigits = text.filter(Char::isDigit), failed = false) } }
        }

        fun pickSource(source: WalletId) {
            session.update { s -> s.withPay { it.copy(sourceId = source, failed = false) } }
        }

        // FR-132, FR-133: "Lunas" fills the remaining due, "Sebagian" clears the amount for a partial payment
        fun fillRemaining() {
            val remaining = (state.value as? CreditDetailUiState.Success)?.statement?.breakdown?.remainingDue
            val text = remaining?.rupiah?.toString().orEmpty()
            session.update { s -> s.withPay { it.copy(typedDigits = text, failed = false) } }
        }

        fun clearAmount() {
            session.update { s -> s.withPay { it.copy(typedDigits = "", failed = false) } }
        }

        // FR-132, BR-039: the bill is a TRANSFER from a non-credit wallet, never an EXPENSE; the save rules decide
        fun payBill() {
            val draft = session.value.payDraft ?: return
            val success = state.value as? CreditDetailUiState.Success ?: return
            val source = draft.sourceId ?: success.sources.firstOrNull()?.walletId
            val amount = draft.typedDigits.toLongOrNull()
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
                session.update { it.copy(payDraft = null) }
            } else {
                markFailed()
            }
        }

        fun openPicker(creditDay: CreditDay) {
            session.update { it.copy(picker = creditDay) }
        }

        fun dismissPicker() {
            session.update { it.copy(picker = null) }
        }

        // FR-138: the new day is stored at once; the bill recalculates from the stream of wallets
        fun pickDay(pickedDay: Int) {
            val picker = session.value.picker ?: return
            viewModelScope.launch {
                val wallet = wallets.observeAll().first().firstOrNull { it.walletId == walletId } ?: return@launch
                wallets.update(wallet.withCreditDay(picker, pickedDay, Instant.now(clock)))
                session.update { it.copy(picker = null) }
            }
        }

        private fun markFailed() {
            session.update { s -> s.withPay { it.copy(failed = true) } }
        }

        private fun buildState(
            allWallets: List<Wallet>,
            rows: List<Transaction>,
            planRows: List<InstallmentPlan>,
            current: Session,
        ): CreditDetailUiState {
            val wallet = allWallets.firstOrNull { it.walletId == walletId } ?: return CreditDetailUiState.Missing
            val terms = wallet.terms.credit ?: return CreditDetailUiState.Missing
            val today = LocalDate.now(clock)
            val account = CreditAccountFactory.build(wallet, rows, planRows, clock.zone)
            return CreditDetailUiState.Success(
                identity = CreditIdentity(name = wallet.profile.name, type = wallet.kind.type),
                figures =
                    CreditFigures(
                        terms = terms,
                        statement = StatementCalculator.current(account, today),
                        outstanding = StatementCalculator.outstandingOn(account, today),
                        availableLimit = StatementCalculator.availableLimit(account, today),
                    ),
                today = today,
                pay =
                    PayState(
                        sources =
                            allWallets
                                .filter {
                                    it.isPayableSource()
                                }.map { PaySource(it.walletId, it.profile.name) },
                        payDraft = current.payDraft,
                    ),
                picker = current.picker,
                instalments = activeInstalments(wallet, planRows, rows, today),
            )
        }

        // S-14: the plans of this card that still have portions to bill, shown in the Cicilan aktif section
        private fun activeInstalments(
            wallet: Wallet,
            planRows: List<InstallmentPlan>,
            rows: List<Transaction>,
            today: LocalDate,
        ): List<InstalmentRow> =
            planRows
                .filter { it.purchase.walletId == walletId }
                .mapNotNull {
                    it.toRow(
                        cardName = wallet.profile.name,
                        transactions = rows,
                        cutOff = StatementCutOff(statementDay = wallet.terms.credit?.statementDay ?: 1, today = today),
                        zone = clock.zone,
                    )
                }.filterNot { it.isFinished }

        private data class Session(
            val payDraft: PayDraft? = null,
            val picker: CreditDay? = null,
        ) {
            fun withPay(change: (PayDraft) -> PayDraft): Session = payDraft?.let { copy(payDraft = change(it)) } ?: this
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
