package io.polymorphicpanda.kspec.runner

import io.polymorphicpanda.kspec.Utils
import io.polymorphicpanda.kspec.annotation.Configurations
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.Configuration
import io.polymorphicpanda.kspec.filter.Filter
import io.polymorphicpanda.kspec.filter.Focused
import io.polymorphicpanda.kspec.hook.Chain
import io.polymorphicpanda.kspec.pending.Pending
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class KSpecRunner(val root: ExampleGroupContext,
                  val configure: (KSpecConfig) -> Unit,
                  val configurations: Configurations? = null) {
    val coreConfigurations = setOf(
            Focused,
            Pending,
            Filter
    )

    fun run(notifier: RunNotifier) {
        val config = KSpecConfig()
        val extensions = LinkedList<Configuration>()

        extensions.add(object: Configuration {
            override fun apply(config: KSpecConfig) {
                configure.invoke(config)
            }

        })

        // apply shared configurations
        if (configurations != null) {
            val configurations = configurations.configurations

            configurations.forEach {
                extensions.add(Utils.instantiateUsingNoArgConstructor(it))
            }
        }

        extensions.addAll(coreConfigurations)

        extensions.forEach { it.apply(config) }

        Runner(notifier, config).execute(root)
    }

    internal class Runner(val notifier: RunNotifier, val config: KSpecConfig) {
        init {
            config.around { context, chain ->
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
                                doExecute(it)
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

        fun execute(root: ExampleGroupContext) {
            doExecute(root)
        }

        private fun doExecute(context: Context) {
            config.before.filter { it.handles(context) }
                    .forEach { it.execute(context) }

            val aroundHooks = config.around.filter { it.handles(context) }
            val chain = object: Chain(aroundHooks) {
                override fun stop(reason: String) {
                    when(context) {
                        is ExampleContext -> notifier.notifyExampleIgnored(context)
                        is ExampleGroupContext -> notifier.notifyExampleGroupIgnored(context)
                    }
                }
            }

            chain.next(context)

            config.after.filter { it.handles(context) }
                    .forEach { it.execute(context) }
        }

        private fun invokeBeforeEach(context: ExampleGroupContext) {
            if (context.parent != null) {
                invokeBeforeEach(context.parent)
            }
            context.beforeEach?.invoke()
        }

        private fun invokeAfterEach(context: ExampleGroupContext) {
            context.afterEach?.invoke()
            if (context.parent != null) {
                invokeAfterEach(context.parent)
            }
        }
    }
}
