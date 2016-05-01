package io.polymorphicpanda.kspec.engine

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import io.polymorphicpanda.kspec.engine.execution.Executor
import io.polymorphicpanda.kspec.engine.execution.ExecutorChain
import io.polymorphicpanda.kspec.hook.AroundHook
import io.polymorphicpanda.kspec.hook.Chain
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class HookExecutor(val config: KSpecConfig, val notifier: ExecutionNotifier): Executor {
    override fun execute(context: Context, chain: ExecutorChain) {
        config.before.filter { it.handles(context) }
                .forEach { it.execute(context) }

        config.around(matchAll = true) { context, other ->
            chain.next(context)
        }

        val aroundHooks = LinkedList<AroundHook>(
                config.around.filter { it.handles(context) }
        )

        val exec = object: Chain(aroundHooks) {
        }

        exec.next(context)

        config.after.filter { it.handles(context) }
                .forEach { it.execute(context) }
    }
}
