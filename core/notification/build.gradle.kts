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
    implementation(libs.hilt.work)
    implementation(libs.work.runtime.ktx)
    ksp(libs.hilt.androidx.compiler)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit4)
    testImplementation(libs.assertk)
    testImplementation(libs.robolectric)
    testImplementation(libs.robolectric.annotations)
    testImplementation(libs.androidx.test.core)
}
