plugins {
    id("build-jvm")
}

group = "ru.nnsh.woof-connect"

dependencies {

    implementation(projects.woofConnectCommon)
    api(kotlin("test"))
    api(kotlin("test-junit"))
    api(libs.kotlinx.coroutines.test)
    api(libs.test.jupiterApi)
}

tasks.test {
    useJUnitPlatform()
}
