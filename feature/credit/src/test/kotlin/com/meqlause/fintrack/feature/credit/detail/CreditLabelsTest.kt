package com.meqlause.fintrack.feature.credit.detail

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.credit.R
import org.junit.Test

// FR-131, FR-138, FR-139, S-14: the labels and the day drawer rules of the credit wallet detail
class CreditLabelsTest {
    /** FR-139: a paylater and a card read differently. */
    @Test
    fun `given the two credit types when labelled then they differ (FR-139)`() {
        assertThat(CreditLabels.type(WalletType.PAYLATER)).isEqualTo(R.string.credit_type_paylater)
        assertThat(CreditLabels.type(WalletType.CREDIT_CARD)).isEqualTo(R.string.credit_type_card)
    }

    /** BR-034: every statement status has its own label. */
    @Test
    fun `given every status when labelled then each has its own string (BR-034)`() {
        val labels = StatementStatus.entries.map { CreditLabels.status(it) }
        assertThat(labels.toSet().size).isEqualTo(StatementStatus.entries.size)
    }

    /** FR-138: the drawer title follows the day being changed. */
    @Test
    fun `given the two days when the drawer is built then the title follows (FR-138)`() {
        assertThat(pickerTitle(CreditDay.STATEMENT)).isNotEqualTo(pickerTitle(CreditDay.DUE))
    }
}
