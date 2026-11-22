package com.meqlause.fintrack.core.parser

import kotlinx.serialization.Serializable

/** DOC-07: the two outcomes a fixture can expect. */
@Serializable
internal enum class FixtureKind {
    TRANSACTION,
    IGNORED,
}
