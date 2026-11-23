// DOC-03: core:backup, encrypted backup, restore validation (11)
plugins {
    id("fintrack.android.library")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":core:model"))
    api(project(":core:domain"))
    api(project(":core:common"))
    api(libs.coroutines.core)
    implementation(libs.serialization.json)
    testImplementation(libs.junit4)
    testRuntimeOnly(libs.robolectric)
    testRuntimeOnly(libs.androidx.test.core)
    testImplementation(libs.assertk)
    testImplementation(libs.coroutines.test)
    implementation(libs.serialization.core)
}
