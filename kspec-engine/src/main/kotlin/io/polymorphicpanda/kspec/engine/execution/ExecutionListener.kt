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
    fun exampleIgnored(example: ExampleContext)
    fun exampleFinished(example: ExampleContext, result: ExecutionResult)

    fun exampleGroupStarted(group: ExampleGroupContext)
    fun exampleGroupIgnored(group: ExampleGroupContext)
    fun exampleGroupFinished(group: ExampleGroupContext, result: ExecutionResult)
}
