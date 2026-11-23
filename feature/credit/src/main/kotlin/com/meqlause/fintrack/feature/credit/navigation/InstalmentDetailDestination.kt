package com.meqlause.fintrack.feature.credit.navigation

import kotlinx.serialization.Serializable

// FR-144, S-16: the instalment detail route; the plan id is the only argument
@Serializable
public data class InstalmentDetailDestination(
    public val planId: Long,
)
