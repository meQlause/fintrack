package com.meqlause.fintrack.buildlogic

import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

// DOC-05: quality gates; Spotless (ktlint official style), detekt (strict, 05) and Compose rules
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.diffplug.spotless")
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            pluginManager.apply("com.autonomousapps.dependency-analysis")
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
            extensions.configure<DetektExtension> {
                config.setFrom(rootProject.file("config/detekt/detekt.yml"))
                buildUponDefaultConfig = true
                allRules = false
            }
            dependencies {
                add("detektPlugins", library("detekt-compose-rules"))
            }
        }
    }
}
