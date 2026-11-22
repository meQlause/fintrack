package com.meqlause.fintrack.buildlogic

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

// DOC-02: fintrack.android.room — Room with KSP; exported schemas live beside the database module
class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            dependencies {
                add("implementation", library("room-runtime"))
                add("implementation", library("room-ktx"))
                add("ksp", library("room-compiler"))
            }
            extensions.configure<KspExtension> {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }
}
