package io.polymorphicpanda.kspec.console.reporter

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.launcher.reporter.BaseReporter
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Ranie Jade Ramiso
 */
class ConsoleReporter(val out: PrintStream = System.out,
                      val err: PrintStream = System.err): BaseReporter() {

    private val counter = AtomicInteger()

    override fun exampleGroupSuccess(group: ExampleGroupContext) {
        super.exampleGroupSuccess(group)
        updateStatusLine()
    }

    override fun exampleSuccess(example: ExampleContext) {
        super.exampleSuccess(example)
        updateStatusLine()
        counter.andIncrement
    }

    override fun exampleGroupFailure(group: ExampleGroupContext, reason: Throwable) {
        super.exampleGroupFailure(group, reason)
        updateStatusLine()
    }

    override fun exampleFailure(example: ExampleContext, reason: Throwable) {
        super.exampleFailure(example, reason)
        updateStatusLine()
    }

    override fun exampleGroupIgnored(group: ExampleGroupContext) {
        super.exampleGroupIgnored(group)
        updateStatusLine()
    }

    override fun exampleIgnored(example: ExampleContext) {
        super.exampleIgnored(example)
        updateStatusLine()
    }

    override fun executionFinished() {
        super.executionFinished()
        out.println()
    }

    private fun updateStatusLine() {
        val status = "\u001B[1m> ${counter.toInt()} spec(s) completed, " +
            "${totalFailureCount.toInt()} failed, ${totalIgnoredCount.toInt()} ignored."
        out.print("\r$status")
    }
}
