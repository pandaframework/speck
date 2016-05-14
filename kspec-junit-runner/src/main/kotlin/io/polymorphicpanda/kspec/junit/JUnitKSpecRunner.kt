package io.polymorphicpanda.kspec.junit

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.engine.KSpecEngine
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryRequest
import io.polymorphicpanda.kspec.engine.execution.ExecutionListenerAdapter
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import io.polymorphicpanda.kspec.engine.execution.ExecutionResult
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier

class JUnitKSpecRunner<T: KSpec>(val clazz: Class<T>): Runner() {
    val describer = JUnitTestDescriber()
    val executionNotifier = ExecutionNotifier()
    val engine = KSpecEngine(executionNotifier)

    val discoveryResult by lazy(LazyThreadSafetyMode.NONE) {
        engine.discover(DiscoveryRequest(listOf(clazz.kotlin)))
    }

    val _description by lazy(LazyThreadSafetyMode.NONE) {
        val spec = discoveryResult.instances.first()
        spec.root.visit(describer)
        describer.contextDescriptions[spec.root]!!
    }

    override fun run(notifier: RunNotifier) {
        executionNotifier.clearListeners()

        executionNotifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                notifier.fireTestStarted(describer.contextDescriptions[example])
            }


            override fun exampleFinished(example: ExampleContext, result: ExecutionResult) {
                if (result is ExecutionResult.Failure) {
                    notifier.fireTestFailure(Failure(describer.contextDescriptions[example], result.cause))
                } else {
                    notifier.fireTestFinished(describer.contextDescriptions[example])
                }
            }

            override fun exampleIgnored(example: ExampleContext) {
                notifier.fireTestIgnored(describer.contextDescriptions[example])
            }
        })

        engine.execute(discoveryResult)
    }

    override fun getDescription(): Description = _description
}
