plugins {
    id("build-jvm")
}

group = "ru.nnsh.woof-connect"

dependencies {

    implementation(projects.woofConnectCommon)
    api(kotlin("test"))
    api(kotlin("test-junit5"))
    api(libs.kotlinx.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
}
