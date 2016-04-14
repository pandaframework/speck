package io.polymorphicpanda.kspec.hook

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
abstract class Hook(val tags: Set<Tag>) {
    fun handles(context: Context): Boolean {
        return context.tags.containsAll(tags)
    }
}
