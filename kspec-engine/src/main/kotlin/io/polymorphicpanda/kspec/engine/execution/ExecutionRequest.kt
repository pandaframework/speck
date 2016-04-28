package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryResult

/**
 * @author Ranie Jade Ramiso
 */
class ExecutionRequest(val config: KSpecConfig,
                       val discoveryResult: DiscoveryResult)
