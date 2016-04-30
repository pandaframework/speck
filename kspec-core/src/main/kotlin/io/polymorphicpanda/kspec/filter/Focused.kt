package io.polymorphicpanda.kspec.filter

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.Configuration
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
object Focused: Configuration {
    val tag = Tag("focus")

    override fun apply(config: KSpecConfig) {
        config.filter.matching(tag)
    }
}
