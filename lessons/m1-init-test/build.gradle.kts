plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}