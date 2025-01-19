package ru.nanashi.ru.nnsh.cor.impl

import ru.nanashi.ru.nnsh.cor.contract.ICorExecutable
import ru.nanashi.ru.nnsh.cor.contract.ICorWorker
import ru.nanashi.ru.nnsh.cor.dsl.CorDsl
import ru.nanashi.ru.nnsh.cor.dsl.ICorChainDsl
import ru.nanashi.ru.nnsh.cor.dsl.ICorExecDsl
import ru.nanashi.ru.nnsh.cor.dsl.ICorWorkerDsl

internal class CorChain<T>(
    override val title: String,
    private val onLambda: suspend T.() -> Boolean,
    private val raiseLambda: suspend T.(e: Exception) -> Unit,
    private val workers: List<ICorExecutable<T>>,
) : ICorWorker<T> {

    override suspend fun on(context: T): Boolean = context.onLambda()
    override suspend fun raise(context: T, e: Exception) = context.raiseLambda(e)
    override suspend fun doWork(context: T) {
        workers.forEach { it.execute(context) }
    }
}

@CorDsl
internal class CorChainDsl<T, C>(
    override val configuration: C,
    override var title: String = "",
) : ICorChainDsl<T, C> {

    override fun build(): ICorExecutable<T> {
        return CorChain(title, onLambda, raiseLambda, executables.map { it.build() })
    }

    private val executables = mutableListOf<ICorExecDsl<T, C>>()

    override fun add(execDsl: ICorExecDsl<T, C>) {
        executables.add(execDsl)
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
}

@CorDsl
fun <T, C> ICorChainDsl<T, C>.chain(block: ICorChainDsl<T, C>.() -> Unit) = add(CorChainDsl<T, C>(configuration).apply(block))

@CorDsl
fun <T, C> ICorChainDsl<T, C>.worker(block: ICorWorkerDsl<T, C>.() -> Unit) = add(CorWorkerDsl<T, C>(configuration).apply(block))

@CorDsl
fun <T> rootChain(
    block: ICorChainDsl<T, Unit>.() -> Unit
): ICorChainDsl<T, Unit> = CorChainDsl<T, Unit>(Unit).apply(block)

@CorDsl
fun <T, C> rootChain(
    configuration: C,
    block: ICorChainDsl<T, C>.() -> Unit
): ICorChainDsl<T, C> = CorChainDsl<T, C>(configuration).apply(block)