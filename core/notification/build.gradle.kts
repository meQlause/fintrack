// DOC-03: core:notification, notification listener and access check (FR-080 … FR-083, FR-090)
plugins {
    id("fintrack.android.library")
    id("fintrack.android.hilt")
}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
    api(project(":core:domain"))
    api(project(":core:parser"))
    api(libs.coroutines.core)
    api(libs.dagger)
    api(libs.hilt.android)
    api(libs.hilt.work)
    ksp(libs.hilt.androidx.compiler)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit4)
    testImplementation(libs.assertk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    implementation(libs.androidx.annotation)
    api(libs.work.runtime)
    api(libs.hilt.core)
    api(libs.javax.inject)
}
