package io.polymorphicpanda.kspec

import io.polymorphicpanda.kspec.config.KSpecConfig

/**
 * @author Ranie Jade Ramiso
 */
interface Configuration {
    fun apply(config: KSpecConfig)
}
