plugins {
    id("build-jvm")
}

dependencies {
    implementation(libs.logging.logback.classic)
    api(libs.logging.slf4j.api)
}
