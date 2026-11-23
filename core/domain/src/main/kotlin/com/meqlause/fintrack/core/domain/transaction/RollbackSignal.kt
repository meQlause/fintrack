package com.meqlause.fintrack.core.domain.transaction

// BR-011: thrown inside the atomic block so the whole store rolls back, then unwrapped into the rule result
internal class RollbackSignal(
    val result: SaveTransactionResult.Rejected,
) : RuntimeException()
