package com.urdzik.convention

import org.gradle.api.JavaVersion

internal object CoreVersion {
    const val minSdk = 26
    const val compileSdk = 34
    // Update billing lib if increment
    const val targetSdk = 34

    const val versionCode = 1
    const val versionName = "1.0.0"

    const val languageVersion = "2.0"
    const val apiVersion = languageVersion

    val javaVersion = JavaVersion.VERSION_17
}