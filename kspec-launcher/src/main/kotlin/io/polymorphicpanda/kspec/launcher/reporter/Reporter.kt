package io.polymorphicpanda.kspec.launcher.reporter

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext

/**
 * @author Ranie Jade Ramiso
 */
interface Reporter {
    fun executionStarted()
    fun executionFinished()

    fun exampleGroupSuccess(group: ExampleGroupContext)
    fun exampleSuccess(example: ExampleContext)

    fun exampleGroupFailure(group: ExampleGroupContext, reason: Throwable)
    fun exampleFailure(example: ExampleContext, reason: Throwable)

    fun exampleGroupIgnored(group: ExampleGroupContext)
    fun exampleIgnored(example: ExampleContext)
}
