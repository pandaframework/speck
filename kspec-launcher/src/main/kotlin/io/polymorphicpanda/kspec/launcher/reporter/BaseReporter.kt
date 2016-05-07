package io.polymorphicpanda.kspec.launcher.reporter

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
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


    override fun exampleGroupSuccess(group: ExampleGroupContext) {
        exampleGroupSuccessCounter.andIncrement
    }

    override fun exampleSuccess(example: ExampleContext) {
        exampleSuccessCounter.andIncrement
    }

    override fun exampleGroupFailure(group: ExampleGroupContext, reason: Throwable) {
        exampleGroupFailureCounter.andIncrement
    }

    override fun exampleFailure(example: ExampleContext, reason: Throwable) {
        exampleFailureCounter.andIncrement
    }

    override fun exampleGroupIgnored(group: ExampleGroupContext) {
        exampleGroupIgnoredCounter.andIncrement
    }

    override fun exampleIgnored(example: ExampleContext) {
        exampleIgnoredCounter.andIncrement
    }

    override fun executionStarted() {
        exampleGroupSuccessCounter.set(0)
        exampleSuccessCounter.set(0)
        exampleGroupFailureCounter.set(0)
        exampleFailureCounter.set(0)
        exampleGroupIgnoredCounter.set(0)
        exampleIgnoredCounter.set(0)
    }
}
