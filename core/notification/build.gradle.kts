// DOC-03: core:notification, notification listener
plugins {
    id("fintrack.android.library")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:parser"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
}
