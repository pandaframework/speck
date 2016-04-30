package io.polymorphicpanda.kspec.config

import io.polymorphicpanda.kspec.tag.Tag
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class FilterConfig {
    private val _includes = HashSet<Tag>()
    val includes: Set<Tag> = _includes

    private val _excludes = HashSet<Tag>()
    val excludes: Set<Tag> = _excludes

    private val _match = HashSet<Tag>()
    val match: Set<Tag> = _match


    fun include(vararg tags: Tag) {
        _includes.addAll(tags)
    }

    fun exclude(vararg tags: Tag) {
        _excludes.addAll(tags)
    }

    fun matching(vararg tags: Tag) {
        _match.addAll(tags)
    }

    fun copy(source: FilterConfig) {
        this._includes.addAll(source.includes)
        this._excludes.addAll(source.excludes)
        this._match.addAll(source.match)
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as FilterConfig

        if (includes != other.includes) return false
        if (excludes != other.excludes) return false
        if (match != other.match) return false

        return true
    }

    override fun hashCode(): Int{
        var result = includes.hashCode()
        result += 31 * result + excludes.hashCode()
        result += 31 * result + match.hashCode()
        return result
    }


}
