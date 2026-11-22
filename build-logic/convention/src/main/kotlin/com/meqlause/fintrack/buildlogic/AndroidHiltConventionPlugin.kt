package com.meqlause.fintrack.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

// DOC-03: fintrack.android.hilt — Hilt processing with KSP; modules declare the Hilt libraries they use
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("com.google.dagger.hilt.android")
        }
    }
}
