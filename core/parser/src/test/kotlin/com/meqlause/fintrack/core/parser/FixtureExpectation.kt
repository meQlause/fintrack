package com.meqlause.fintrack.core.parser

import kotlinx.serialization.Serializable

/** DOC-07: the expected result of a fixture; [reason] is only used for IGNORED fixtures. */
@Serializable
internal data class FixtureExpectation(
    val kind: FixtureKind = FixtureKind.TRANSACTION,
    val amount: Long = 0L,
    val direction: String = "",
    val merchant: String = "",
    val reason: String = "",
)
