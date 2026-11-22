package com.meqlause.fintrack.feature.onboarding

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class OnboardingWalletStepTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fixture = OnboardingFixture()

    // FR-002 AC: the wallet step suggests "Tunai", cash, balance 0
    @Test
    fun `given first wallet step when shown then Tunai cash with zero balance is suggested (FR-002)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                val form = (awaitItem() as OnboardingUiState.FirstWallet).form

                assertThat(form.name).isEqualTo("Tunai")
                assertThat(form.type).isEqualTo(WalletType.CASH)
                assertThat(form.balance).isEqualTo(Money(0L))
            }
        }

    @Test
    fun `given blank wallet name when continued then the name error is shown and the step stays (FR-002)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.NameChanged("  "))
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)

                val form = (awaitItem() as OnboardingUiState.FirstWallet).form
                assertThat(form.nameError).isEqualTo(WalletNameError.BLANK)
            }
        }

    @Test
    fun `given valid wallet name when continued then the notification step is shown (FR-002)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)

                assertThat(awaitItem()).isInstanceOf(OnboardingUiState.NotificationAccess::class)
            }
        }

    // FR-002 AC: the keypad types the initial balance, "000" appends three zeros
    @Test
    fun `given keypad digits when typed then the initial balance follows them (FR-002)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.DigitTyped("15"))
                awaitItem()
                viewModel.onAction(OnboardingAction.DigitTyped("000"))

                val form = (awaitItem() as OnboardingUiState.FirstWallet).form
                assertThat(form.balance).isEqualTo(Money(15_000L))
            }
        }

    @Test
    fun `given keypad backspace when tapped then the last digit of the balance is removed (FR-002)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.DigitTyped("42"))
                awaitItem()
                viewModel.onAction(OnboardingAction.BackspaceTyped)

                val form = (awaitItem() as OnboardingUiState.FirstWallet).form
                assertThat(form.balance).isEqualTo(Money(4L))
            }
        }

    // FR-002 AC: the first wallet is created with the name, type and balance entered on the wallet step
    @Test
    fun `given wallet step filled when finished then the wallet is stored with the entered values (FR-002)`() =
        runTest {
            val viewModel = fixture.viewModel()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(OnboardingAction.SkipIntro)
                awaitItem()
                viewModel.onAction(OnboardingAction.NameChanged("Dompet Bank"))
                awaitItem()
                viewModel.onAction(OnboardingAction.TypeSelected(WalletType.BANK))
                awaitItem()
                viewModel.onAction(OnboardingAction.DigitTyped("100000"))
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)
                awaitItem()
                viewModel.onAction(OnboardingAction.Continue)
                awaitItem()
                viewModel.onAction(OnboardingAction.Finish)
                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Security(isSaving = true))
                assertThat(awaitItem()).isEqualTo(OnboardingUiState.Completed)
            }

            val stored =
                fixture.wallets
                    .observeAll()
                    .first()
                    .single()
            assertThat(stored.profile.name).isEqualTo("Dompet Bank")
            assertThat(stored.kind.type).isEqualTo(WalletType.BANK)
            assertThat(stored.terms.initialBalance).isEqualTo(Money(100_000L))
        }

    // FR-004 AC: the default categories are seeded when onboarding finishes
    @Test
    fun `given onboarding finished when completed then the default categories are seeded (FR-004)`() =
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
                awaitItem()
                awaitItem()
            }

            assertThat(fixture.categories.observeAll().first()).hasSize(18)
        }
}
