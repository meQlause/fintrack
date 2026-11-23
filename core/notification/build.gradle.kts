// DOC-03: core:notification, notification listener and access check (FR-080 … FR-083, FR-090)
plugins {
    id("fintrack.android.library")
    id("fintrack.android.hilt")
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:parser"))
    implementation(libs.coroutines.core)
    implementation(libs.dagger)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
