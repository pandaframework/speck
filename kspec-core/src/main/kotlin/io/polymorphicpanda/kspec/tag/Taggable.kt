package io.polymorphicpanda.kspec.tag

import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
open class Taggable(private val parent: Taggable?, tags: Set<Tag>) {
    val tags = HashSet<Tag>(tags)

    fun contains(tags: Collection<Tag>): Boolean {
        return this.tags.any { tags.contains(it) }
                || parent?.contains(tags) ?: false
    }

    fun contains(vararg tags: Tag) = contains(setOf(*tags))

    operator fun get(tag: String): Tag? {
        var result = tags.firstOrNull { it.name == tag }

        if (result == null) {
            result = parent?.get(tag)
        }
        return result
    }
}
