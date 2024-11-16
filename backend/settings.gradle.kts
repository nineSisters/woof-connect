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
        id("build-kmp") apply false
    }
}

//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include("testmodule")
