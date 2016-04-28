package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext

/**
 * @author Ranie Jade Ramiso
 */
interface ExecutionListener {
    fun executionStarted()
    fun executionFinished()

    fun exampleStarted(example: ExampleContext)
    fun exampleFailure(example: ExampleContext, throwable: Throwable)
    fun exampleIgnored(example: ExampleContext)
    fun exampleFinished(example: ExampleContext)

    fun exampleGroupStarted(group: ExampleGroupContext)
    fun exampleGroupFailure(group: ExampleGroupContext, throwable: Throwable)
    fun exampleGroupIgnored(group: ExampleGroupContext)
    fun exampleGroupFinished(group: ExampleGroupContext)
}
