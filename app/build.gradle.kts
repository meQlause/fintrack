// DOC-03: app, entry point, navigation graph and Hilt root
plugins {
    id("fintrack.android.application")
    id("fintrack.android.hilt")
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:parser"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:data"))
    implementation(project(":core:notification"))
    implementation(project(":core:security"))
    implementation(project(":core:backup"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:transaction"))
    implementation(project(":feature:inbox"))
    implementation(project(":feature:wallet"))
    implementation(project(":feature:category"))
    implementation(project(":feature:budget"))
    implementation(project(":feature:statistics"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:lock"))
    implementation(project(":feature:credit"))
    implementation(project(":feature:loan"))
    implementation(project(":feature:investment"))
}
