package io.polymorphicpanda.kspec.support

import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.runner.RunListener
import java.util.*

class RememberingListener: RunListener {

    val groupStarted = HashMap<String, ExampleGroupContext>()
    val groupFinished = HashMap<String, ExampleGroupContext>()
    val groupFailure = HashMap<String, ExampleGroupContext>()
    val groupIgnored = HashMap<String, ExampleGroupContext>()

    val exampleStarted = HashMap<String, ExampleContext>()
    val exampleFinished = HashMap<String, ExampleContext>()
    val exampleFailure = HashMap<String, ExampleContext>()
    val exampleIgnored = HashMap<String, ExampleContext>()

    override fun exampleStarted(example: ExampleContext) {
        exampleStarted.put(example.description, example)
    }

    override fun exampleFailure(example: ExampleContext, failure: Throwable) {
        exampleFailure.put(example.description, example)
    }

    override fun exampleFinished(example: ExampleContext) {
        exampleFinished.put(example.description, example)
    }

    override fun exampleIgnored(example: ExampleContext) {
        exampleIgnored.put(example.description, example)
    }

    override fun exampleGroupStarted(group: ExampleGroupContext) {
        groupStarted.put(group.description, group)
    }

    override fun exampleGroupFailure(group: ExampleGroupContext, failure: Throwable) {
        groupFailure.put(group.description, group)
    }

    override fun exampleGroupFinished(group: ExampleGroupContext) {
        groupFinished.put(group.description, group)
    }

    override fun exampleGroupIgnored(group: ExampleGroupContext) {
        groupIgnored.put(group.description, group)
    }
}
