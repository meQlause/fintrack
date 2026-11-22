// DOC-03: core:testing, fakes and test builders; the test framework types are part of its API
plugins {
    id("fintrack.android.library")
}

dependencies {
    api(platform(libs.junit5.bom))
    api(libs.junit4)
    api(libs.junit.jupiter.api)
    api(libs.coroutines.core)
    implementation(libs.coroutines.test)
    testImplementation(libs.assertk)
}
