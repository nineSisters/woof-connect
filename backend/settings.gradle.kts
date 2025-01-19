@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
pluginManagement {
    includeBuild("../build-plugin")
    includeBuild("../libs")
    plugins {
        id("build-jvm") apply false
    }
}

rootProject.name = "backend"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":woof-connect-api-v1",
    ":woof-connect-common",
    ":woof-connect-ktor-app",
    ":woof-connect-logger",
)