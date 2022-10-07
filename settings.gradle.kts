pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://packages.johnturkson.com/maven")
    }
}

rootProject.name = "Sync"
include(":common")
include(":android")
include(":server")
include(":infrastructure")
