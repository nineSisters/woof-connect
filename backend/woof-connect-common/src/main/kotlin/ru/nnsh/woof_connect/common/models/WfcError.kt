package ru.nnsh.woof_connect.common.models

class WfcError(
    val code: Int,
    val field: String? = null,
    val group: String? = null,
    val message: String? = null,
    val cause: Throwable? = null
)