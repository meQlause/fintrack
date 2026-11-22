package com.meqlause.fintrack.core.model

/** DOC-02: free text of a transaction; note up to 120 chars, merchant up to 60 chars. */
public data class Memo(
    public val note: String = "",
    public val merchant: String = "",
)
