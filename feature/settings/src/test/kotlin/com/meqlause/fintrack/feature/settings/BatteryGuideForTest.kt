package com.meqlause.fintrack.feature.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

// DOC-07: the manufacturer picks the OEM guide; unknown makers fall back to the generic one
class BatteryGuideForTest {
    @Test
    fun givenXiaomiWhenResolvedThenTheXiaomiGuideIsUsed() {
        assertThat(batteryGuideFor("Xiaomi").brand).isEqualTo(R.string.battery_brand_xiaomi)
    }

    @Test
    fun givenSamsungWhenResolvedThenTheSamsungGuideIsUsed() {
        assertThat(batteryGuideFor("samsung").brand).isEqualTo(R.string.battery_brand_samsung)
    }

    @Test
    fun givenUnknownMakerWhenResolvedThenTheGenericGuideIsUsed() {
        assertThat(batteryGuideFor("Nokia").brand).isEqualTo(R.string.battery_brand_generic)
    }

    @Test
    fun givenOppoFamilyWhenResolvedThenTheOppoGuideIsUsed() {
        assertThat(batteryGuideFor("realme").brand).isEqualTo(R.string.battery_brand_oppo)
        assertThat(batteryGuideFor("OnePlus").brand).isEqualTo(R.string.battery_brand_oppo)
    }

    @Test
    fun givenVivoWhenResolvedThenTheVivoGuideIsUsed() {
        assertThat(batteryGuideFor("vivo").brand).isEqualTo(R.string.battery_brand_vivo)
    }
}
