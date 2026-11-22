// DOC-03: core:database, Room with SQLCipher
plugins {
    id("fintrack.android.library")
    id("fintrack.android.room")
}

dependencies {
    implementation(project(":core:model"))
}
