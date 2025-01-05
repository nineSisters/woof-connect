package ru.nanashi.woof_connect

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun Project.kotlinOptions(block: KotlinJvmProjectExtension.() -> Unit) {
    the<KotlinJvmProjectExtension>().block()
}

internal val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()
