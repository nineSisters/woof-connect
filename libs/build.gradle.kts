plugins {
    alias(libs.plugins.kotlin.jvm) apply  false
}

group = "ru.nnsh.woof-connect"
version = "0.1"

subprojects {
    group = rootProject.group
    version = rootProject.version
}
