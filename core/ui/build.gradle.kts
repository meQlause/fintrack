// DOC-03: core:ui, shared feature UI (money and date formatters, MoneyText, TransactionRow); depends on model and designsystem only
plugins {
    id("fintrack.android.library")
    id("fintrack.android.compose")
}

dependencies {
    api(project(":core:model"))
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.runtime)
    api(project(":core:designsystem"))
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime.saveable)
    api(libs.compose.foundation.layout)
    implementation(libs.compose.ui.graphics)
    api(libs.compose.ui.text)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)

    testImplementation(project(":core:testing"))
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.junit4)
    testImplementation(libs.assertk)
    testImplementation(libs.robolectric)
    testImplementation(libs.robolectric.annotations)
    testImplementation(libs.robolectric.shadows.framework)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.core)
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    implementation(libs.androidx.annotation)
    implementation(libs.compose.ui.unit)
    implementation(libs.compose.animation)
    implementation(libs.compose.animation.core)
    implementation(libs.coroutines.core)
}
