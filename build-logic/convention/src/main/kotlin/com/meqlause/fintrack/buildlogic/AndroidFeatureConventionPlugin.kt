package com.meqlause.fintrack.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

// DOC-03: fintrack.android.feature — a screen module: library, Compose, Hilt and the shared core modules
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("fintrack.android.library")
            pluginManager.apply("fintrack.android.compose")
            pluginManager.apply("fintrack.android.hilt")
            dependencies {
                add("implementation", project(":core:model"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))
                add("implementation", library("hilt-navigation-compose"))
            }
        }
    }
}
