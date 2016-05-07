package io.polymorphicpanda.kspec.launcher

import java.nio.file.Path

/**
 * @author Ranie Jade Ramiso
 */
data class LaunchConfiguration(
    val specs: Path,
    val classpath: List<Path>,
    val spec: String = "",
    val group: String = "",
    val example: String = ""
)
