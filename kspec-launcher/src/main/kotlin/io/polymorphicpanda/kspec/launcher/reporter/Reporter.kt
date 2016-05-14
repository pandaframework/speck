package io.polymorphicpanda.kspec.launcher.reporter

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.engine.execution.ExecutionResult

/**
 * @author Ranie Jade Ramiso
 */
interface Reporter {
    fun executionStarted()
    fun executionFinished()

    fun exampleGroupFinished(group: ExampleGroupContext, result: ExecutionResult)
    fun exampleFinished(example: ExampleContext, result: ExecutionResult)

    fun exampleGroupIgnored(group: ExampleGroupContext)
    fun exampleIgnored(example: ExampleContext)
}
