package io.polymorphicpanda.kspec.hook

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
abstract class Hook(val tags: Set<Tag>) {
    open fun handles(context: Context): Boolean {
        return tags.isEmpty() || context.tags.containsAll(tags)
    }
}
