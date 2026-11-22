// DOC-03: root settings; repositories are declared here only (FAIL_ON_PROJECT_REPOS)
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "fintrack"

// DOC-03: every module of the module list; dependency edges live in each module's build file
include(":app")
include(
    ":core:model",
    ":core:common",
    ":core:domain",
    ":core:parser",
    ":core:database",
    ":core:datastore",
    ":core:data",
    ":core:notification",
    ":core:security",
    ":core:backup",
    ":core:designsystem",
    ":core:ui",
    ":core:testing",
)
include(
    ":feature:onboarding",
    ":feature:dashboard",
    ":feature:transaction",
    ":feature:inbox",
    ":feature:wallet",
    ":feature:category",
    ":feature:budget",
    ":feature:statistics",
    ":feature:settings",
    ":feature:lock",
    ":feature:credit",
    ":feature:loan",
    ":feature:investment",
)
