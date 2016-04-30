package io.polymorphicpanda.kspec.engine

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import io.polymorphicpanda.kspec.engine.execution.Executor

/**
 * @author Ranie Jade Ramiso
 */
abstract class FilterExecutor(val filter: Filter, val notifier: ExecutionNotifier): Executor {
    protected fun notifyContextIgnored(context: Context) {
        when(context) {
            is ExampleGroupContext -> notifier.notifyExampleGroupIgnored(context)
            is ExampleContext -> notifier.notifyExampleIgnored(context)
        }
    }
}
