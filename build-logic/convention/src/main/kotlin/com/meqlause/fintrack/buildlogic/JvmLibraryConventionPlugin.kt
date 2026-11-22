package com.meqlause.fintrack.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

// DOC-03: fintrack.jvm.library — pure Kotlin module with explicit API and JUnit 5 on the test task
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            extensions.getByType<KotlinJvmProjectExtension>().explicitApi()
            configureKotlin()
            tasks.withType(Test::class.java).configureEach {
                useJUnitPlatform()
            }
            applyQuality()
        }
    }
}
