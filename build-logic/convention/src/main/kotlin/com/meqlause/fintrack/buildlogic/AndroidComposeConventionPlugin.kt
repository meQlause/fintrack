package com.meqlause.fintrack.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

// DOC-08: fintrack.android.compose — Compose for application, feature, designsystem and ui modules only
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    buildFeatures { compose = true }
                }
            }
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    buildFeatures { compose = true }
                }
            }
            dependencies {
                add("implementation", platform(library("compose-bom")))
                add("implementation", library("compose-ui"))
                add("implementation", library("compose-material3"))
                add("implementation", library("compose-ui-tooling-preview"))
                add("debugImplementation", library("compose-ui-tooling"))
            }
        }
    }
}
