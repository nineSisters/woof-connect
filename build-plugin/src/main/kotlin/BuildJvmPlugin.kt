package ru.nanashi.woof_connect

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

class BuildJvmPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(libs.plugins.kotlin.jvm.get().pluginId)
            group = rootProject.group
            version = rootProject.version
            repositories {
                mavenCentral()
            }
            kotlinOptions {
                jvmToolchain(libs.versions.jvmToolchain.get().toInt())
            }
        }
    }
}