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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "PawCalc"
include(":app")
include(":feature:onboarding")
include(":feature:newdog")
include(":core:ui")

include(":common:doginput")
include(":feature:editdog")
include(":common:camera")
include(":common:date")
