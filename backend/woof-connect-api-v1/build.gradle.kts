plugins {
    id("build-jvm")
    alias(libs.plugins.fabrikt)
    id("java-test-fixtures")
}

dependencies {
    implementation(libs.kotlinx.datetime)
    testImplementation(kotlin("test"))

    implementation(projects.woofConnectCommon)
    implementation(libs.serialization.jackson)
}

tasks.test {
    useJUnitPlatform()
}

fabrikt {
    generate("dog-profile-api-v1") {
        apiFile = file("../specs/dog-profile-spec-v1.yaml")
        basePackage = "ru.nnsh.woof_connect.api.v1"
        validationLibrary = NoValidation
        quarkusReflectionConfig = disabled
        model {
            generate = true
            serializationLibrary = Jackson
            extensibleEnums = false
        }
    }
}
