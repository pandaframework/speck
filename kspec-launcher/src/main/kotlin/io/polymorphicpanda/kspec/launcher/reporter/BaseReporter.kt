package io.polymorphicpanda.kspec.launcher.reporter

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.engine.execution.ExecutionResult
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Ranie Jade Ramiso
 */
open class BaseReporter: ReporterAdapter() {
    private val exampleGroupSuccessCounter = AtomicInteger()
    private val exampleSuccessCounter = AtomicInteger()

    private val exampleGroupFailureCounter = AtomicInteger()
    private val exampleFailureCounter = AtomicInteger()

    private val exampleGroupIgnoredCounter = AtomicInteger()
    private val exampleIgnoredCounter = AtomicInteger()

    val exampleGroupFailureCount: Int
        get() = exampleGroupFailureCounter.toInt()

    val exampleFailureCount: Int
        get() = exampleFailureCounter.toInt()

    val exampleGroupSuccessCount: Int
        get() = exampleGroupSuccessCounter.toInt()

    val exampleSuccessCount: Int
        get() = exampleSuccessCounter.toInt()

    val exampleGroupIgnoredCount: Int
        get() = exampleGroupIgnoredCounter.toInt()

    val exampleIgnoredCount: Int
        get() = exampleIgnoredCounter.toInt()

    val totalFailureCount: Int
        get() = exampleGroupFailureCount + exampleFailureCount

    val totalSuccessCount: Int
        get() = exampleGroupSuccessCount + exampleSuccessCount

    val totalIgnoredCount: Int
        get() = exampleGroupIgnoredCount + exampleIgnoredCount


    override fun exampleGroupFinished(group: ExampleGroupContext, result: ExecutionResult) {
        super.exampleGroupFinished(group, result)
        if (result is ExecutionResult.Failure) {
            exampleGroupFailureCounter.andIncrement
        } else {
            exampleGroupSuccessCounter.andIncrement
        }
    }

    override fun exampleFinished(example: ExampleContext, result: ExecutionResult) {
        super.exampleFinished(example, result)
        if (result is ExecutionResult.Failure) {
            exampleFailureCounter.andIncrement
        } else {
            exampleSuccessCounter.andIncrement
        }
    }

    override fun exampleGroupIgnored(group: ExampleGroupContext) {
        super.exampleGroupIgnored(group)
        exampleGroupIgnoredCounter.andIncrement
    }

    override fun exampleIgnored(example: ExampleContext) {
        super.exampleIgnored(example)
        exampleIgnoredCounter.andIncrement
    }

    override fun executionStarted() {
        super.executionStarted()
        exampleGroupSuccessCounter.set(0)
        exampleSuccessCounter.set(0)
        exampleGroupFailureCounter.set(0)
        exampleFailureCounter.set(0)
        exampleGroupIgnoredCounter.set(0)
        exampleIgnoredCounter.set(0)
    }
}
