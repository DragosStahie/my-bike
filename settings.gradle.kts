pluginManagement {
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

rootProject.name = "My Bike"
include(":app")
include(":data")
include(":common")
include(":feature:startup")
include(":feature:bikes")
include(":navigation")
include(":feature:rides")
