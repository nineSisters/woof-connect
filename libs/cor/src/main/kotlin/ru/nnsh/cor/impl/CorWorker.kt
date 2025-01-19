package ru.nanashi.ru.nnsh.cor.impl

import ru.nanashi.ru.nnsh.cor.contract.ICorExecutable
import ru.nanashi.ru.nnsh.cor.contract.ICorWorker
import ru.nanashi.ru.nnsh.cor.dsl.CorDsl
import ru.nanashi.ru.nnsh.cor.dsl.ICorWorkerDsl

internal class CorWorker<T>(
    override val title: String,
    private val onLambda: suspend T.() -> Boolean,
    private val doWorkLambda: suspend T.() -> Unit,
    private val raiseLambda: suspend T.(e: Exception) -> Unit
) : ICorWorker<T> {

    override suspend fun on(context: T): Boolean = context.onLambda()
    override suspend fun raise(context: T, e: Exception) = context.raiseLambda(e)
    override suspend fun doWork(context: T) = context.doWorkLambda()

}

@CorDsl
internal class CorWorkerDsl<T, C>(
    override val configuration: C,
    override var title: String = "",
) : ICorWorkerDsl<T,C> {
    override fun build(): ICorExecutable<T> {
        return CorWorker(title, onLambda, doWorkLambda, raiseLambda)
    }

    private var onLambda: suspend T.() -> Boolean = { true }
    @CorDsl
    override fun on(lambda: suspend T.() -> Boolean) {
        onLambda = lambda
    }

    private var raiseLambda: suspend T.(e: Exception) -> Unit = {
        throw it
    }
    @CorDsl
    override fun raise(lambda: suspend T.(e: Exception) -> Unit) {
        raiseLambda = lambda
    }

    private var doWorkLambda: suspend T.() -> Unit = {}

    @CorDsl
    override fun doWork(lambda: suspend T.() -> Unit) {
        doWorkLambda = lambda
    }

}

@CorDsl
fun <T> rootWorker(
    block: ICorWorkerDsl<T, Unit>.() -> Unit
): ICorWorkerDsl<T, Unit> = CorWorkerDsl<T, Unit>(Unit).apply(block)

@CorDsl
fun <T, C> rootWorker(
    configuration: C,
    block: ICorWorkerDsl<T, C>.() -> Unit
): ICorWorkerDsl<T, C> = CorWorkerDsl<T, C>(configuration).apply(block)