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
                namespace = FintrackConfig.ROOT_PACKAGE
                lint {
                    warningsAsErrors = true
                    abortOnError = true
                    checkDependencies = true
                    lintConfig = rootProject.file("config/lint/lint.xml")
                }
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
            pluginManager.apply("fintrack.android.compose")
            applyQuality()
        }
    }
}
