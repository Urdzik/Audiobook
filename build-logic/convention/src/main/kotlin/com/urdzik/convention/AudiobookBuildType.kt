package com.urdzik.convention

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
@Suppress("unused")
enum class AudiobookBuildType(val applicationIdSuffix: String? = null) {
    DEBUG,
    RELEASE,
}
