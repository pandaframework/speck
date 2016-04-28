package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.context.Context
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class ContextExecutionChain {
    private val blocks = LinkedList<(Context, Filter, ContextExecutionChain) -> Unit>()
    private var iterator: Iterator<(Context, Filter, ContextExecutionChain) -> Unit>? = null

    fun add(block: (Context, Filter, ContextExecutionChain) -> Unit) {
        blocks.add(block)
    }

    fun next(context: Context, filter: Filter) {
        if (iterator == null) {
            iterator = blocks.iterator()
        }
        iterator!!.next().invoke(context, filter, this)
    }

    fun reset() {
        iterator = null
    }
}
