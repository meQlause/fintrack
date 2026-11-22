package com.meqlause.fintrack.buildlogic

import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

// DOC-06 coverage: minimum line and branch coverage per module group, enforced by koverVerify in check
internal data class CoverageTarget(
    val line: Int,
    val branch: Int,
)

// DOC-06 exclusions: generated Hilt, factory and Room (*_Impl) code, modules, R, BuildConfig, @Preview
private val EXCLUDED_CLASSES =
    listOf(
        "*_Factory",
        "*_HiltModules*",
        "*Module",
        "*_MembersInjector",
        "*.R",
        "*.R\$*",
        "*BuildConfig",
        "*_Impl",
        "*_Impl\$*",
    )
private const val PREVIEW_ANNOTATION = "androidx.compose.ui.tooling.preview.Preview"

internal fun Project.coverageTarget(): CoverageTarget? =
    when {
        path == ":core:parser" -> CoverageTarget(line = 95, branch = 90)
        path == ":core:domain" || path == ":core:model" -> CoverageTarget(line = 90, branch = 85)
        path in DATA_MODULES -> CoverageTarget(line = 80, branch = 70)
        path.startsWith(":feature:") -> CoverageTarget(line = 85, branch = 75)
        else -> null
    }

// DOC-06: a module with no Kotlin sources yet is not verified, so empty skeletons do not fail check
internal fun Project.configureKover() {
    pluginManager.apply("org.jetbrains.kotlinx.kover")
    val target = coverageTarget()
    extensions.configure<KoverProjectExtension> {
        reports {
            filters {
                excludes {
                    classes(*EXCLUDED_CLASSES.toTypedArray())
                    annotatedBy(PREVIEW_ANNOTATION)
                }
            }
            if (target != null && hasKotlinSources()) {
                verify {
                    rule {
                        minBound(target.line, CoverageUnit.LINE, AggregationType.COVERED_PERCENTAGE)
                    }
                    rule {
                        minBound(target.branch, CoverageUnit.BRANCH, AggregationType.COVERED_PERCENTAGE)
                    }
                }
            }
        }
    }
}

// DOC-06 overall: the root merges every module's coverage and enforces 75% line coverage
internal fun Project.configureKoverAggregate() {
    // :core and :feature are grouping projects with no build file; only real modules are merged
    val modules = subprojects.filter { it.buildFile.exists() }
    dependencies {
        modules.forEach { module -> add("kover", module) }
    }
    extensions.configure<KoverProjectExtension> {
        reports {
            verify {
                rule {
                    minBound(OVERALL_LINE_MINIMUM, CoverageUnit.LINE, AggregationType.COVERED_PERCENTAGE)
                }
            }
        }
    }
}

private const val OVERALL_LINE_MINIMUM = 75

private fun Project.hasKotlinSources(): Boolean = fileTree("src").matching { include("**/*.kt") }.isEmpty.not()

private val DATA_MODULES = setOf(":core:data", ":core:database", ":core:security", ":core:backup")
