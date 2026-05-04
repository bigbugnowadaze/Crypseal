
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
rootProject.name = "Crypseal"
include(":app")
include(":crypseal-runtime")
include(":crypseal-shell-bridge")
include(":crypseal-guard")
include(":ui")
