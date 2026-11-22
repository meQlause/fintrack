package com.meqlause.fintrack.feature.onboarding

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class OnboardingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = OnboardingFixture()

    // FR-001 AC: onboarding opens on the welcome page
    @Test
    fun `given onboarding opened when first shown then the welcome page is shown (FR-001)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Welcome)
            }
        }

    // FR-001 AC: at most four pages, in the order welcome, wallet, notification, security
    @Test
    fun `given onboarding when continued page by page then four pages are shown in order (FR-001)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Welcome)
                viewModel.onAction(OnboardingAction.Continue)
                assertThat(awaitItem()).isInstanceOf(OnboardingUiState.FirstWallet::class)
                viewModel.onAction(OnboardingAction.Continue)
                assertThat(awaitItem()).isInstanceOf(OnboardingUiState.NotificationAccess::class)
                viewModel.onAction(OnboardingAction.Continue)
                assertThat(awaitItem()).isInstanceOf(OnboardingUiState.Security::class)
            }
        }

    @Test
    fun `given welcome page when skip tapped then the required wallet step is shown (FR-001)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                assertThat(awaitItem()).isInstanceOf(OnboardingUiState.FirstWallet::class)
            }
        }

    // FR-003 AC: the notification step reflects the access state checked when the user returns from settings
    @Test
    fun `given notification access not granted when checked then the step shows it as not active (FR-003)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)

                assertThat(awaitItem()).isEqualTo(OnboardingUiState.NotificationAccess(isGranted = false))
            }
        }

    @Test
    fun `given notification access granted when returned from settings then the step shows it granted (FR-003)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)
                awaitItem()
                viewModel.onNotificationAccessChecked(isGranted = true)

                assertThat(awaitItem()).isEqualTo(OnboardingUiState.NotificationAccess(isGranted = true))
            }
        }

    @Test
    fun `given notification access skipped when continued then the PIN step is shown (FR-003)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)

                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Security(isSaving = false))
            }
        }

    // FR-003 AC: skipping notification access and PIN still completes onboarding
    @Test
    fun `given notification and PIN skipped when finished then onboarding completes (FR-003)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)
                awaitItem()
                viewModel.onAction(OnboardingAction.Finish)

                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Security(isSaving = true))
                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Completed)
            }
        }

    // FR-001 AC: after onboarding the Dashboard is shown, which the route does on the completed state
    @Test
    fun `given onboarding finished when completed then the completed state is emitted for navigation (FR-001)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)
                awaitItem()
                viewModel.onAction(OnboardingAction.Finish)

                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Security(isSaving = true))
                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Completed)
            }
        }
}
