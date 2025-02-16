package ru.nnsh.woof_connect.common

data class WfcError(
    val code: Int,
    val field: String? = null,
    val group: String? = null,
    val message: String? = null,
    val cause: Throwable? = null
) {
    constructor(e: Exception): this(-1, message = e.toString(), cause = e)
    companion object {
        const val CODE_VALIDATION = -2
        const val CODE_DB = -3
        const val CODE_SYSTEM = -4

        val DOG_NOT_FOUND = WfcError(
            code = WfcError.CODE_DB,
            field = "dogId",
            group = "database",
            message = "Dog not found"
        )
    }
}
