package io.polymorphicpanda.kspec.hook

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
abstract class Hook(val tags: Set<Tag>, val matchAll: Boolean = false) {
    open fun handles(context: Context): Boolean {
        return matchAll || context.tags.containsAll(tags)
    }
}
