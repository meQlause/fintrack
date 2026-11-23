// DOC-03: app, entry point and Hilt root; declares only the libraries its sources import
plugins {
    id("fintrack.android.application")
    id("fintrack.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:parser"))
    implementation(project(":core:security"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:category"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:wallet"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:transaction"))
    implementation(libs.navigation.compose)
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.common)
    implementation(libs.serialization.core)
    implementation(libs.compose.animation)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.ui.graphics)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.activity.compose)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.lifecycle.viewmodel.savedstate)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.coroutines.core)
    implementation(libs.datastore.preferences.core)
    implementation(libs.dagger)
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.annotation)
    implementation(libs.datastore.core)
    implementation(libs.javax.inject)
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)
}

dependencies {
    testImplementation(project(":core:testing"))
    testImplementation(libs.konsist)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.assertk)
    testImplementation(libs.guava)
    testImplementation(libs.robolectric.annotations)
    testImplementation(project(":core:model"))
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test.junit4)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    testImplementation(libs.compose.ui.test)
    testImplementation(libs.androidx.test.ext.junit)
}

// DOC-10: the manifest test reads the merged release manifest
tasks.matching { it.name == "testDebugUnitTest" }.configureEach {
    dependsOn("processReleaseMainManifest")
    // DOC-06: the Konsist architecture tests read every module's sources, so those files are inputs of this
    // task; without them Gradle reuses a cached result after a change in another module
    inputs
        .files(
            rootProject.fileTree(rootDir) {
                include("**/src/**/*.kt", "**/*.gradle.kts", "**/libs.versions.toml")
                exclude("**/build/**", ".gradle/**", ".claude/**")
            },
        ).withPropertyName("architectureSources")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
