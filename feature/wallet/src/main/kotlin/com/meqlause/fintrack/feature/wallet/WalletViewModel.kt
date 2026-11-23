package com.meqlause.fintrack.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.usecase.ObserveNetWorthUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
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
import javax.inject.Inject

// FR-010..016, S-09: drives the Dompet tab; the repository is the only source of wallets and balances
@HiltViewModel
internal class WalletViewModel
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val validator: WalletNameValidator,
        private val netWorth: ObserveNetWorthUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val editor = MutableStateFlow<WalletEditor?>(null)
        private val picker = MutableStateFlow<WalletPicker?>(null)
        private val archivedExpanded = MutableStateFlow(false)

        // S-09: Loading until the first list arrives, then Empty or Success with the total and the sections
        val state: StateFlow<WalletUiState> =
            combine(
                combine(wallets.observeAll(), wallets.observeBalances(), netWorth()) { all, balances, worth ->
                    Triple(all, balances, worth)
                },
                editor,
                picker,
                archivedExpanded,
            ) { (all, balances, worth), draft, open, expanded ->
                buildState(all, balances, worth, draft, open, expanded)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), WalletUiState.Loading)

        // S-09: the archived section opens and closes in place
        fun toggleArchivedSection() {
            archivedExpanded.update { !it }
        }

        // FR-010: opens the editor for a new wallet
        fun startCreate() {
            editor.value = WalletEditor.forNew()
        }

        // FR-011: opens the editor for an existing wallet
        fun startEdit(item: WalletItem) {
            editor.value = WalletEditor.of(item)
        }

        // FR-010: closes the editor and any drawer it opened
        fun dismissEditor() {
            editor.value = null
            picker.value = null
        }

        fun changeName(name: String) {
            editor.update { it?.copy(name = name, nameError = null) }
        }

        fun changeInitialBalance(digits: String) {
            editor.update { it?.copy(initialBalance = digitsOf(digits)) }
        }

        fun changeCreditLimit(digits: String) {
            editor.update { it?.copy(creditLimit = digitsOf(digits), limitError = false) }
        }

        fun changeIncludeInTotal(include: Boolean) {
            editor.update { it?.copy(includeInTotal = include) }
        }

        fun openPicker(target: WalletPicker) {
            picker.value = target
        }

        fun dismissPicker() {
            picker.value = null
        }

        // FR-010: the type drawer result; the drawer closes
        fun pickType(type: WalletType) {
            editor.update { it?.copy(type = type) }
            picker.value = null
        }

        // FR-170: the investment kind drawer result; the drawer closes
        fun pickInvestKind(kind: InvestKind) {
            editor.update { it?.copy(investKind = kind) }
            picker.value = null
        }

        // FR-139: the statement or due day drawer result, depending on which drawer was open
        fun pickDay(day: Int) {
            when (picker.value) {
                WalletPicker.STATEMENT_DAY -> editor.update { it?.copy(statementDay = day) }
                WalletPicker.DUE_DAY -> editor.update { it?.copy(dueDay = day) }
                else -> Unit
            }
            picker.value = null
        }

        // FR-010, FR-011, FR-139: saves a new or edited wallet; a rejected name or limit keeps the editor open
        fun save() {
            val draft = editor.value ?: return
            val nameError = validator.validate(draft.name)
            val limitError = draft.isCredit && (draft.creditLimit.toLongOrNull() ?: 0L) <= 0L
            if (nameError != null || limitError) {
                editor.value = draft.copy(nameError = nameError, limitError = limitError)
                return
            }
            viewModelScope.launch {
                val all = wallets.observeAll().first()
                val now = clock.instant()
                val existing = all.firstOrNull { it.walletId == draft.walletId }
                if (existing == null) {
                    wallets.insert(WalletDraftMapper.newWallet(draft, nextSortOrder(all), now))
                } else {
                    wallets.update(WalletDraftMapper.applyTo(existing, draft, now))
                }
                dismissEditor()
            }
        }

        // FR-012: archives or restores the wallet being edited; archived wallets leave the pickers, not history
        fun setArchivedEditing(archived: Boolean) {
            val walletId = editor.value?.walletId ?: return
            viewModelScope.launch {
                val existing = wallets.observeAll().first().firstOrNull { it.walletId == walletId } ?: return@launch
                wallets.update(existing.withArchived(archived, clock.instant()))
                dismissEditor()
            }
        }

        private fun buildState(
            all: List<Wallet>,
            balances: Map<WalletId, Money>,
            worth: NetWorth,
            draft: WalletEditor?,
            open: WalletPicker?,
            expanded: Boolean,
        ): WalletUiState {
            if (all.isEmpty()) return WalletUiState.Empty(editor = draft, picker = open)
            val items = all.map { WalletItem(wallet = it, balance = balances[it.walletId] ?: it.terms.initialBalance) }
            val active = items.filterNot { it.wallet.status.isArchived }
            return WalletUiState.Success(
                netWorth = worth,
                sections = sectionsOf(active),
                archived = items.filter { it.wallet.status.isArchived },
                archivedExpanded = expanded,
                editor = draft,
                picker = open,
            )
        }

        // S-09: the active wallets grouped in display order; empty groups are not shown
        private fun sectionsOf(active: List<WalletItem>): List<WalletSection> =
            WalletGroupKey.entries.mapNotNull { group ->
                val items = active.filter { WalletGroupKey.of(it.wallet.kind.type) == group }
                items.takeIf { it.isNotEmpty() }?.let { WalletSection(group = group, items = it) }
            }

        private fun Wallet.withArchived(
            archived: Boolean,
            now: Instant,
        ): Wallet =
            copy(
                status = status.copy(isArchived = archived),
                audit = audit.copy(updatedAt = now),
            )

        private fun nextSortOrder(all: List<Wallet>): Int = (all.maxOfOrNull { it.profile.sortOrder } ?: -1) + 1

        private fun digitsOf(raw: String): String = raw.filter { it.isDigit() }.take(MAX_DIGITS)

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
            const val MAX_DIGITS = 12
        }
    }
