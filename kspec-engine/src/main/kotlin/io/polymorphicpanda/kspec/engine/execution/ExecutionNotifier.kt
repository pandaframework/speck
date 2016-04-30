package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class ExecutionNotifier {
     val listeners = LinkedList<ExecutionListener>()

    fun addListener(listener: ExecutionListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ExecutionListener) {
        listeners.remove(listener)
    }

    fun clearListeners() {
        listeners.clear()
    }

     fun notifyExampleGroupStarted(group: ExampleGroupContext) {
        listeners.forEach { it.exampleGroupStarted(group) }
    }

     fun notifyExampleGroupFailure(group: ExampleGroupContext, e: Throwable) {
        listeners.forEach { it.exampleGroupFailure(group, e) }
    }

     fun notifyExampleGroupFinished(group: ExampleGroupContext) {
        listeners.forEach { it.exampleGroupFinished(group) }
    }

     fun notifyExampleStarted(example: ExampleContext) {
        listeners.forEach { it.exampleStarted(example) }
    }

     fun notifyExampleFailure(example: ExampleContext, e: Throwable) {
        listeners.forEach { it.exampleFailure(example, e) }
    }

     fun notifyExampleFinished(example: ExampleContext) {
        listeners.forEach { it.exampleFinished(example) }
    }

     fun notifyExecutionStarted() {
        listeners.forEach { it.executionStarted() }
    }

     fun notifyExecutionFinished() {
        listeners.forEach { it.executionFinished() }
    }

     fun notifyExampleGroupIgnored(group: ExampleGroupContext) {
        listeners.forEach { it.exampleGroupIgnored(group) }
    }

     fun notifyExampleIgnored(example: ExampleContext) {
        listeners.forEach { it.exampleIgnored(example) }
    }

}
