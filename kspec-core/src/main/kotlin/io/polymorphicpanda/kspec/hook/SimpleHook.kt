package io.polymorphicpanda.kspec.hook

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
class SimpleHook(val block: (Context) -> Unit, tags: Set<Tag>): Hook(tags) {
    fun execute(context: Context) {
        block(context)
    }
}
