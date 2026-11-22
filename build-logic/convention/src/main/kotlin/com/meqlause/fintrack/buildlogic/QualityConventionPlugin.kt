package com.meqlause.fintrack.buildlogic

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// DOC-05: quality gates; Spotless with ktlint (official style, .editorconfig) formats Kotlin and Gradle scripts
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.diffplug.spotless")
            val ktlintVersion = versionCatalog().findVersion("ktlint").get().requiredVersion
            extensions.configure<SpotlessExtension> {
                kotlin {
                    target("src/**/*.kt", "build-logic/**/*.kt")
                    ktlint(ktlintVersion)
                }
                kotlinGradle {
                    target("**/*.gradle.kts")
                    ktlint(ktlintVersion)
                }
            }
        }
    }
}
