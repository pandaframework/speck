package io.polymorphicpanda.kspec.launcher.reporter

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext

/**
 * @author Ranie Jade Ramiso
 */
open class ReporterAdapter: Reporter {
    override fun executionStarted() { }

    override fun executionFinished() { }

    override fun exampleGroupSuccess(group: ExampleGroupContext) { }

    override fun exampleSuccess(example: ExampleContext) { }

    override fun exampleGroupFailure(group: ExampleGroupContext, reason: Throwable) { }

    override fun exampleFailure(example: ExampleContext, reason: Throwable) { }

    override fun exampleGroupIgnored(group: ExampleGroupContext) { }

    override fun exampleIgnored(example: ExampleContext) { }
}
