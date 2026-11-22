// DOC-03: core:datastore, DataStore settings
plugins {
    id("fintrack.android.library")
}

dependencies {
    api(project(":core:model"))
    api(libs.coroutines.core)
    api(libs.datastore.core)
    api(libs.datastore.preferences.core)

    testImplementation(libs.junit4)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.assertk)
}
