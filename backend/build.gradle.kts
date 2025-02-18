plugins {
    id("build-jvm")
    alias(libs.plugins.docker.compose)
}

group = "ru.nnsh.woof-connect"
version = "0.1"

dockerCompose {
    useComposeFiles.add("../deploy/docker-compose.yml")
    isRequiredBy(tasks.test)
}
