package com.meqlause.fintrack.buildlogic

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// DOC-03: every convention plugin reads versions from the one catalog in gradle/libs.versions.toml
internal fun Project.versionCatalog(): VersionCatalog =
    extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.library(alias: String): Provider<MinimalExternalModuleDependency> =
    versionCatalog().findLibrary(alias).orElseThrow { GradleException("Missing catalog library: $alias") }

// DOC-05: fintrack.quality is applied by every other convention plugin (05 §4)
internal fun Project.applyQuality() {
    pluginManager.apply("fintrack.quality")
}

// DOC-03: Kotlin is compiled for JDK 21 and every warning fails the build
internal fun Project.configureKotlin() {
    extensions.configure(KotlinBaseExtension::class.java) {
        jvmToolchain(FintrackConfig.JDK_VERSION)
    }
    tasks.withType(KotlinCompile::class.java).configureEach {
        compilerOptions.allWarningsAsErrors.set(true)
    }
}
