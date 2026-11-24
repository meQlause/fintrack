// DOC-03: app, entry point and Hilt root; declares only the libraries its sources import
import java.util.Properties

plugins {
    id("fintrack.android.application")
    id("fintrack.android.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.baselineprofile)
}

// DOC-12, ADR-013: release signing reads the ignored keystore.properties; without a complete file the release
// stays unsigned and the build never fails (CI and other checkouts have no key)
val signingKeys = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
val keystoreProperties =
    Properties().apply {
        val file = rootProject.file("keystore.properties")
        if (file.isFile) file.inputStream().use { stream -> load(stream) }
    }
val hasSigningKey = signingKeys.all { key -> keystoreProperties.getProperty(key).orEmpty().isNotBlank() }

android {
    defaultConfig {
        // DOC-12: versionCode = major * 10000 + minor * 100 + patch, versionName = SemVer
        versionCode = 10000
        versionName = "1.0.0"
    }
    signingConfigs {
        if (hasSigningKey) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
        }
    }
}

dependencies {
    // NFR-004: the generated profile is installed on first launch, so cold start runs precompiled code
    baselineProfile(project(":baselineprofile"))
    runtimeOnly(libs.profileinstaller)
    implementation(project(":core:backup"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:parser"))
    implementation(project(":core:notification"))
    implementation(project(":core:security"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:budget"))
    implementation(project(":feature:category"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:inbox"))
    implementation(project(":feature:wallet"))
    implementation(project(":feature:credit"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:transaction"))
    implementation(project(":feature:investment"))
    implementation(project(":feature:loan"))
    implementation(project(":feature:lock"))
    implementation(libs.navigation.compose)
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.common)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.ui.graphics)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.activity.compose)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.lifecycle.viewmodel.savedstate)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.process)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.coroutines.core)
    implementation(libs.datastore.preferences.core)
    implementation(libs.dagger)
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    implementation(libs.hilt.work)
    implementation(libs.androidx.annotation)
    implementation(libs.biometric)
    implementation(libs.datastore.core)
    implementation(libs.javax.inject)
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.work.runtime)
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
    implementation(project(":core:model"))
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
