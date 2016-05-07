package io.polymorphicpanda.kspec.launcher

import java.nio.file.Files
import java.nio.file.Path
import java.util.*


fun Path.isDirectory() = this.toFile().isDirectory

private fun searchFiles(path: Path): List<Path> {
    return Files.newDirectoryStream(path).use { stream ->
        stream.map {
            when {
                it.isDirectory() -> {
                    searchFiles(it)
                }
                else -> listOf(it)
            }
        }.reduceRight { left, right ->
            ArrayList<Path>().apply {
                addAll(left)
                addAll(right)
            }
        }
    }
}

fun searchClasses(root: Path): List<String> {
    return searchFiles(root).map { root.relativize(it) }
        .map({ it.toString() })
        .filter { it.endsWith(".class") }
        .map { it.replace("/", ".").removeSuffix(".class") }
}

