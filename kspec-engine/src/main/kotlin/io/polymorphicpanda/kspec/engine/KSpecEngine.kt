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
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import io.polymorphicpanda.kspec.engine.execution.ExecutionRequest
import io.polymorphicpanda.kspec.engine.execution.Executor
import io.polymorphicpanda.kspec.engine.execution.ExecutorChain
import io.polymorphicpanda.kspec.Configuration
import io.polymorphicpanda.kspec.filter.Focused
import io.polymorphicpanda.kspec.pending.Pending
import java.util.*
import kotlin.reflect.KClass

/**
 * @author Ranie Jade Ramiso
 */
class KSpecEngine(val notifier: ExecutionNotifier) {
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
        notifier.notifyExecutionStarted()
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

            val filter = Filter(spec.root, config.filter)
            val chain = ExecutorChain().apply {
                + MatchExecutor(filter, notifier)
                + IncludeExecutor(filter, notifier)
                + ExcludeExecutor(filter, notifier)
                + HookExecutor(config, notifier)
                + ActualExecutor()
            }


            // start the execution chain
            chain.next(spec.root)
        }

        notifier.notifyExecutionFinished()
    }

    inner class ActualExecutor: Executor {
        override fun execute(context: Context, chain: ExecutorChain) {
            when(context) {
                is ExampleContext -> {
                    notifier.notifyExampleStarted(context)

                    try {
                        invokeBeforeEach(context.parent)

                        // ensures that afterEach is still invoke even if the test fails
                        try {
                            context()
                        } catch (e: Throwable) {
                            notifier.notifyExampleFailure(context, e)
                        }

                        invokeAfterEach(context.parent)

                        notifier.notifyExampleFinished(context)
                    } catch (e: Throwable) {
                        notifier.notifyExampleFailure(context, e)
                    }
                }
                is ExampleGroupContext -> {
                    try {
                        context.before?.invoke()
                        notifier.notifyExampleGroupStarted(context)

                        context.children.forEach {
                            chain.reset()
                            chain.next(it)
                        }

                        context.after?.invoke()
                        notifier.notifyExampleGroupFinished(context)

                    } catch(e: Throwable) {
                        notifier.notifyExampleGroupFailure(context, e)
                    }
                }
            }
        }

    }

    private fun applyBuiltInConfigurations(config: KSpecConfig) {
        Pending.apply(config)
        Focused.apply(config)
    }

    private fun discover(spec: KSpec) {
        spec.spec()
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
