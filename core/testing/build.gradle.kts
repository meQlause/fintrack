// DOC-03: core:testing, fakes and test builders; the test framework types are part of its API
plugins {
    id("fintrack.android.library")
}

dependencies {
    api(libs.junit4)
    api(libs.coroutines.core)
    implementation(libs.coroutines.test)
    testImplementation(libs.assertk)
}
