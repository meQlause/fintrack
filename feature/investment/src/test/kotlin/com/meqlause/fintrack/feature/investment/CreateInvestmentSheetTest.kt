package com.meqlause.fintrack.feature.investment

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.investment.component.CreateInvestmentSheet
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-170: opening "Tambah investasi" must not crash; the drawer already scrolls, so its body must not scroll again
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h844dp")
class CreateInvestmentSheetTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `given the creation sheet when it opens then it renders without nested scrolling (FR-170)`() {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                CreateInvestmentSheet(draft = InvestmentDraft(), callbacks = noopCallbacks())
            }
        }
        composeRule.onNodeWithText("1").assertExists()
    }

    @Test
    fun `given the creation sheet when rendered then it matches the reference (FR-170)`() {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                CreateInvestmentSheet(
                    draft = InvestmentDraft(name = "Bibit", invested = "500000"),
                    callbacks = noopCallbacks(),
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/InvestmentCreate_light.png")
    }

    private fun noopCallbacks(): InvestmentCallbacks =
        InvestmentCallbacks(
            onAdd = {},
            onOpenAccount = {},
            onNameChange = {},
            onOpenKindPicker = {},
            onPickKind = {},
            sheet = AmountSheetCallbacks(onDigit = {}, onBackspace = {}, onSave = {}, onDismiss = {}),
        )
}
