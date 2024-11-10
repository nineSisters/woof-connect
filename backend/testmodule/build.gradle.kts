plugins {
    id("build-kmp")
}

kotlin {
    linuxX64 {
        binaries {
            executable()
        }
    }
}