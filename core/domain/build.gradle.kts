// DOC-03: core:domain, use cases and repository interfaces
plugins {
    id("fintrack.jvm.library")
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
}
