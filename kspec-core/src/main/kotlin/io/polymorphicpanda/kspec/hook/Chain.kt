package io.polymorphicpanda.kspec.hook

import io.polymorphicpanda.kspec.context.Context

/**
 * @author Ranie Jade Ramiso
 */
abstract class Chain(val list: List<AroundHook>) {
    var iterator = list.iterator()

    fun next(context: Context) {
        iterator.next().execute(context, this)
    }
}
