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

rootProject.name = "assignment"
include(":app")
include(":navigation")
include(":shared")
include(":core:theme")
include(":core:network")
include(":core:data:card_repository")
include(":core:navigation")
include(":feature:home")
include(":feature:scratch")
include(":feature:activate")
