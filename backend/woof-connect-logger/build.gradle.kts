plugins {
    id("build-jvm")
}

dependencies {
    implementation(libs.logging.logback.classic)
    implementation(libs.logging.fluentd.appender)
    implementation(libs.logging.fluentd.logger)
    api(libs.logging.slf4j.api)
}
