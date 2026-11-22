// DOC-03: app, entry point and Hilt root; declares only the libraries its sources import
plugins {
    id("fintrack.android.application")
    id("fintrack.android.hilt")
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.lifecycle.viewmodel.savedstate)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.text)
    implementation(libs.dagger)
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test.junit4)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    testImplementation(libs.compose.ui.test)
    testImplementation(libs.androidx.test.ext.junit)
}

// DOC-10: the manifest test reads the merged release manifest
tasks.matching { it.name == "testDebugUnitTest" }.configureEach {
    dependsOn("processReleaseMainManifest")
}
