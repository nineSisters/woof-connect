package ru.nnsh.cor

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.nanashi.ru.nnsh.cor.impl.chain
import ru.nanashi.ru.nnsh.cor.impl.rootChain
import ru.nanashi.ru.nnsh.cor.impl.rootWorker
import ru.nanashi.ru.nnsh.cor.impl.worker
import ru.nnsh.cor.mock.CorStatuses
import ru.nnsh.cor.mock.MockContext
import kotlin.test.assertEquals

class CorTest {

    @Test
    fun testRootWorker() = runTest {
        val ctx = MockContext(CorStatuses.None, 42)
        val chain = buildWorker().build()
        chain.execute(ctx)
        assertEquals(ctx.int, 43)
    }

    @Test
    fun testRootChain() = runTest {
        val ctx = MockContext(CorStatuses.None, 42)
        val chain = buildChain()
        chain.build().execute(ctx)
        assertEquals(ctx.int, 43)
        assertEquals(ctx.status, CorStatuses.Running)
    }

    @Test
    fun testNestedChain() = runTest {
        val ctx = MockContext(CorStatuses.None, 42)
        val chain = buildNestedChain()
        chain.build().execute(ctx)
        assertEquals(ctx.int, 45)
        assertEquals(ctx.text, "Hello from nested chain")
        assertEquals(ctx.status, CorStatuses.Running)
    }

    private fun buildWorker() = rootWorker<MockContext> {
        on {
            status == CorStatuses.None
        }
        doWork {
            int = 43
        }
    }

    private fun buildChain() = rootChain<MockContext> {
        on {
            status == CorStatuses.None
        }
        add(buildWorker())
        worker {
            doWork {
                status = CorStatuses.Running
            }
        }
    }

    private fun buildNestedChain() = rootChain<MockContext> {
        on {
            status == CorStatuses.None
        }
        add(buildChain())
        chain {
            title = "Second chain"
            on { status == CorStatuses.Running }
            worker {
                title = "Worker 1"
                on { int == 43 }
                doWork {
                    int += 1
                }
            }
            chain {
                title = "Chain in second chain"
                worker {
                    title = "Worker 2"
                    doWork { text = "Hello from nested chain" }
                }
            }
            worker {
                title = "Worker 3"
                doWork { int += 1 }
            }
        }
    }
}