package com.meqlause.fintrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.budget.navigation.BudgetDestination
import com.meqlause.fintrack.feature.budget.navigation.BudgetNavigation
import com.meqlause.fintrack.feature.category.navigation.CategoryDestination
import com.meqlause.fintrack.feature.category.navigation.CategoryNavigation
import com.meqlause.fintrack.feature.credit.navigation.CreditDetailDestination
import com.meqlause.fintrack.feature.credit.navigation.CreditNavigation
import com.meqlause.fintrack.feature.credit.navigation.InstalmentDetailDestination
import com.meqlause.fintrack.feature.credit.navigation.InstalmentListDestination
import com.meqlause.fintrack.feature.dashboard.HomeEntryLinks
import com.meqlause.fintrack.feature.dashboard.HomeHostLinks
import com.meqlause.fintrack.feature.dashboard.navigation.HomeDestination
import com.meqlause.fintrack.feature.dashboard.navigation.HomeNavigation
import com.meqlause.fintrack.feature.inbox.navigation.InboxDestination
import com.meqlause.fintrack.feature.inbox.navigation.InboxNavigation
import com.meqlause.fintrack.feature.investment.navigation.InvestmentDetailDestination
import com.meqlause.fintrack.feature.investment.navigation.InvestmentDetailNavigation
import com.meqlause.fintrack.feature.investment.navigation.InvestmentNavigation
import com.meqlause.fintrack.feature.loan.navigation.LoanDetailDestination
import com.meqlause.fintrack.feature.loan.navigation.LoanListDestination
import com.meqlause.fintrack.feature.loan.navigation.LoanNavigation
import com.meqlause.fintrack.feature.lock.navigation.PinSetupDestination
import com.meqlause.fintrack.feature.lock.navigation.PinSetupMode
import com.meqlause.fintrack.feature.lock.navigation.PinSetupNavigation
import com.meqlause.fintrack.feature.onboarding.OnboardingDestination
import com.meqlause.fintrack.feature.onboarding.OnboardingGraph
import com.meqlause.fintrack.feature.settings.SettingsLinks
import com.meqlause.fintrack.feature.settings.SettingsSecurityCallbacks
import com.meqlause.fintrack.feature.settings.navigation.AboutDestination
import com.meqlause.fintrack.feature.settings.navigation.AboutNavigation
import com.meqlause.fintrack.feature.settings.navigation.BatteryHelpDestination
import com.meqlause.fintrack.feature.settings.navigation.BatteryHelpNavigation
import com.meqlause.fintrack.feature.settings.navigation.MonitoredAppsDestination
import com.meqlause.fintrack.feature.settings.navigation.MonitoredAppsNavigation
import com.meqlause.fintrack.feature.settings.navigation.SettingsDestination
import com.meqlause.fintrack.feature.settings.navigation.SettingsNavigation
import com.meqlause.fintrack.feature.transaction.detail.navigation.TransactionDetailDestination
import com.meqlause.fintrack.feature.transaction.detail.navigation.TransactionDetailNavigation
import com.meqlause.fintrack.feature.transaction.edit.navigation.EditTransactionDestination
import com.meqlause.fintrack.feature.transaction.edit.navigation.EditTransactionNavigation
import com.meqlause.fintrack.feature.transaction.history.navigation.HistoryNavigation
import com.meqlause.fintrack.feature.transaction.shortcut.navigation.ShortcutFormDestination
import com.meqlause.fintrack.feature.transaction.shortcut.navigation.ShortcutFormNavigation
import com.meqlause.fintrack.feature.wallet.navigation.WalletDetailDestination
import com.meqlause.fintrack.feature.wallet.navigation.WalletDetailNavigation
import com.meqlause.fintrack.feature.wallet.navigation.WalletNavigation

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

// DOC-03: the routes of the app; onboarding leads to Beranda and its PIN offer opens the PIN setup
private fun NavGraphBuilder.fintrackGraph(navController: NavHostController) {
    startGraph(navController)
    walletGraph(navController)
    creditAndLoanGraph(navController)
    settingsGraph(navController)
    utilityGraph(navController)
    transactionGraph(navController)
}

private fun NavGraphBuilder.startGraph(navController: NavHostController) {
    onboardingGraph(navController)
    homeGraph(navController)
}

private fun NavGraphBuilder.onboardingGraph(navController: NavHostController) {
    OnboardingGraph.register(
        builder = this,
        onFinished = {
            navController.navigate(HomeDestination) {
                popUpTo<OnboardingDestination> { inclusive = true }
            }
        },
        // FR-003: the optional PIN offer opens the PIN setup; leaving it returns to the security page
        onSetupPin = { navController.navigate(PinSetupDestination(PinSetupMode.SET)) },
    )
    // FR-100, FR-103, S-02: PIN set, change and remove screen; leaving it returns to the screen it came from
    PinSetupNavigation.register(
        builder = this,
        onDone = { navController.popBackStack() },
        onCancel = { navController.popBackStack() },
    )
}

// DOC-09, FR-040..043: Beranda (S-03); Catat cepat opens the shortcut form, a row opens its S-06 detail
private fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    HomeNavigation.register(
        builder = this,
        host =
            HomeHostLinks(
                onOpenSettings = { navController.navigate(SettingsDestination) },
                onOpenInbox = { navController.navigate(InboxDestination) },
                onOpenWallet = { navController.navigate(WalletDetailDestination(it.value)) },
                onOpenCreditWallet = { navController.navigate(CreditDetailDestination(it.value)) },
                onOpenWallets = { MainTab.WALLET.navigateFrom(navController) },
                onOpenTransaction = { navController.navigate(TransactionDetailDestination(transactionId = it.value)) },
            ),
        entry =
            HomeEntryLinks(
                onOpenHistory = { MainTab.HISTORY.navigateFrom(navController) },
                onOpenInstalments = { navController.navigate(InstalmentListDestination) },
                onOpenLoans = { navController.navigate(LoanListDestination) },
                onNewShortcut = { navController.navigate(ShortcutFormDestination()) },
                onEditShortcut = { navController.navigate(ShortcutFormDestination(quickEntryId = it.value)) },
                onApplyShortcut = { navController.navigate(EditTransactionDestination(quickEntryId = it.value)) },
            ),
    )
}

private fun NavGraphBuilder.transactionGraph(navController: NavHostController) {
    // DOC-09, FR-036: Riwayat (S-05); a tapped row opens the S-06 detail for that transaction
    HistoryNavigation.register(
        builder = this,
        onOpenTransaction = { id -> navController.navigate(TransactionDetailDestination(transactionId = id)) },
        onAdd = { navController.navigate(EditTransactionDestination()) },
    )
    // FR-034, S-06: the detail leaves on delete or duplicate; Ubah opens the S-04 sheet above it
    TransactionDetailNavigation.register(
        builder = this,
        onBack = { navController.popBackStack() },
        onEdit = { id -> navController.navigate(EditTransactionDestination(transactionId = id)) },
        onShortcuts = { navController.navigate(EditTransactionDestination()) },
    )
    // DOC-09, S-04: the add or edit sheet returns to the screen it was opened from
    EditTransactionNavigation.register(builder = this, onDismiss = { navController.popBackStack() })
    // FR-039: the "Pintasan baru" form saves a shortcut and returns to the screen it was opened from
    ShortcutFormNavigation.register(builder = this, onDismiss = { navController.popBackStack() })
}

private fun NavGraphBuilder.walletGraph(navController: NavHostController) {
    // DOC-09, FR-173: Investasi tab (S-08); an account opens S-20, which leaves to the add sheet for transfers
    InvestmentNavigation.register(
        builder = this,
        onOpenAccount = { navController.navigate(InvestmentDetailDestination(it.value)) },
    )
    InvestmentDetailNavigation.register(
        builder = this,
        onBack = { navController.popBackStack() },
        onTransfer = { navController.navigate(EditTransactionDestination()) },
    )
    WalletNavigation.register(
        builder = this,
        onOpenDetail = { id, type ->
            if (type.isCredit()) {
                navController.navigate(CreditDetailDestination(id.value))
            } else {
                navController.navigate(WalletDetailDestination(id.value))
            }
        },
        // FR-150, S-09: the Pinjaman row opens the loan list S-17
        onOpenLoans = { navController.navigate(LoanListDestination) },
    )
    // FR-017, S-19: Pakai and Transfer open the add sheet (S-04); preselecting the wallet/type is a later step
    WalletDetailNavigation.register(
        builder = this,
        onBack = { navController.popBackStack() },
        onUse = { navController.navigate(EditTransactionDestination()) },
        onTransfer = { navController.navigate(EditTransactionDestination()) },
    )
}

private fun NavGraphBuilder.creditAndLoanGraph(navController: NavHostController) {
    // FR-131, S-14: a card or paylater wallet opens its credit detail; Bayar tagihan is the S-15 sheet inside it
    CreditNavigation.register(
        builder = this,
        onBack = { navController.popBackStack() },
        onOpenInstalment = { planId -> navController.navigate(InstalmentDetailDestination(planId)) },
        onOpenInstalments = { navController.navigate(InstalmentListDestination) },
    )
    // FR-150 to FR-158, S-17: the Pinjaman list and the loan detail with repayments and write-off
    LoanNavigation.register(
        builder = this,
        onBack = { navController.popBackStack() },
        onOpenLoan = { loanId -> navController.navigate(LoanDetailDestination(loanId)) },
    )
}

private fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    // DOC-09, S-12: Pengaturan is opened from the Beranda header; its Kategori row leads to S-10 (FR-020)
    SettingsNavigation.register(
        builder = this,
        links =
            SettingsLinks(
                onBack = { navController.popBackStack() },
                onOpenCategories = { navController.navigate(CategoryDestination) },
                onOpenBudgets = { navController.navigate(BudgetDestination) },
                onOpenMonitoredApps = { navController.navigate(MonitoredAppsDestination) },
                // FR-123, S-12: the Tentang row opens the About and privacy screen
                onOpenAbout = { navController.navigate(AboutDestination) },
                // DOC-07, S-12: the Bantuan optimasi baterai row opens the OEM help page
                onOpenBatteryHelp = { navController.navigate(BatteryHelpDestination) },
            ),
        // FR-100, S-12: Keamanan rows open the PIN setup in the mode they need
        pins =
            SettingsSecurityCallbacks(
                onOpenPinSetup = { navController.navigate(PinSetupDestination(PinSetupMode.SET)) },
                onOpenPinChange = { navController.navigate(PinSetupDestination(PinSetupMode.CHANGE)) },
                onOpenPinRemove = { navController.navigate(PinSetupDestination(PinSetupMode.REMOVE)) },
            ),
    )
    // FR-070 to FR-074, S-11: Anggaran is opened from the Data group of Pengaturan
    BudgetNavigation.register(builder = this, onBack = { navController.popBackStack() })
}

private fun NavGraphBuilder.utilityGraph(navController: NavHostController) {
    // FR-081, FR-082, FR-090: Aplikasi dipantau (S-13) is opened from the Deteksi otomatis group of Pengaturan
    MonitoredAppsNavigation.register(builder = this, onBack = { navController.popBackStack() })
    // FR-123, S-12: Tentang is opened from the Tentang group of Pengaturan
    AboutNavigation.register(builder = this, onBack = { navController.popBackStack() })
    // DOC-07, S-12: the battery-optimisation help page is opened from the Deteksi otomatis group of Pengaturan
    BatteryHelpNavigation.register(builder = this, onBack = { navController.popBackStack() })
    CategoryNavigation.register(builder = this, onBack = { navController.popBackStack() })
    // FR-084 to FR-091, S-07: Kotak Masuk is opened from the bell in the Beranda header
    InboxNavigation.register(builder = this, onBack = { navController.popBackStack() })
}

// BR-031: card and paylater wallets are the credit wallets and open S-14 instead of S-19
private fun WalletType.isCredit(): Boolean = this == WalletType.CREDIT_CARD || this == WalletType.PAYLATER
