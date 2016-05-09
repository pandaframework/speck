package io.polymorphicpanda.kspec.console

import io.polymorphicpanda.kspec.launcher.KSpecLauncher

fun main(vararg args: String) {
    ConsoleRunner(KSpecLauncher()).run(*args)
}
