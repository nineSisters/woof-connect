plugins {
    id("build-jvm")
    alias(libs.plugins.docker.compose)
}

group = "ru.nnsh.woof-connect"

dependencies {

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.exposed)
    implementation(libs.db.postgres)
    implementation(projects.woofConnectCommon)
    implementation(libs.db.hikari)

    testImplementation(projects.woofConnectRepositoryTests)
}

tasks.test {
    useJUnitPlatform()
}

dockerCompose {
    useComposeFiles.add("docker-compose-postgres.yml")
    isRequiredBy(tasks.test)
}
