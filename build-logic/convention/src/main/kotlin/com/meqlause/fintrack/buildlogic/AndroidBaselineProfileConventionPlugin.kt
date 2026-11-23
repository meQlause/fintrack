package com.meqlause.fintrack.buildlogic

import com.android.build.api.dsl.TestExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// NFR-004: fintrack.android.baselineprofile — the test module that drives :app on a device to build its profile
class AndroidBaselineProfileConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.test")
            extensions.configure<TestExtension> {
                namespace = "${FintrackConfig.ROOT_PACKAGE}.baselineprofile"
                compileSdk {
                    version =
                        release(FintrackConfig.COMPILE_SDK) {
                            minorApiLevel = FintrackConfig.COMPILE_SDK_MINOR
                        }
                }
                defaultConfig {
                    minSdk = FintrackConfig.MIN_SDK
                }
                targetProjectPath = ":app"
            }
            configureKotlin()
            applyQuality()
        }
    }
}
