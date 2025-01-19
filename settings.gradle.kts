plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "woof-connect"

includeBuild("build-plugin")
includeBuild("backend")
includeBuild("libs")
