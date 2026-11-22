package com.meqlause.fintrack.core.parser

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * DOC-07: one fixture file in the 06 format; other 06 fields are ignored on read.
 * [source] is the fixture's path under the fixtures folder, used as the test display name.
 */
@Serializable
internal data class NotificationFixture(
    val text: String = "",
    val expected: FixtureExpectation = FixtureExpectation(),
    @Transient val source: String = "",
) {
    override fun toString(): String = source
}
