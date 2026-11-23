package com.meqlause.fintrack.feature.investment

import com.meqlause.fintrack.core.model.InvestKind

// FR-170: the new investment account being typed; kind and amount are required before saving
internal data class InvestmentDraft(
    val name: String = "",
    val kind: InvestKind? = null,
    val invested: String = "",
    val kindPickerOpen: Boolean = false,
    val nameRejected: Boolean = false,
) {
    val canSave: Boolean
        get() = name.isNotBlank() && kind != null
}
