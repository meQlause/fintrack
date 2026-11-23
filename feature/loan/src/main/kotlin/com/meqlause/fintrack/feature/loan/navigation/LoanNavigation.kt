package com.meqlause.fintrack.feature.loan.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.loan.detail.LoanDetailRoute
import com.meqlause.fintrack.feature.loan.list.LoanListRoute

// FR-150 … FR-158, S-17: registers the loan list and the loan detail; the host decides where they are opened from
public fun NavGraphBuilder.loanDestination(
    onBack: () -> Unit,
    onOpenLoan: (Long) -> Unit,
) {
    composable<LoanListDestination> {
        LoanListRoute(onBack = onBack, onOpenLoan = onOpenLoan)
    }
    composable<LoanDetailDestination> {
        LoanDetailRoute(onBack = onBack)
    }
}
