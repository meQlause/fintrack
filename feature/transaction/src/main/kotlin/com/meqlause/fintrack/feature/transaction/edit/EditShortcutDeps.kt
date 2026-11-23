package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import javax.inject.Inject

// FR-039: the shortcut store and the use case that validates and saves a shortcut, injected as one
internal class EditShortcutDeps
    @Inject
    constructor(
        val quickEntries: QuickEntryRepository,
        val saveQuickEntry: SaveQuickEntryUseCase,
    )
