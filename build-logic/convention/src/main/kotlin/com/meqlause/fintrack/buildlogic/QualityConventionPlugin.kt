package com.meqlause.fintrack.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

// DOC-05: quality gates; empty until T1.6–T1.13 wire Spotless, detekt, lint, Konsist, Kover and Roborazzi
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // no gates yet: each one is added by its own task
    }
}
