plugins {
    id("build-jvm")
}

dependencies {
    implementation(projects.woofConnectCommon)
    implementation(projects.woofConnectLogger)
    implementation("ru.nnsh.woof-connect:cor:0.1")

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(projects.woofConnectRepositoryTests)
}

tasks.test {
    useJUnitPlatform()
}
