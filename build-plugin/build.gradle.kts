plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("build-jvm") {
            id = "build-jvm"
            implementationClass = "ru.nanashi.woof_connect.BuildJvmPlugin"
        }
        register("build-kmp") {
            id = "build-kmp"
            implementationClass ="ru.nanashi.woof_connect.BuildKmpPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.plugin.kotlin)
}