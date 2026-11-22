package com.meqlause.fintrack.feature.category

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.meqlause.fintrack.core.model.CategoryKind
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-020, S-10: the main interaction of the screen, adding a category from the empty state
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class CategoryScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenEmptyStateWhenAddTappedThenAddCallbackFires() {
        var added = false
        composeRule.setContent {
            CategoryScreen(
                state = CategoryUiState.Empty(kind = CategoryKind.EXPENSE, editor = null),
                callbacks = CategoryCallbacks(onAdd = { added = true }),
            )
        }

        val label = ApplicationProvider.getApplicationContext<Context>().getString(R.string.category_add)
        composeRule.onNodeWithText(label).performClick()

        assertTrue(added)
    }
}
