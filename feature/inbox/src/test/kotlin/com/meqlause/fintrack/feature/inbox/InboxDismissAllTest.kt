package com.meqlause.fintrack.feature.inbox

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// S-07: Abaikan semua is offered only while the inbox has items waiting
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h844dp")
class InboxDismissAllTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val label = "Abaikan semua"

    @Test
    fun `given an empty inbox when rendered then dismiss all is hidden (S-07)`() {
        composeRule.setContent { InboxScreen(state = InboxUiState(isLoaded = true), actions = InboxActions()) }
        composeRule.onNodeWithText(label).assertDoesNotExist()
    }

    @Test
    fun `given a pending item when rendered then dismiss all is shown (S-07)`() {
        val card =
            InboxCardUi(
                itemId = 1L,
                text = InboxCardText(sourceLabel = "GoPay", timeLabel = "07.05", merchant = "Gojek"),
                amount = Money(25_000L),
                type = TransactionType.EXPENSE,
                wallet = InboxSlot(slotId = null, name = null),
                category = InboxSlot(slotId = null, name = null),
            )
        val state = InboxUiState(isLoaded = true, pending = InboxPending(cards = listOf(card)))
        composeRule.setContent { InboxScreen(state = state, actions = InboxActions()) }
        composeRule.onNodeWithText(label).assertIsDisplayed()
    }
}
