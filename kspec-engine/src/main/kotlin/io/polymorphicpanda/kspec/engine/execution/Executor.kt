package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.context.Context

/**
 * @author Ranie Jade Ramiso
 */
interface Executor {
    fun execute(context: Context, chain: ExecutorChain)
}
