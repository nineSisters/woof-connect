package ru.nanashi.woof_connect

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

class BuildJvmPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply(KotlinPlatformJvmPlugin::class.java)
        group = rootProject.group
        version = rootProject.version
        repositories {
            mavenCentral()
        }
    }
}