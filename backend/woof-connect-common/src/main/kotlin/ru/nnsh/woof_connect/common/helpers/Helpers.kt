package ru.nnsh.woof_connect.common.helpers

import ru.nnsh.woof_connect.common.WfcContext
import ru.nnsh.woof_connect.common.WfcError
import ru.nnsh.woof_connect.common.WfcState

fun WfcContext.fail(
    code: Int,
    field: String? = null,
    group: String? = null,
    message: String? = null,
    cause: Throwable? = null
) {
    state = WfcState.FAILING
    error = WfcError(code, field, group, message, cause)
}
fun WfcContext.fail(
    error: WfcError,
) {
    state = WfcState.FAILING
    this.error = error
}
