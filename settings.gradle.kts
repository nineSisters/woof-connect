@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
rootProject.name = "woof-connect"

includeBuild("build-plugin")
includeBuild("backend")
includeBuild("lessons")
