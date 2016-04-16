package io.polymorphicpanda.kspec.runner

import io.polymorphicpanda.kspec.CoreExtensions
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.hook.Chain

/**
 * @author Ranie Jade Ramiso
 */
class KSpecRunner(val root: ExampleGroupContext, val config: KSpecConfig = KSpecConfig()) {
    fun run(notifier: RunNotifier) {
        CoreExtensions.configure(config, root)

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
