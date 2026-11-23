package com.meqlause.fintrack.feature.credit.instalment

import com.meqlause.fintrack.core.domain.usecase.DeleteInstalmentPlanUseCase
import com.meqlause.fintrack.core.domain.usecase.PayOffInstalmentUseCase
import javax.inject.Inject

// FR-144, FR-145: the two things the instalment detail can do to a plan
internal class InstalmentActions
    @Inject
    constructor(
        val payOffInstalment: PayOffInstalmentUseCase,
        val deleteInstalment: DeleteInstalmentPlanUseCase,
    )
