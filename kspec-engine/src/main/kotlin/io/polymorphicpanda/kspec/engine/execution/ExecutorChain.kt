package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.engine.Filter
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class ExecutorChain {
    private val blocks = LinkedList<(Context, Filter, ExecutorChain) -> Unit>()
    private val executors = LinkedList<Executor>()
    private var iterator: Iterator<Executor>? = null

    fun add(executor: Executor) {
        executors.add(executor)
    }

    fun next(context: Context) {
        if (iterator == null) {
            iterator = executors.iterator()
        }
        iterator!!.next().execute(context, this)
    }

    fun reset() {
        iterator = null
    }

    operator fun Executor.unaryPlus() {
        add(this)
    }
}
