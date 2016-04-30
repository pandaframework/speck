package io.polymorphicpanda.kspec.engine

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import io.polymorphicpanda.kspec.engine.execution.ExecutorChain

/**
 * @author Ranie Jade Ramiso
 */
class IncludeExecutor(filter: Filter, notifier: ExecutionNotifier): FilterExecutor(filter, notifier) {
    override fun execute(context: Context, chain: ExecutorChain) {
        if (!filter.hasIncludeFilter() || filter.matchesIncludeFilter(context)) {
            chain.next(context)
        } else {
            notifyContextIgnored(context)
        }
    }
}
