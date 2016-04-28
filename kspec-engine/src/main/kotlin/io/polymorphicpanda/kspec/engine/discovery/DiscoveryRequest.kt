package io.polymorphicpanda.kspec.engine.discovery

import io.polymorphicpanda.kspec.KSpec
import kotlin.reflect.KClass

/**
 * @author Ranie Jade Ramiso
 */
class DiscoveryRequest(val specs: List<KClass<out KSpec>>)
