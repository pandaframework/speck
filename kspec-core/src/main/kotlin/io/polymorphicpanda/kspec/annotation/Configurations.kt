package io.polymorphicpanda.kspec.annotation

import io.polymorphicpanda.kspec.Configuration
import kotlin.reflect.KClass

/**
 * @author Ranie Jade Ramiso
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Configurations(vararg val configurations: KClass<out Configuration>)
