// DOC-03: root project enforces dependency hygiene and the module graph for every module
plugins {
    id("fintrack.quality")
    alias(libs.plugins.module.graph.assertion)
}

// ADR-020: empty Android modules may be flagged as "should be a JVM project" until their first Android file lands.
// Remove a module from this list in the task that adds its first Android code.
val emptyAndroidModules =
    listOf(
        ":core:testing",
        ":feature:budget",
        ":feature:credit",
        ":feature:inbox",
        ":feature:investment",
        ":feature:loan",
        ":feature:lock",
        ":feature:statistics",
    )

dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("fail")
            }
        }
        emptyAndroidModules.forEach { modulePath ->
            project(modulePath) {
                onModuleStructure {
                    severity("ignore")
                }
            }
        }
    }
}

moduleGraphAssert {
    restricted =
        arrayOf(
            // DOC-03 rule 1: feature modules never depend on other feature modules
            ":feature:.* -X> :feature:.*",
            // DOC-03 rule 2: feature modules never depend on data, database or datastore
            ":feature:.* -X> :core:(data|database|datastore)",
        )
}

tasks.named("check") {
    dependsOn("buildHealth", "assertModuleGraph")
}
