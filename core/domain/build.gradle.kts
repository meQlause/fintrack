// DOC-03: core:domain, use cases and repository interfaces
plugins {
    id("fintrack.jvm.library")
}

dependencies {
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.platform.commons)
    testImplementation(libs.opentest4j)
    testRuntimeOnly(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertk)
}
