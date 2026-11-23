package com.meqlause.fintrack.core.domain.detection

/** FR-081, DOC-07: the apps whose notifications can be read, by package name; verify each on a device. */
public object SupportedApps {
    /** FR-081: package name to the name shown in the monitored apps list, in display order. */
    public val displayNames: Map<String, String> =
        linkedMapOf(
            "com.bca.mybca.omni.android" to "myBCA",
            "com.bca" to "BCA mobile",
            "id.bmri.livin" to "Livin' by Mandiri",
            "id.co.bri.brimo" to "BRImo",
            "com.gojek.gopay" to "GoPay",
            "com.gojek.app" to "Gojek",
            "ovo.id" to "OVO",
            "id.dana" to "DANA",
            "com.shopee.id" to "ShopeePay",
        )
}
