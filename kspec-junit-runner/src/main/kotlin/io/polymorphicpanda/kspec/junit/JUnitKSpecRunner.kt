package io.polymorphicpanda.kspec.junit

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.runner.KSpecRunner
import io.polymorphicpanda.kspec.runner.RunListener
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier

class JUnitKSpecRunner<T: KSpec>(clazz: Class<T>): Runner() {
    val describer = JUnitTestDescriber()
    val spec = clazz.newInstance()

    val _description by lazy(LazyThreadSafetyMode.NONE) {
        spec.spec()
        spec.root.visit(describer)
        spec.disable()
        describer.contextDescriptions[spec.root]!!
    }

    override fun run(notifier: RunNotifier?) {
        val config = KSpecConfig()
        spec.configure(config)

        val runner = KSpecRunner(spec.root, config)
        val runNotifier = io.polymorphicpanda.kspec.runner.RunNotifier()

        runNotifier.addListener(object: RunListener {
            override fun exampleGroupStarted(group: ExampleGroupContext) { }

            override fun exampleGroupFailure(group: ExampleGroupContext, failure: Throwable) { }

            override fun exampleGroupFinished(group: ExampleGroupContext) { }

            override fun exampleGroupIgnored(group: ExampleGroupContext) { }

            override fun exampleStarted(example: ExampleContext) {
                notifier!!.fireTestStarted(describer.contextDescriptions[example])
            }

            override fun exampleFailure(example: ExampleContext, failure: Throwable) {
                notifier!!.fireTestFailure(Failure(describer.contextDescriptions[example], failure))
            }

            override fun exampleFinished(example: ExampleContext) {
                notifier!!.fireTestFinished(describer.contextDescriptions[example])
            }

            override fun exampleIgnored(example: ExampleContext) {
                notifier!!.fireTestIgnored(describer.contextDescriptions[example])
            }
        })

        runner.run(runNotifier)
    }

    override fun getDescription(): Description = _description
}
