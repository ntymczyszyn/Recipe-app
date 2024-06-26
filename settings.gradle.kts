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
        maven { url("https://jitpack.io") }
    }
}

rootProject.name = "RecipeApp"
include(":app")

fun MavenArtifactRepository.url(s: String): MavenArtifactRepository? {
    setUrl(s)
    return this
}
