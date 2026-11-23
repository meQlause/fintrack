package com.meqlause.fintrack.feature.transaction.history

// FR-036, FR-037: applies one event to the selection; pure, so the ViewModel only wires flows
internal fun HistorySelection.reduce(event: HistoryEvent): HistorySelection =
    when (event) {
        is HistoryEvent.MonthPicked -> {
            copy(month = event.month, sheet = null)
        }

        HistoryEvent.PreviousMonth -> {
            copy(month = month.minusMonths(1L))
        }

        HistoryEvent.NextMonth -> {
            copy(month = month.plusMonths(1L))
        }

        is HistoryEvent.YearShifted -> {
            copy(pickerYear = pickerYear + event.delta)
        }

        is HistoryEvent.TypePicked -> {
            copy(filter = filter.copy(type = event.type))
        }

        is HistoryEvent.WalletPicked -> {
            copy(filter = filter.copy(walletId = event.walletId), sheet = null)
        }

        is HistoryEvent.CategoryPicked -> {
            copy(filter = filter.copy(categoryId = event.categoryId), sheet = null)
        }

        is HistoryEvent.DayPicked -> {
            copy(
                filter = filter.copy(day = event.day.takeIf { it != filter.day }),
                sheet = null,
            )
        }

        is HistoryEvent.QueryChanged -> {
            copy(filter = filter.copy(query = event.query))
        }

        HistoryEvent.SearchToggled -> {
            toggledSearch()
        }

        is HistoryEvent.SheetOpened -> {
            copy(sheet = event.sheet, pickerYear = month.year)
        }

        HistoryEvent.SheetDismissed -> {
            copy(sheet = null)
        }
    }

// S-05: closing the search field also clears its text so the list is not left narrowed by a hidden field
private fun HistorySelection.toggledSearch(): HistorySelection =
    if (isSearchOpen) {
        copy(isSearchOpen = false, filter = filter.copy(query = ""))
    } else {
        copy(isSearchOpen = true)
    }
