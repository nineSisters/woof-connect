package ru.nnsh.woof_connect.postgress

data class PgProperties(
    val host: String = "postgresql",
    val port: Int = 5432,
    val user: String = "woof_user",
    val password: String = "woof_pass",
    val database: String = "woof_dogs_db",
    val schema: String = "public",
    val table: String = "ads",
) {
    val url: String
        get() = "jdbc:postgresql://${host}:${port}/${database}"
}
