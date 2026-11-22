// DOC-03: core:common, pure Kotlin clock, dispatchers and logger
plugins {
    id("fintrack.jvm.library")
}

dependencies {
    api(libs.javax.inject)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.platform.commons)
    testImplementation(libs.opentest4j)
    testRuntimeOnly(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertk)
}
