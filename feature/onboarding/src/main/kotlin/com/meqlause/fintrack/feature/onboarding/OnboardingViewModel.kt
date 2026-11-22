package com.meqlause.fintrack.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.onboarding.CompleteOnboardingResult
import com.meqlause.fintrack.core.domain.usecase.CompleteOnboardingUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * S-01, FR-001..FR-004: drives the onboarding pages. Permission checks and the PIN flow stay outside
 * this class; the route reports the notification access state through [onNotificationAccessChecked].
 */
@HiltViewModel
internal class OnboardingViewModel
    @Inject
    constructor(
        private val completeOnboarding: CompleteOnboardingUseCase,
        private val walletNameValidator: WalletNameValidator,
    ) : ViewModel() {
        private val page = MutableStateFlow(OnboardingPage.WELCOME)
        private val form = MutableStateFlow(FirstWalletForm.initial())
        private val notificationGranted = MutableStateFlow(false)
        private val isSaving = MutableStateFlow(false)
        private val isCompleted = MutableStateFlow(false)

        /** S-01: the state the screen renders, built from the page, the wallet form and the saving state. */
        val uiState: StateFlow<OnboardingUiState> =
            combine(page, form, notificationGranted, isSaving, isCompleted) { pageNow, formNow, grant, busy, done ->
                toUiState(pageNow, formNow, grant, busy, done)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), OnboardingUiState.Welcome)

        /** S-01, FR-001..FR-004: applies a user action from the screen. */
        fun onAction(action: OnboardingAction) {
            when (action) {
                OnboardingAction.SkipIntro -> page.value = OnboardingPage.WALLET
                OnboardingAction.Continue -> continueToNextPage()
                OnboardingAction.Finish -> finish()
                OnboardingAction.BackspaceTyped -> form.update { it.withBackspace() }
                is OnboardingAction.NameChanged -> form.update { it.copy(name = action.name, nameError = null) }
                is OnboardingAction.TypeSelected -> form.update { it.copy(type = action.type) }
                is OnboardingAction.DigitTyped -> form.update { it.withDigits(action.digits) }
            }
        }

        /** FR-003: the route reports the access state each time the screen resumes, e.g. after the system settings. */
        fun onNotificationAccessChecked(isGranted: Boolean) {
            notificationGranted.value = isGranted
        }

        private fun continueToNextPage() {
            when (page.value) {
                OnboardingPage.WELCOME -> page.value = OnboardingPage.WALLET
                OnboardingPage.WALLET -> continueFromWallet()
                OnboardingPage.NOTIFICATION -> page.value = OnboardingPage.SECURITY
                OnboardingPage.SECURITY -> finish()
            }
        }

        // FR-002: the wallet is required, so the step only advances with a valid name
        private fun continueFromWallet() {
            val nameError = walletNameValidator.validate(form.value.name)
            form.update { it.copy(nameError = nameError) }
            if (nameError == null) page.value = OnboardingPage.NOTIFICATION
        }

        // FR-001, FR-004: the store runs once; a rejected name sends the user back to the wallet page
        private fun finish() {
            if (isSaving.value) return
            isSaving.value = true
            viewModelScope.launch {
                when (val result = completeOnboarding(form.value.toDraft())) {
                    CompleteOnboardingResult.Completed -> isCompleted.value = true
                    is CompleteOnboardingResult.NameRejected -> rejectName(result.reason)
                }
            }
        }

        private fun rejectName(reason: WalletNameError) {
            isSaving.value = false
            form.update { it.copy(nameError = reason) }
            page.value = OnboardingPage.WALLET
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L

            fun toUiState(
                currentPage: OnboardingPage,
                currentForm: FirstWalletForm,
                granted: Boolean,
                saving: Boolean,
                done: Boolean,
            ): OnboardingUiState =
                if (done) {
                    OnboardingUiState.Completed
                } else {
                    when (currentPage) {
                        OnboardingPage.WELCOME -> OnboardingUiState.Welcome
                        OnboardingPage.WALLET -> OnboardingUiState.FirstWallet(currentForm)
                        OnboardingPage.NOTIFICATION -> OnboardingUiState.NotificationAccess(granted)
                        OnboardingPage.SECURITY -> OnboardingUiState.Security(isSaving = saving)
                    }
                }
        }
    }
