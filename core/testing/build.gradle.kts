// DOC-03: core:testing, fakes and test builders
plugins {
    id("fintrack.android.library")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
}
