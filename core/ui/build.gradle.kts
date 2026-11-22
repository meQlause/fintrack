// DOC-03: core:ui, shared feature UI
plugins {
    id("fintrack.android.library")
    id("fintrack.android.compose")
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
}
