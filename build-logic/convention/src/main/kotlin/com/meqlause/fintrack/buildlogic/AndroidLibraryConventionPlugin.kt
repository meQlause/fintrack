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
                namespace = FintrackConfig.ROOT_PACKAGE + path.replace(':', '.')
                compileSdk {
                    version =
                        release(FintrackConfig.COMPILE_SDK) {
                            minorApiLevel = FintrackConfig.COMPILE_SDK_MINOR
                        }
                }
                defaultConfig {
                    minSdk = FintrackConfig.MIN_SDK
                }
            }
            configureKotlin()
            applyQuality()
        }
    }
}
