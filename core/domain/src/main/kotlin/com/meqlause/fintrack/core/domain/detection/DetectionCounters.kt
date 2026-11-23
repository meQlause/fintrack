package com.meqlause.fintrack.core.domain.detection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** FR-085, FR-090: in-memory counters for the diagnostics screen; they restart with the process. */
@Singleton
public class DetectionCounters
    @Inject
    constructor() {
        private val current = MutableStateFlow(DetectionCounts())

        /** FR-090: the counts since the process started. */
        public val counts: StateFlow<DetectionCounts> = current.asStateFlow()

        /** FR-085: a notification that matched an ignore rule. */
        public fun countIgnored() {
            current.update { it.copy(ignored = it.ignored + 1) }
        }

        /** FR-085: a notification from a monitored app that no template recognized. */
        public fun countUnrecognized() {
            current.update { it.copy(unrecognized = it.unrecognized + 1) }
        }
    }
