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
               mavenCentral()
        maven { url = uri("https://jitpack.io") }

    }
}
rootProject.name = "Donna_app"
include(":app")