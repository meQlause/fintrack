package com.meqlause.fintrack.core.parser

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.Money
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/** FR-083, DOC-07, DOC-06: one parameterised test runs every fixture against the parsing utilities. */
internal class NotificationFixtureTest {
    /** DOC-07: a fixture expects either a transaction with its amount and direction, or an ignored reason. */
    @ParameterizedTest(name = "{0}", allowZeroInvocations = true)
    @MethodSource("fixtures")
    fun `given fixture when parsing utilities run then expected outcome holds (FR-083, DOC-07)`(
        fixture: NotificationFixture,
    ) {
        when (fixture.expected.kind) {
            FixtureKind.TRANSACTION -> assertTransaction(fixture)
            FixtureKind.IGNORED -> assertIgnored(fixture)
        }
    }

    /** DOC-07: a transaction text is not ignored, carries the expected amount and direction. */
    private fun assertTransaction(fixture: NotificationFixture) {
        val expected = fixture.expected
        val amounts = IndonesianAmountParser.findAll(fixture.text).map(Money::rupiah)

        assertThat(IgnoreClassifier.classify(fixture.text)).isNull()
        assertThat(amounts).contains(expected.amount)
        assertThat(DirectionClassifier.classify(fixture.text)?.name).isEqualTo(expected.direction)
        if (expected.merchant.isNotEmpty()) {
            assertThat(fixture.text).contains(expected.merchant)
        }
    }

    /** DOC-07: an ignored text carries an ignore reason, and the expected one when the fixture names it. */
    private fun assertIgnored(fixture: NotificationFixture) {
        val reason = IgnoreClassifier.classify(fixture.text)

        assertThat(reason).isNotNull()
        if (fixture.expected.reason.isNotEmpty()) {
            assertThat(reason?.name).isEqualTo(fixture.expected.reason)
        }
    }

    companion object {
        /** DOC-07: all fixtures under the fixtures folder; empty until real fixtures are committed. */
        @JvmStatic
        fun fixtures(): List<NotificationFixture> = FixtureLoader.load()
    }
}
