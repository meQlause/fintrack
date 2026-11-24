// DOC-03: core:database, Room with SQLCipher
plugins {
    id("fintrack.android.library")
    id("fintrack.android.room")
}

dependencies {
    api(project(":core:model"))
    api(libs.room.runtime)
    api(libs.coroutines.core)
    implementation(libs.room.common)
    implementation(libs.sqlite)
    implementation(libs.sqlcipher.android)
    ksp(libs.room.compiler)

    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.monitor)
    testImplementation(libs.assertk)
    testImplementation(libs.room.testing)
}

// NFR-009: the migration test reads the exported schemas as assets
extensions.configure<com.android.build.api.dsl.LibraryExtension>("android") {
    sourceSets
        .getByName("test")
        .assets.directories
        .add("$projectDir/schemas")
}
