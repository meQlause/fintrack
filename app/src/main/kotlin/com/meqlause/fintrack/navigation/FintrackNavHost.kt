package com.meqlause.fintrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.budget.navigation.BudgetDestination
import com.meqlause.fintrack.feature.budget.navigation.budgetDestination
import com.meqlause.fintrack.feature.category.navigation.CategoryDestination
import com.meqlause.fintrack.feature.category.navigation.categoryDestination
import com.meqlause.fintrack.feature.credit.navigation.CreditDetailDestination
import com.meqlause.fintrack.feature.credit.navigation.creditDetailDestination
import com.meqlause.fintrack.feature.dashboard.navigation.HomeDestination
import com.meqlause.fintrack.feature.dashboard.navigation.homeDestination
import com.meqlause.fintrack.feature.inbox.navigation.InboxDestination
import com.meqlause.fintrack.feature.inbox.navigation.inboxDestination
import com.meqlause.fintrack.feature.onboarding.OnboardingDestination
import com.meqlause.fintrack.feature.onboarding.onboardingGraph
import com.meqlause.fintrack.feature.settings.navigation.MonitoredAppsDestination
import com.meqlause.fintrack.feature.settings.navigation.SettingsDestination
import com.meqlause.fintrack.feature.settings.navigation.monitoredAppsDestination
import com.meqlause.fintrack.feature.settings.navigation.settingsDestination
import com.meqlause.fintrack.feature.transaction.detail.navigation.TransactionDetailDestination
import com.meqlause.fintrack.feature.transaction.detail.navigation.transactionDetailDestination
import com.meqlause.fintrack.feature.transaction.edit.navigation.EditTransactionDestination
import com.meqlause.fintrack.feature.transaction.edit.navigation.editTransactionDestination
import com.meqlause.fintrack.feature.transaction.history.navigation.historyDestination
import com.meqlause.fintrack.feature.wallet.navigation.WalletDetailDestination
import com.meqlause.fintrack.feature.wallet.navigation.walletDestination
import com.meqlause.fintrack.feature.wallet.navigation.walletDetailDestination
import com.meqlause.fintrack.tab.PlaceholderTabScreen

// DOC-03: app owns the NavHost; onboarding (FR-001) starts when it is needed, otherwise Beranda is the start
@Composable
internal fun FintrackNavHost(
    navController: NavHostController,
    onboardingNeeded: Boolean,
    modifier: Modifier = Modifier,
) {
    if (onboardingNeeded) {
        NavHost(navController = navController, startDestination = OnboardingDestination, modifier = modifier) {
            fintrackGraph(navController)
        }
    } else {
        NavHost(navController = navController, startDestination = HomeDestination, modifier = modifier) {
            fintrackGraph(navController)
        }
    }
}

// DOC-03: the routes of the app; onboarding leads to Beranda and its PIN offer has no screen until feature:lock
private fun NavGraphBuilder.fintrackGraph(navController: NavHostController) {
    onboardingGraph(
        onFinished = {
            navController.navigate(HomeDestination) {
                popUpTo<OnboardingDestination> { inclusive = true }
            }
        },
        // FR-003: the PIN setup screen arrives with feature:lock (T8.1); until then the offer does nothing
        onSetupPin = {},
    )
    // DOC-09, FR-040..043: Beranda (S-03); Catat cepat opens the add sheet, a row opens its S-06 detail
    homeDestination(
        onOpenSettings = { navController.navigate(SettingsDestination) },
        onOpenInbox = { navController.navigate(InboxDestination) },
        onOpenWallet = { navController.navigate(WalletDetailDestination(it.value)) },
        onOpenWallets = { MainTab.WALLET.navigateFrom(navController) },
        onOpenHistory = { MainTab.HISTORY.navigateFrom(navController) },
        onQuickEntry = { navController.navigate(EditTransactionDestination()) },
        onApplyShortcut = { navController.navigate(EditTransactionDestination(quickEntryId = it.value)) },
        onOpenTransaction = { navController.navigate(TransactionDetailDestination(transactionId = it.value)) },
    )
    // DOC-09, FR-036: Riwayat (S-05); a tapped row opens the S-06 detail for that transaction
    historyDestination(onOpenTransaction = { id ->
        navController.navigate(TransactionDetailDestination(transactionId = id))
    })
    composable<InvestmentRoute> { PlaceholderTabScreen(mainTab = MainTab.INVESTMENT) }
    walletDestination(onOpenDetail = { id, type ->
        if (type.isCredit()) {
            navController.navigate(CreditDetailDestination(id.value))
        } else {
            navController.navigate(WalletDetailDestination(id.value))
        }
    })
    // FR-017, S-19: Pakai and Transfer open the add sheet (S-04); preselecting the wallet/type is a later step
    walletDetailDestination(
        onBack = { navController.popBackStack() },
        onUse = { navController.navigate(EditTransactionDestination()) },
        onTransfer = { navController.navigate(EditTransactionDestination()) },
    )
    // FR-131, S-14: a card or paylater wallet opens its credit detail; Bayar tagihan is the S-15 sheet inside it
    creditDetailDestination(onBack = { navController.popBackStack() })
    // DOC-09, S-12: Pengaturan is opened from the Beranda header; its Kategori row leads to S-10 (FR-020)
    settingsDestination(
        onBack = { navController.popBackStack() },
        onOpenCategories = { navController.navigate(CategoryDestination) },
        onOpenBudgets = { navController.navigate(BudgetDestination) },
        onOpenMonitoredApps = { navController.navigate(MonitoredAppsDestination) },
    )
    // FR-070 … FR-074, S-11: Anggaran is opened from the Data group of Pengaturan
    budgetDestination(onBack = { navController.popBackStack() })
    // FR-081, FR-082, FR-090: Aplikasi dipantau (S-13) is opened from the Deteksi otomatis group of Pengaturan
    monitoredAppsDestination(onBack = { navController.popBackStack() })
    categoryDestination(onBack = { navController.popBackStack() })
    // FR-084 … FR-091, S-07: Kotak Masuk is opened from the bell in the Beranda header
    inboxDestination(onBack = { navController.popBackStack() })
    // FR-034, S-06: the detail leaves on delete or duplicate; Ubah opens the S-04 sheet above it
    transactionDetailDestination(
        onBack = { navController.popBackStack() },
        onEdit = { id -> navController.navigate(EditTransactionDestination(transactionId = id)) },
    )
    // DOC-09, S-04: the add or edit sheet returns to the screen it was opened from
    editTransactionDestination(onDismiss = { navController.popBackStack() })
}

// BR-031: card and paylater wallets are the credit wallets and open S-14 instead of S-19
private fun WalletType.isCredit(): Boolean = this == WalletType.CREDIT_CARD || this == WalletType.PAYLATER
