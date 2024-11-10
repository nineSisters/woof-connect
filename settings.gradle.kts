@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
rootProject.name = "ru.nnsh.woof_connect"

includeBuild("build-plugin")
includeBuild("backend")
includeBuild("lessons")
