package com.meqlause.fintrack.architecture

import java.io.File

// DOC-06: the Gradle test task runs in the app module, so the repository root is one level up
internal object ProjectPaths {
    val root: File =
        checkNotNull(File(checkNotNull(System.getProperty("user.dir"))).parentFile) {
            "Cannot locate the repository root"
        }
}
