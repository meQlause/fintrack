package com.meqlause.fintrack.core.domain.onboarding

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Settings
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** FR-001, FR-002: in-memory wallets; core:testing cannot be used from this pure JVM module (it depends on it). */
internal class InMemoryWalletRepository : WalletRepository {
    private val rows = MutableStateFlow<List<Wallet>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<Wallet>> = rows

    override fun observeBalances(): Flow<Map<WalletId, Money>> =
        rows.map { list -> list.associate { it.walletId to it.terms.initialBalance } }

    override suspend fun insert(wallet: Wallet): WalletId {
        lastId += 1
        rows.update { it + wallet.copy(walletId = WalletId(lastId)) }
        return WalletId(lastId)
    }

    override suspend fun delete(wallet: Wallet) {
        rows.update { list -> list.filterNot { it.walletId == wallet.walletId } }
    }
}

/** FR-001: in-memory settings starting at the given values (DOC-02 defaults unless stated). */
internal class InMemorySettingsRepository(
    initial: Settings = Settings(),
) : SettingsRepository {
    private val current = MutableStateFlow(initial)

    override fun observe(): Flow<Settings> = current

    override suspend fun update(transform: (Settings) -> Settings) {
        current.update(transform)
    }
}
