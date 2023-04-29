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
include(":feature:editdog")
include(":feature:doglist")
include(":common:doginput")
include(":common:camera")
include(":common:navigation")
include(":common:ui")
include(":core:domain")
include(":core:data")
include(":core:db")
include(":core:test")
include(":feature:settings")
