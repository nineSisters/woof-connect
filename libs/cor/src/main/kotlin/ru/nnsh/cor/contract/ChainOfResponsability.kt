package ru.nanashi.ru.nnsh.cor.contract

interface ICorExecutable<T> {
    suspend fun execute(context: T)
}

interface ICorWorker<T> : ICorExecutable<T> {
    val title: String

    suspend fun on(context: T): Boolean
    suspend fun raise(context: T, e: Exception)
    suspend fun doWork(context: T)

    override suspend fun execute(context: T) {
        if (on(context)) {
            try {
                doWork(context)
            } catch (e: Exception) {
                raise(context, e)
            }
        }
    }
}