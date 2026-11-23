package com.meqlause.fintrack.feature.transaction.history

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import org.junit.Test
import java.time.YearMonth

// FR-036, FR-037, FR-064, S-05, S-08: every event of the Riwayat reduced onto the selection
class HistoryReducerTest {
    private val november = YearMonth.parse("2026-11")
    private val start = HistorySelection(month = november)

    /** FR-036: the month is picked or stepped, and the picker year follows its shifts. */
    @Test
    fun `given month events when reduced then the month and the picker year move (FR-036)`() {
        val picked =
            start
                .reduce(
                    HistoryEvent.SheetOpened(HistorySheet.MONTH),
                ).reduce(HistoryEvent.MonthPicked(YearMonth.parse("2026-03")))
        assertThat(picked.month).isEqualTo(YearMonth.parse("2026-03"))
        assertThat(picked.sheet).isNull()
        assertThat(start.reduce(HistoryEvent.PreviousMonth).month).isEqualTo(YearMonth.parse("2026-10"))
        assertThat(start.reduce(HistoryEvent.NextMonth).month).isEqualTo(YearMonth.parse("2026-12"))
        assertThat(start.reduce(HistoryEvent.YearShifted(-1)).pickerYear).isEqualTo(2025)
    }

    /** FR-037: each filter event sets its own field and closes a sheet that picked it. */
    @Test
    fun `given filter events when reduced then each sets its own field (FR-037)`() {
        val open = start.reduce(HistoryEvent.SheetOpened(HistorySheet.WALLET))
        assertThat(open.sheet).isEqualTo(HistorySheet.WALLET)
        assertThat(
            start.reduce(HistoryEvent.TypePicked(HistoryTypeFilter.INCOME)).filter.type,
        ).isEqualTo(HistoryTypeFilter.INCOME)
        val wallet = open.reduce(HistoryEvent.WalletPicked(WalletId(3L)))
        assertThat(wallet.filter.walletId).isEqualTo(WalletId(3L))
        assertThat(wallet.sheet).isNull()
        val category = open.reduce(HistoryEvent.CategoryPicked(CategoryId(4L)))
        assertThat(category.filter.categoryId).isEqualTo(CategoryId(4L))
        val dayPick = start.reduce(HistoryEvent.DayPicked(18))
        assertThat(dayPick.filter.dayOfMonth).isEqualTo(18)
        assertThat(dayPick.reduce(HistoryEvent.DayPicked(18)).filter.dayOfMonth).isNull()
        assertThat(start.reduce(HistoryEvent.QueryChanged("kopi")).filter.query).isEqualTo("kopi")
    }

    /** S-05: the search field opens and closing it clears its text; sheets open on the month's year and close. */
    @Test
    fun `given search and sheet events when reduced then the field and the sheets follow (S-05)`() {
        val searching = start.reduce(HistoryEvent.SearchToggled).reduce(HistoryEvent.QueryChanged("kopi"))
        assertThat(searching.isSearchOpen).isTrue()
        val closed = searching.reduce(HistoryEvent.SearchToggled)
        assertThat(closed.isSearchOpen).isFalse()
        assertThat(closed.filter.query).isEqualTo("")
        val sheet = start.reduce(HistoryEvent.SheetOpened(HistorySheet.DAY))
        assertThat(sheet.sheet).isEqualTo(HistorySheet.DAY)
        assertThat(sheet.pickerYear).isEqualTo(2026)
        assertThat(sheet.reduce(HistoryEvent.SheetDismissed).sheet).isNull()
    }

    /** S-08: the view, the statistics type, the category list and the drill down. */
    @Test
    fun `given statistics events when reduced then the view and the drill down follow (S-08)`() {
        val stats = start.reduce(HistoryEvent.ViewPicked(HistoryView.STATISTICS))
        assertThat(stats.view).isEqualTo(HistoryView.STATISTICS)
        assertThat(stats.reduce(HistoryEvent.StatisticsTypePicked(TransactionType.INCOME)).statisticsType)
            .isEqualTo(TransactionType.INCOME)
        assertThat(stats.reduce(HistoryEvent.CategoriesToggled).isCategoriesExpanded).isTrue()
        val drilled =
            stats
                .reduce(
                    HistoryEvent.SearchToggled,
                ).reduce(HistoryEvent.CategoryDrilled(CategoryId(9L), HistoryTypeFilter.EXPENSE))
        assertThat(drilled.view).isEqualTo(HistoryView.LIST)
        assertThat(drilled.isSearchOpen).isFalse()
        assertThat(drilled.filter).isEqualTo(HistoryFilter(HistoryTypeFilter.EXPENSE, categoryId = CategoryId(9L)))
    }

    /** FR-037: the filter flags follow the fields that narrow the list. */
    @Test
    fun `given filters when asked then the flags follow the narrowed fields (FR-037)`() {
        assertThat(HistoryFilter().isActive).isFalse()
        assertThat(HistoryFilter().hasEntryFilter).isFalse()
        assertThat(HistoryFilter(type = HistoryTypeFilter.TRANSFER).isActive).isTrue()
        assertThat(HistoryFilter(walletId = WalletId(1L)).hasEntryFilter).isTrue()
        assertThat(HistoryFilter(categoryId = CategoryId(1L)).isActive).isTrue()
        assertThat(HistoryFilter(dayOfMonth = 3).hasEntryFilter).isTrue()
        assertThat(HistoryFilter(query = " ").isActive).isFalse()
        assertThat(HistoryFilter(query = "a").isActive).isTrue()
    }

    /** FR-037: a type tab accepts its own type only; the first accepts all. */
    @Test
    fun `given the type tabs when asked then each accepts its own type (FR-037)`() {
        TransactionType.entries.forEach { assertThat(HistoryTypeFilter.EVERY.accepts(it)).isTrue() }
        assertThat(HistoryTypeFilter.EXPENSE.accepts(TransactionType.INCOME)).isFalse()
        assertThat(HistoryTypeFilter.INCOME.accepts(TransactionType.INCOME)).isTrue()
        assertThat(HistoryTypeFilter.TRANSFER.accepts(TransactionType.TRANSFER)).isTrue()
        assertThat(HistoryTypeFilter.TRANSFER.accepts(TransactionType.EXPENSE)).isFalse()
    }

    /** FR-062: the change text carries the arrow and the colour follows the type. */
    @Test
    fun `given percent changes when worded then arrows and zero read right (FR-062)`() {
        assertThat(percentText(12L)).isEqualTo("▲ 12%")
        assertThat(percentText(-7L)).isEqualTo("▼ 7%")
        assertThat(percentText(0L)).isEqualTo("0%")
    }
}
