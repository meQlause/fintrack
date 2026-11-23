package com.meqlause.fintrack.core.domain.detection

/** FR-085, FR-090: how many received notifications were ignored or not recognized; no text is kept. */
public data class DetectionCounts(
    public val ignored: Int = 0,
    public val unrecognized: Int = 0,
)
