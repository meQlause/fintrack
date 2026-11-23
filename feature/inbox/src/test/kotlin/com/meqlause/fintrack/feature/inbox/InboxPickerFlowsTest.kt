package com.meqlause.fintrack.feature.inbox

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.ui.picker.PickerOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test

// FR-087, FR-091, S-18: the pickers of the Kotak Masuk
@OptIn(ExperimentalCoroutinesApi::class)
class InboxPickerFlowsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fixtures = InboxTestFixtures()

    /** S-18: the picker offers income or expense categories by the card type and applies the choice. */
    @Test
    fun `given the pickers when options and selections are asked then they follow the card (S-18)`() =
        runBlocking {
            val bank = fixtures.wallet("BCA")
            val cash = fixtures.wallet("Tunai")
            val food = fixtures.category("Makan", CategoryKind.EXPENSE, order = 1)
            val salary = fixtures.category("Gaji", CategoryKind.INCOME, order = 0)
            val debit =
                fixtures.inbox
                    .insert(
                        fixtures.item(MoneyDirection.DEBIT, suggestion = Suggestion(bank, food, null)),
                    ).value
            val credit =
                fixtures.inbox
                    .insert(
                        fixtures.item(MoneyDirection.CREDIT, suggestion = Suggestion(cash, salary, null)),
                    ).value
            val state = fixtures.viewModel().state.first { it.isLoaded }
            assertThat(
                optionsFor(InboxPicker(debit, PickKind.CATEGORY), state).map { it.label },
            ).isEqualTo(listOf("Makan"))
            assertThat(
                optionsFor(InboxPicker(credit, PickKind.CATEGORY), state).map { it.label },
            ).isEqualTo(listOf("Gaji"))
            assertThat(optionsFor(InboxPicker(debit, PickKind.WALLET), state)).hasSize(2)
            assertThat(optionsFor(InboxPicker(debit, PickKind.TARGET_WALLET), state)).hasSize(2)
            assertThat(selectedKey(InboxPicker(debit, PickKind.CATEGORY), state)).isEqualTo(food.value.toString())
            assertThat(selectedKey(InboxPicker(debit, PickKind.WALLET), state)).isEqualTo(bank.value.toString())
            assertThat(selectedKey(InboxPicker(999L, PickKind.TARGET_WALLET), state)).isNull()
        }

    /** S-18: a picked option goes to the wallet or the category action of the item. */
    @Test
    fun `given a picked option when applied then the matching action receives it (S-18)`() =
        runBlocking {
            val debit = 5L
            var chosen = ""
            val actions =
                InboxItemActions(
                    onChooseWallet = { item, option -> chosen += "w$item:$option " },
                    onChooseCategory = { item, option -> chosen += "c$item:$option " },
                )
            onPicked(InboxPicker(debit, PickKind.CATEGORY), PickerOption("7", "x"), actions)
            onPicked(InboxPicker(debit, PickKind.WALLET), PickerOption("8", "y"), actions)
            onPicked(InboxPicker(debit, PickKind.TARGET_WALLET), PickerOption("9", "z"), actions)
            assertThat(chosen).isEqualTo("c$debit:7 w$debit:8 w$debit:9 ")
        }

    /** FR-091: edits on either side of a pair change the wallet it shows; the pair picker reads its sides. */
    @Test
    fun `given a pair when its sides are edited then the pair shows them and the picker reads them (FR-091)`() =
        runBlocking {
            val bank = fixtures.wallet("BCA")
            val cash = fixtures.wallet("Tunai")
            val other = fixtures.wallet("Dana")
            val (debit, credit) = fixtures.pairOf(bank, cash)
            val model = fixtures.viewModel()
            model.state.first { it.isLoaded }
            model.chooseWallet(debit, other.value)
            model.chooseWallet(credit, bank.value)
            val pair =
                model.state
                    .first {
                        it.pending.pairs
                            .firstOrNull()
                            ?.from
                            ?.name == "Dana"
                    }.pending.pairs
                    .single()
            assertThat(pair.target.name).isEqualTo("BCA")
            val state = model.state.first { it.isLoaded }
            assertThat(selectedKey(InboxPicker(debit, PickKind.WALLET), state)).isEqualTo(other.value.toString())
            assertThat(selectedKey(InboxPicker(credit, PickKind.TARGET_WALLET), state)).isEqualTo(bank.value.toString())
            assertThat(selectedKey(InboxPicker(debit, PickKind.CATEGORY), state)).isNull()
        }

    /** FR-087: a card without suggestions has no wallet or category selected in the pickers. */
    @Test
    fun `given a bare card when the pickers read it then nothing is selected (FR-087)`() =
        runBlocking {
            val bare = fixtures.inbox.insert(fixtures.item(MoneyDirection.DEBIT)).value
            val state = fixtures.viewModel().state.first { it.isLoaded }
            assertThat(selectedKey(InboxPicker(bare, PickKind.WALLET), state)).isNull()
            assertThat(selectedKey(InboxPicker(bare, PickKind.CATEGORY), state)).isNull()
            assertThat(
                state.pending.cards
                    .single()
                    .wallet.name,
            ).isNull()
        }
}
