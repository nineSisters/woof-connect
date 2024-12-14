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
    plugins {
        id("build-jvm") apply false
    }
}

rootProject.name = "backend"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")