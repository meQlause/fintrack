// DOC-03: core:designsystem, theme and base components; depends on no other project module
plugins {
    id("fintrack.android.library")
    id("fintrack.android.compose")
}

dependencies {
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.runtime)
    api(libs.compose.foundation)
    api(libs.compose.ui.graphics)
    api(libs.compose.ui.text)
    api(libs.compose.animation.core)
    implementation(libs.compose.runtime.annotation)
    implementation(libs.coroutines.core)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.ui.unit)
    implementation(libs.compose.material3)

    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.robolectric.annotations)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.core)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
}
