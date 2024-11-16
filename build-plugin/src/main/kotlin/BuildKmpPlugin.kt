package ru.nanashi.woof_connect

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class BuildKmpPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        group = rootProject.group
        version = rootProject.version

        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            extensions.configure<KotlinMultiplatformExtension> {
                configureTargets(this@with)
                sourceSets.configureEach {
                    languageSettings.apply {
                        languageVersion = "2.0"
                        progressiveMode = true
                        optIn("kotlin.time.ExperimentalTime")
                    }
                }
            }
        }
        repositories {
            mavenCentral()
        }
    }
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
@Suppress("LongMethod", "MagicNumber")
private fun KotlinMultiplatformExtension.configureTargets(project: Project) {
    val libs = project.the<LibrariesForLibs>()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmToolchain.get()))
        vendor.set(JvmVendorSpec.AZUL)
    }

    jvm {
        this.mainRun { mainClass.set("TestKmpKt")  }
        compilations.configureEach {
            this.compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmToolchain.get()))
                }
            }
        }

    }
    linuxX64()
    project.tasks.withType(JavaCompile::class.java) {
        sourceCompatibility = libs.versions.jvmToolchain.get()
        targetCompatibility = libs.versions.jvmToolchain.get()
    }
}
