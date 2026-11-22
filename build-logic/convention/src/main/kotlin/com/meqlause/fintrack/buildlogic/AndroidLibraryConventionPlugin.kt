package com.meqlause.fintrack.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// DOC-03: fintrack.android.library — Android library module without Compose or Hilt
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            extensions.configure<LibraryExtension> {
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
            applyQuality()
        }
    }
}
