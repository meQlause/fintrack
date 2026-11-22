// DOC-03: core:backup, backup, restore and CSV
plugins {
    id("fintrack.android.library")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:security"))
    implementation(project(":core:common"))
}
