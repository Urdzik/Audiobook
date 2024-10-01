package com.urdzik.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType

internal val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.libsDependency(alias: String): Provider<MinimalExternalModuleDependency> {
    return libs.findLibrary(alias).get()
}

internal fun DependencyHandlerScope.implementation(dependency: Any) {
    add("implementation", dependency)
}

internal fun DependencyHandlerScope.kapt(dependency: Any) {
    add("kapt", dependency)
}

internal fun DependencyHandlerScope.ksp(dependency: Any) {
    add("ksp", dependency)
}

internal fun DependencyHandlerScope.testImplementation(dependency: Any) {
    add("testImplementation", dependency)
}

internal fun DependencyHandlerScope.testRuntimeOnly(dependency: Any) {
    add("testRuntimeOnly", dependency)
}

