// DOC-03: convention plugins; Gradle plugin artifacts are on the runtime classpath and versioned in the catalog
plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
    implementation(libs.roborazzi.gradle.plugin)
    implementation(libs.dependency.analysis.gradle.plugin)
}

val conventionPackage = "com.meqlause.fintrack.buildlogic"

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "fintrack.android.application"
            implementationClass = "$conventionPackage.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "fintrack.android.library"
            implementationClass = "$conventionPackage.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "fintrack.android.compose"
            implementationClass = "$conventionPackage.AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "fintrack.android.feature"
            implementationClass = "$conventionPackage.AndroidFeatureConventionPlugin"
        }
        register("androidRoom") {
            id = "fintrack.android.room"
            implementationClass = "$conventionPackage.AndroidRoomConventionPlugin"
        }
        register("androidHilt") {
            id = "fintrack.android.hilt"
            implementationClass = "$conventionPackage.AndroidHiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = "fintrack.jvm.library"
            implementationClass = "$conventionPackage.JvmLibraryConventionPlugin"
        }
        register("quality") {
            id = "fintrack.quality"
            implementationClass = "$conventionPackage.QualityConventionPlugin"
        }
    }
}
