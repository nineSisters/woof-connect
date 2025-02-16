plugins {
    id("build-jvm")
    alias(libs.plugins.ktor)
}

application {
    mainClass = "ru.nnsh.woof_connect.ApplicationKt"
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.websockets)
    implementation(libs.kotlinx.datetime)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.client.websockets)
    testImplementation(testFixtures(projects.woofConnectApiV1))

    implementation(projects.woofConnectCommon)
    implementation(projects.woofConnectApiV1)
    implementation(projects.woofConnectLogger)
    implementation(projects.woofConnectBusinessLogic)
    implementation(projects.woofConnectRepositoryInMemory)
}

tasks.test {
    useJUnitPlatform()
}
