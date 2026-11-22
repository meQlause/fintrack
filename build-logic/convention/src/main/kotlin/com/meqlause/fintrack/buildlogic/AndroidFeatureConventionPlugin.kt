package com.meqlause.fintrack.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

// DOC-03: fintrack.android.feature — a screen module: library and Compose; a feature applies fintrack.android.hilt when it needs injection
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("fintrack.android.library")
            pluginManager.apply("fintrack.android.compose")
        }
    }
}
