package com.meqlause.fintrack.core.domain.detection

import com.meqlause.fintrack.core.parser.NotificationParser
import com.meqlause.fintrack.core.parser.ParseResult
import com.meqlause.fintrack.core.parser.RawNotification

// FR-083: a test double standing in for an app parser; no bank or wallet template is modelled here
class StubNotificationParser(
    override val packageNames: Set<String>,
    private val answer: (RawNotification) -> ParseResult,
) : NotificationParser {
    override val parserId: String = "test.stub.v1"

    override fun parse(raw: RawNotification): ParseResult = answer(raw)
}
