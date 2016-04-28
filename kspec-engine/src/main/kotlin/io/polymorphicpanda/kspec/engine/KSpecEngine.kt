package io.polymorphicpanda.kspec.engine

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.Utils
import io.polymorphicpanda.kspec.annotation.Configurations
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryRequest
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryResult
import io.polymorphicpanda.kspec.engine.execution.ContextExecutionChain
import io.polymorphicpanda.kspec.engine.execution.ExecutionListener
import io.polymorphicpanda.kspec.engine.execution.ExecutionRequest
import io.polymorphicpanda.kspec.engine.execution.Filter
import io.polymorphicpanda.kspec.extension.Configuration
import io.polymorphicpanda.kspec.filter.Focused
import io.polymorphicpanda.kspec.hook.AroundHook
import io.polymorphicpanda.kspec.hook.Chain
import io.polymorphicpanda.kspec.pending.Pending
import java.util.*
import kotlin.reflect.KClass

/**
 * @author Ranie Jade Ramiso
 */
class KSpecEngine {
    private val listeners = LinkedList<ExecutionListener>()

    fun discover(discoveryRequest: DiscoveryRequest): DiscoveryResult {
        val instances = LinkedList<KSpec>()

        discoveryRequest.specs.forEach {
            val instance = Utils.instantiateUsingNoArgConstructor(it)
            discover(instance)
            instances.add(instance)
        }

        return DiscoveryResult(instances)
    }

    fun execute(discoveryResult: DiscoveryResult) {
        execute(ExecutionRequest(KSpecConfig(), discoveryResult))
    }

    fun execute(executionRequest: ExecutionRequest) {
        notifyExecutionStarted()
        val chain = ContextExecutionChain()

        // match filter
        chain.add { context, filter, chain ->
            if (!filter.config.filter.match.isEmpty() && filter.hasAnyMatch()) {
                if (!filter.matchesMatchFilter(context)) {
                    notifyContextIgnored(context)
                    return@add
                }
            }
            chain.next(context, filter)
        }

        // include filter
        chain.add { context, filter, chain ->
            if (filter.config.filter.includes.isEmpty() || filter.matchesIncludeFilter(context)) {
                chain.next(context, filter)
            } else {
                notifyContextIgnored(context)
            }
        }

        // exclude filter
        chain.add { context, filter, chain ->
            if (filter.config.filter.excludes.isEmpty() || !filter.matchesExcludeFilter(context)) {
                chain.next(context, filter)
            } else {
                notifyContextIgnored(context)
            }
        }

        // hooks
        chain.add { context, filter, chain ->
            val config = filter.config
            config.before.filter { it.handles(context) }
                    .forEach { it.execute(context) }

            config.around(matchAll = true) { context, other ->
                chain.next(context, filter)
            }

            val aroundHooks = LinkedList<AroundHook>(
                    config.around.filter { it.handles(context) }
            )

            val exec = object: Chain(aroundHooks) {
                override fun stop(reason: String) {
                    throw UnsupportedOperationException()
                }
            }

            exec.next(context)

            config.after.filter { it.handles(context) }
                    .forEach { it.execute(context) }
        }

        // actual

        chain.add { context, filter, chain ->
            when(context) {
                is ExampleContext -> {
                    notifyExampleStarted(context)

                    try {
                        invokeBeforeEach(context.parent)

                        // ensures that afterEach is still invoke even if the test fails
                        try {
                            context()
                        } catch (e: Throwable) {
                            notifyExampleFailure(context, e)
                        }

                        invokeAfterEach(context.parent)

                        notifyExampleFinished(context)
                    } catch (e: Throwable) {
                        notifyExampleFailure(context, e)
                    }
                }
                is ExampleGroupContext -> {
                    try {
                        context.before?.invoke()
                        notifyExampleGroupStarted(context)

                        context.children.forEach {
                            chain.reset()
                            chain.next(it, filter)
                        }

                        context.after?.invoke()
                        notifyExampleGroupFinished(context)

                    } catch(e: Throwable) {
                        notifyExampleGroupFailure(context, e)
                    }
                }
            }
        }

        executionRequest.discoveryResult.instances.forEach { spec ->
            // apply global configuration
            val config = KSpecConfig()
            config.copy(executionRequest.config)

            // apply shared configurations
            val annotation = Utils.findAnnotation(spec.javaClass.kotlin, Configurations::class)
            if (annotation != null) {
                val configurations = annotation.configurations
                configurations.forEach { it: KClass<out Configuration> ->
                    val configuration = Utils.instantiateUsingNoArgConstructor(it)
                    configuration.apply(config)
                }
            }

            // apply spec configuration
            spec.configure(config)

            // built-in configurations
            applyBuiltInConfigurations(config)

            val filter = Filter(spec.root, config)

            // start the execution chain
            chain.next(spec.root, filter)
        }

        notifyExecutionFinished()
    }

    fun addListener(listener: ExecutionListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ExecutionListener) {
        listeners.remove(listener)
    }

    fun clearListeners() {
        listeners.clear()
    }

    private fun applyBuiltInConfigurations(config: KSpecConfig) {
        Pending.apply(config)
        Focused.apply(config)
    }

    private fun discover(spec: KSpec) {
        spec.spec()
    }

    private fun notifyExampleGroupStarted(group: ExampleGroupContext) {
        listeners.forEach { it.exampleGroupStarted(group) }
    }

    private fun notifyExampleGroupFailure(group: ExampleGroupContext, e: Throwable) {
        listeners.forEach { it.exampleGroupFailure(group, e) }
    }

    private fun notifyExampleGroupFinished(group: ExampleGroupContext) {
        listeners.forEach { it.exampleGroupFinished(group) }
    }

    private fun notifyExampleStarted(example: ExampleContext) {
        listeners.forEach { it.exampleStarted(example) }
    }

    private fun notifyExampleFailure(example: ExampleContext, e: Throwable) {
        listeners.forEach { it.exampleFailure(example, e) }
    }

    private fun notifyExampleFinished(example: ExampleContext) {
        listeners.forEach { it.exampleFinished(example) }
    }

    private fun notifyExecutionStarted() {
        listeners.forEach { it.executionStarted() }
    }

    private fun notifyExecutionFinished() {
        listeners.forEach { it.executionFinished() }
    }

    private fun notifyContextIgnored(context: Context) {
        when(context) {
            is ExampleGroupContext -> notifyExampleGroupIgnored(context)
            is ExampleContext -> notifyExampleIgnored(context)
        }
    }

    private fun notifyExampleGroupIgnored(group: ExampleGroupContext) {
        listeners.forEach { it.exampleGroupIgnored(group) }
    }

    private fun notifyExampleIgnored(example: ExampleContext) {
        listeners.forEach { it.exampleIgnored(example) }
    }


    private fun invokeBeforeEach(context: ExampleGroupContext) {
        if (context.parent != null) {
            invokeBeforeEach(context.parent!!)
        }
        context.beforeEach?.invoke()
    }

    private fun invokeAfterEach(context: ExampleGroupContext) {
        context.afterEach?.invoke()
        if (context.parent != null) {
            invokeAfterEach(context.parent!!)
        }
    }
}
