// DOC-03: core:data, repository implementations
plugins {
    id("fintrack.android.library")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
}
