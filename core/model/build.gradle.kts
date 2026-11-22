// DOC-03: core:model, pure Kotlin entities
plugins {
    id("fintrack.jvm.library")
}

dependencies {
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertk)
}
