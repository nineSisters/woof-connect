package ru.nanashi.ru.nnsh.cor.dsl

import ru.nanashi.ru.nnsh.cor.contract.ICorExecutable

@DslMarker
annotation class CorDsl

interface ICorConfigurable<C> {
    val configuration: C
}

interface ICorExecDsl<T, C> : ICorConfigurable<C> {
    var title: String
    fun build(): ICorExecutable<T>
}

fun interface ICorOnDsl<T> {
    @CorDsl
    fun on(lambda: suspend T.() -> Boolean)
}

fun interface ICorRaiseDsl<T> {
    @CorDsl
    fun raise(lambda: suspend T.(e: Exception) -> Unit)
}

fun interface ICorDoWorkDsl<T> {
    @CorDsl
    fun doWork(lambda: suspend T.() -> Unit)
}

interface ICorWorkerDsl<T, C> : ICorExecDsl<T, C>, ICorOnDsl<T>, ICorRaiseDsl<T>, ICorDoWorkDsl<T>

interface ICorAddExecDsl<T, C> {
    @CorDsl
    fun add(execDsl: ICorExecDsl<T, C>)
}

interface ICorChainDsl<T, C> : ICorExecDsl<T, C>, ICorOnDsl<T>, ICorRaiseDsl<T>, ICorAddExecDsl<T, C>
