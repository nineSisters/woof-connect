plugins {
    id("build-jvm")
}

group = "ru.nnsh.woof-connect"

dependencies {

    implementation(libs.persistence.cache4k)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(projects.woofConnectCommon)

    testImplementation(projects.woofConnectRepositoryTests)
}

tasks.test {
    useJUnitPlatform()
}
