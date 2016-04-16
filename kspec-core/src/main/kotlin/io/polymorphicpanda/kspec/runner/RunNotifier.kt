package io.polymorphicpanda.kspec.runner

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class RunNotifier {
    private val _listeners = LinkedList<RunListener>()

    val listeners: List<RunListener> = _listeners

    fun addListener(listener: RunListener) {
        _listeners.add(listener)
    }

    fun removeListener(listener: RunListener) {
        _listeners.remove(listener)
    }

    fun notifyExampleStarted(example: ExampleContext) {
        listeners.forEach {
            it.exampleStarted(example)
        }
    }

    fun notifyExampleFailure(example: ExampleContext, failure: Throwable) {
        listeners.forEach {
            it.exampleFailure(example, failure)
        }
    }

    fun notifyExampleFinished(example: ExampleContext) {
        listeners.forEach {
            it.exampleFinished(example)
        }
    }

    fun notifyExampleIgnored(example: ExampleContext) {
        listeners.forEach {
            it.exampleIgnored(example)
        }
    }

    fun notifyExampleGroupStarted(group: ExampleGroupContext) {
        listeners.forEach {
            it.exampleGroupStarted(group)
        }
    }

    fun notifyExampleGroupFailure(group: ExampleGroupContext, failure: Throwable) {
        listeners.forEach {
            it.exampleGroupFailure(group, failure)
        }
    }

    fun notifyExampleGroupFinished(group: ExampleGroupContext) {
        listeners.forEach {
            it.exampleGroupFinished(group)
        }
    }

    fun notifyExampleGroupIgnored(group: ExampleGroupContext) {
        listeners.forEach {
            it.exampleGroupIgnored(group)
        }
    }
}
