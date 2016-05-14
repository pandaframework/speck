package io.polymorphicpanda.kspec.launcher.reporter

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.engine.execution.ExecutionResult

/**
 * @author Ranie Jade Ramiso
 */
open class ReporterAdapter: Reporter {
    override fun executionStarted() { }

    override fun executionFinished() { }

    override fun exampleGroupFinished(group: ExampleGroupContext, result: ExecutionResult) { }

    override fun exampleFinished(example: ExampleContext, result: ExecutionResult) { }

    override fun exampleGroupIgnored(group: ExampleGroupContext) { }

    override fun exampleIgnored(example: ExampleContext) { }
}
