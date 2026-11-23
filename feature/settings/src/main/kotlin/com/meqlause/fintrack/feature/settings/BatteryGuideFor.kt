package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.feature.settings.R.string as S

// DOC-07: picks the OEM steps from the device manufacturer; unknown makers get the generic Android steps
internal fun batteryGuideFor(manufacturer: String): BatteryGuide {
    val maker = manufacturer.lowercase()
    return when {
        listOf("xiaomi", "redmi", "poco").any { it in maker } -> XIAOMI_GUIDE
        listOf("oppo", "realme", "oneplus").any { it in maker } -> OPPO_GUIDE
        "vivo" in maker -> VIVO_GUIDE
        "samsung" in maker -> SAMSUNG_GUIDE
        else -> GENERIC_GUIDE
    }
}

// DOC-07: Xiaomi, Redmi and Poco (MIUI / HyperOS)
private val XIAOMI_GUIDE =
    BatteryGuide(
        brand = S.battery_brand_xiaomi,
        steps = listOf(S.battery_xiaomi_step_1, S.battery_xiaomi_step_2, S.battery_xiaomi_step_3),
    )

// DOC-07: Oppo, Realme and OnePlus (ColorOS, Realme UI, OxygenOS)
private val OPPO_GUIDE =
    BatteryGuide(
        brand = S.battery_brand_oppo,
        steps = listOf(S.battery_oppo_step_1, S.battery_oppo_step_2),
    )

// DOC-07: Vivo (Funtouch OS)
private val VIVO_GUIDE =
    BatteryGuide(
        brand = S.battery_brand_vivo,
        steps = listOf(S.battery_vivo_step_1, S.battery_vivo_step_2),
    )

// DOC-07: Samsung (One UI)
private val SAMSUNG_GUIDE =
    BatteryGuide(
        brand = S.battery_brand_samsung,
        steps = listOf(S.battery_samsung_step_1, S.battery_samsung_step_2),
    )

// DOC-07: any other maker, using the stock Android battery screen
private val GENERIC_GUIDE =
    BatteryGuide(
        brand = S.battery_brand_generic,
        steps = listOf(S.battery_generic_step_1, S.battery_generic_step_2),
    )
