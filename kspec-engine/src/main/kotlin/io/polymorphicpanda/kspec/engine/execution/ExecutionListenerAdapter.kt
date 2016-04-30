package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext

/**
 * @author Ranie Jade Ramiso
 */
open class ExecutionListenerAdapter: ExecutionListener {
    override fun executionStarted() { }

    override fun executionFinished() { }

    override fun exampleStarted(example: ExampleContext) { }

    override fun exampleFailure(example: ExampleContext, throwable: Throwable) { }

    override fun exampleIgnored(example: ExampleContext) { }

    override fun exampleFinished(example: ExampleContext) { }

    override fun exampleGroupStarted(group: ExampleGroupContext) { }

    override fun exampleGroupFailure(group: ExampleGroupContext, throwable: Throwable) { }

    override fun exampleGroupIgnored(group: ExampleGroupContext) { }

    override fun exampleGroupFinished(group: ExampleGroupContext) { }
}
