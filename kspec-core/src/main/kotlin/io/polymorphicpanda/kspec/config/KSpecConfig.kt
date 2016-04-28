package io.polymorphicpanda.kspec.config

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.hook.AroundHook
import io.polymorphicpanda.kspec.hook.Chain
import io.polymorphicpanda.kspec.hook.SimpleHook
import io.polymorphicpanda.kspec.tag.Tag
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class KSpecConfig {
    private val _before = LinkedList<SimpleHook>()
    val before: List<SimpleHook> = _before

    private val _after = LinkedList<SimpleHook>()
    val after: List<SimpleHook> = _after

    private val _around = LinkedList<AroundHook>()
    val around: List<AroundHook> = _around

    val filter = FilterConfig()

    fun before(vararg tags: Tag, block: (Context) -> Unit) {
        _before.add(SimpleHook(block, setOf(*tags)))
    }

    fun after(vararg tags: Tag, block: (Context) -> Unit) {
        _after.add(SimpleHook(block, setOf(*tags)))
    }

    fun around(matchAll: Boolean = false, vararg tags: Tag, block: (Context, Chain) -> Unit) {
        _around.add(AroundHook(block, setOf(*tags), matchAll))
    }

    fun copy(source: KSpecConfig) {
        filter.copy(source.filter)
        _before.addAll(source.before)
        _after.addAll(source.after)
        _around.addAll(source.around)
    }
}
