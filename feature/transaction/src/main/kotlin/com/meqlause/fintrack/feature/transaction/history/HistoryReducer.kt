package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.HistoryView

// FR-036, FR-037: applies one event to the selection; pure, so the ViewModel only wires flows
internal fun HistorySelection.reduce(event: HistoryEvent): HistorySelection =
    when (event) {
        is HistoryEvent.MonthPicked,
        is HistoryEvent.MonthShown,
        HistoryEvent.PreviousMonth,
        HistoryEvent.NextMonth,
        is HistoryEvent.YearShifted,
        -> reduceMonth(event)

        is HistoryEvent.TypePicked,
        is HistoryEvent.WalletPicked,
        is HistoryEvent.CategoryPicked,
        is HistoryEvent.DayPicked,
        HistoryEvent.AllDaysPicked,
        is HistoryEvent.QueryChanged,
        -> reduceFilter(event)

        HistoryEvent.SearchToggled,
        is HistoryEvent.SheetOpened,
        HistoryEvent.SheetDismissed,
        -> reduceSheet(event)

        else -> reduceStats(event)
    }

// FR-036: the month in view, chosen or stepped, and the year the month picker shows
private fun HistorySelection.reduceMonth(event: HistoryEvent): HistorySelection =
    when (event) {
        is HistoryEvent.MonthPicked -> {
            copy(month = event.month).withSheet { copy(sheet = null) }
        }

        is HistoryEvent.MonthShown -> {
            copy(month = event.month, filter = filter.copy(dayOfMonth = null))
                .withSheet { copy(pickerYear = event.month.year) }
        }

        HistoryEvent.PreviousMonth -> {
            copy(month = month.minusMonths(1L))
        }

        HistoryEvent.NextMonth -> {
            copy(month = month.plusMonths(1L))
        }

        is HistoryEvent.YearShifted -> {
            withSheet { copy(pickerYear = pickerYear + event.delta) }
        }

        else -> {
            this
        }
    }

// FR-037: the type, wallet, category, day and text filters; picking from a sheet closes it
private fun HistorySelection.reduceFilter(event: HistoryEvent): HistorySelection =
    when (event) {
        is HistoryEvent.TypePicked -> {
            copy(filter = filter.copy(type = event.type))
        }

        is HistoryEvent.WalletPicked -> {
            copy(filter = filter.copy(walletId = event.walletId)).withSheet { copy(sheet = null) }
        }

        is HistoryEvent.CategoryPicked -> {
            copy(filter = filter.copy(categoryId = event.categoryId)).withSheet { copy(sheet = null) }
        }

        is HistoryEvent.DayPicked -> {
            val picked = event.dayOfMonth.takeIf { it != filter.dayOfMonth }
            copy(filter = filter.copy(dayOfMonth = picked)).withSheet { copy(sheet = null) }
        }

        HistoryEvent.AllDaysPicked -> {
            copy(filter = filter.copy(dayOfMonth = null)).withSheet { copy(sheet = null) }
        }

        is HistoryEvent.QueryChanged -> {
            copy(filter = filter.copy(query = event.query))
        }

        else -> {
            this
        }
    }

// S-05: the search field and the sheets
private fun HistorySelection.reduceSheet(event: HistoryEvent): HistorySelection =
    when (event) {
        HistoryEvent.SearchToggled -> toggledSearch()
        is HistoryEvent.SheetOpened -> withSheet { copy(sheet = event.sheet, pickerYear = month.year) }
        HistoryEvent.SheetDismissed -> withSheet { copy(sheet = null) }
        else -> this
    }

// FR-060: the list or statistics view, the statistics type and the category drill-down
private fun HistorySelection.reduceStats(event: HistoryEvent): HistorySelection =
    when (event) {
        is HistoryEvent.ViewPicked -> {
            withStats { copy(view = event.view) }.withSheet { copy(sheet = null) }
        }

        is HistoryEvent.StatisticsTypePicked -> {
            withStats { copy(statisticsType = event.type) }
        }

        HistoryEvent.CategoriesToggled -> {
            withStats { copy(isCategoriesExpanded = !isCategoriesExpanded) }
        }

        is HistoryEvent.CategoryDrilled -> {
            copy(filter = HistoryFilter(type = event.type, categoryId = event.categoryId))
                .withStats { copy(view = HistoryView.LIST) }
                .withSheet { copy(isSearchOpen = false) }
        }

        else -> {
            this
        }
    }

// S-05: closing the search field also clears its text so the list is not left narrowed by a hidden field
private fun HistorySelection.toggledSearch(): HistorySelection =
    if (isSearchOpen) {
        copy(filter = filter.copy(query = "")).withSheet { copy(isSearchOpen = false) }
    } else {
        withSheet { copy(isSearchOpen = true) }
    }
