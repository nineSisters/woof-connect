plugins {
    id("build-jvm")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.fabrikt)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

fabrikt {
    generate("dog-profile-api-v1") {
        apiFile = file("../specs/dog-profile-spec-v1.yaml")
        basePackage = "ru.nnsh.woof_connect.api.v1"
        validationLibrary = Javax
        quarkusReflectionConfig = disabled
        model {
            generate = true
            serializationLibrary = Kotlin
            extensibleEnums = false
        }
    }
}
