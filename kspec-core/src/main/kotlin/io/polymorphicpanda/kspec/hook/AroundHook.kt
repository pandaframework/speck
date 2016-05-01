package io.polymorphicpanda.kspec.hook

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
class AroundHook(val block: (Context, Chain) -> Unit, tags: Set<Tag>): Hook(tags) {
    fun execute(context: Context, chain: Chain) {
        block(context, chain)
    }
}
