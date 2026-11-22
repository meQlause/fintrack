// DOC-03: convention plugins; Gradle plugin artifacts are compileOnly and versioned in the catalog
plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
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
