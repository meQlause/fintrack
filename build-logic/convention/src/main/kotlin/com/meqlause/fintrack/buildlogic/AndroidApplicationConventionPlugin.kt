package com.meqlause.fintrack.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// DOC-03: fintrack.android.application — the app module (Android application, Compose, quality)
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            extensions.configure<ApplicationExtension> {
                compileSdk = FintrackConfig.COMPILE_SDK
                defaultConfig {
                    minSdk = FintrackConfig.MIN_SDK
                }
                compileOptions {
                    sourceCompatibility = FintrackConfig.JAVA_VERSION
                    targetCompatibility = FintrackConfig.JAVA_VERSION
                }
            }
            configureKotlin()
            pluginManager.apply("fintrack.android.compose")
            applyQuality()
        }
    }
}
