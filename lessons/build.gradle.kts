plugins {
    alias(libs.plugins.kotlin.jvm) apply  false
}

group = "ru.nanashi"
version = "1.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version
}
