package ru.nnsh.woof_connect.common

class WfcError(
    val code: Int,
    val field: String? = null,
    val group: String? = null,
    val message: String? = null,
    val cause: Throwable? = null
) {
    constructor(e: Exception): this(-1, message = e.toString(), cause = e)
}