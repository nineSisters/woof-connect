plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)

    implementation(libs.kotlinx.coroutines.core)
}

tasks.test {
    useJUnitPlatform()
}