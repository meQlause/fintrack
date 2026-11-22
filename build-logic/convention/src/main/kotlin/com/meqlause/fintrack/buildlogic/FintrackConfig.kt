package com.meqlause.fintrack.buildlogic

import org.gradle.api.JavaVersion

// DOC-03: one set of SDK levels and JDK for every module; minSdk 26 for java.time and adaptive icons (04)
internal object FintrackConfig {
    const val MIN_SDK: Int = 26
    const val COMPILE_SDK: Int = 37
    const val JDK_VERSION: Int = 21
    val JAVA_VERSION: JavaVersion = JavaVersion.toVersion(JDK_VERSION)
}
