package io.polymorphicpanda.kspec

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
object StandardConfiguration: Configuration {
    val focus = Tag("focus")
    val pending = Tag("pending")

    override fun apply(config: KSpecConfig) {
        config.filter.matching(focus)
        config.filter.exclude(pending)
    }
}
