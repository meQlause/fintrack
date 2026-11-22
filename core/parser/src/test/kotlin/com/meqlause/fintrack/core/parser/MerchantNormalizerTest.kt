package com.meqlause.fintrack.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

/** FR-088, DOC-07: merchant key for merchant memory and display name for the Inbox. */
class MerchantNormalizerTest {
    /** DOC-07: the normalisation example and the rules for case, digits, punctuation and suffixes. */
    @ParameterizedTest(name = "{0} -> {1} (FR-088)")
    @CsvSource(
        "INDOMARET CABANG KEMANG 12|indomaret kemang",
        "PT. Sumber Makmur Tbk.|sumber makmur",
        "CV Maju Jaya|maju jaya",
        "Toko-Baru|toko baru",
        "  Kopi   Kenangan  |kopi kenangan",
        "12345|''",
        "''|''",
        delimiter = '|',
    )
    fun `given merchant text when normalised then merchant key is returned (FR-088)`(
        merchant: String,
        expectedKey: String,
    ) {
        assertThat(MerchantNormalizer.merchantKey(merchant)).isEqualTo(expectedKey)
    }

    /** DOC-07: the merchant key is at most 40 characters. */
    @Test
    fun `given merchant longer than 40 characters when normalised then key is cut to 40 (DOC-07)`() {
        val merchantKey = MerchantNormalizer.merchantKey("a".repeat(50))

        assertThat(merchantKey.length).isEqualTo(40)
    }

    /** DOC-07: the display name is trimmed and keeps its letter case. */
    @Test
    fun `given padded merchant when displayed then trimmed and case is kept (DOC-07)`() {
        assertThat(MerchantNormalizer.displayName("  INDOMARET CABANG X  ")).isEqualTo("INDOMARET CABANG X")
    }

    /** DOC-07: the display name is at most 60 characters. */
    @Test
    fun `given merchant longer than 60 characters when displayed then cut to 60 (DOC-07)`() {
        assertThat(MerchantNormalizer.displayName("B".repeat(70)).length).isEqualTo(60)
    }
}
