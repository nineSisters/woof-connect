plugins {
    id("build-jvm")
    id("ch.acanda.gradle.fabrikt") version "1.8.0"
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

