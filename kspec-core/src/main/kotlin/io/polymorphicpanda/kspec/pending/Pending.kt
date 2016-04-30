package io.polymorphicpanda.kspec.pending

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.Configuration
import io.polymorphicpanda.kspec.tag.Ignored
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
object Pending: Configuration {
    const val TAG_NAME = "pending"

    fun tag(reason: String = ""): Tag {
        return Ignored(TAG_NAME, reason)
    }

    override fun apply(config: KSpecConfig) {
        config.filter.exclude(tag())
    }
}
