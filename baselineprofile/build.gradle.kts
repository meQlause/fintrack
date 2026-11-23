// NFR-004: generates the Baseline Profile and runs the startup benchmark against :app on a device
plugins {
    id("fintrack.android.baselineprofile")
    alias(libs.plugins.baselineprofile)
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.baselineprofile.benchmark)
}

// NFR-004: the profile is collected from a connected device; no managed device is declared here
baselineProfile {
    useConnectedDevices = true
}
