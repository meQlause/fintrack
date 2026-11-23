// DOC-03: core:data, repository implementations
plugins {
    id("fintrack.android.library")
}

dependencies {
    api(project(":core:model"))
    api(project(":core:domain"))
    api(project(":core:database"))
    api(project(":core:datastore"))
    api(libs.coroutines.core)
    implementation(libs.room.ktx)

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.datastore.core)
    testImplementation(libs.datastore.preferences.core)
    testImplementation(libs.room.runtime)
}
