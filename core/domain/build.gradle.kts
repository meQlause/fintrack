// DOC-03: core:domain, use cases and repository interfaces
plugins {
    id("fintrack.jvm.library")
}

dependencies {
    api(project(":core:model"))
    api(libs.coroutines.core)
    api(libs.javax.inject)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertk)
}
