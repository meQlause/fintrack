package com.meqlause.fintrack.core.ui.inbox

// S-07, S-03: the source app label of a detected notice, from the package names of the monitored apps
object InboxSourceLabel {
    fun forPackage(packageName: String): String =
        when {
            packageName.startsWith("com.bca") -> "BCA"
            packageName.startsWith("id.bmri.livin") -> "Livin' Mandiri"
            packageName.startsWith("id.co.bri.brimo") -> "BRImo"
            packageName.startsWith("com.gojek") -> "GoPay"
            packageName == "ovo.id" -> "OVO"
            packageName == "id.dana" -> "DANA"
            packageName == "com.shopee.id" -> "ShopeePay"
            else -> packageName
        }
}
