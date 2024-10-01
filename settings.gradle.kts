pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

buildCache {
    local {
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Audiobook"
include(":app")
include(":core:ui")
include(
    ":core:database:contract",
    ":core:database:impl"
)
include(
    ":feature:player:data",
    ":feature:player:domain",
    ":feature:player:presentation"
)
include(":core:common")
include(":core:resourses")
include(":core:api:impl")
include(":core:api:contract")
