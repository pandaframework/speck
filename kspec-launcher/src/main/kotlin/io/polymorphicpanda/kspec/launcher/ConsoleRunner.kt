package io.polymorphicpanda.kspec.launcher

import io.polymorphicpanda.kspec.launcher.reporter.ConsoleReporter
import joptsimple.OptionException
import joptsimple.OptionParser
import joptsimple.OptionSet
import java.nio.file.Paths

class ConsoleRunner(val launcher: KSpecLauncher) {

    init {
        launcher.addReporter(ConsoleReporter())
    }

    private val parser by lazy(LazyThreadSafetyMode.NONE) {
        OptionParser().apply {
            acceptsAll(listOf("d", "dir"), "Directory where specs will be searched.")
                .withRequiredArg()
                .required()

            acceptsAll(listOf("classpath", "cp"), "Contains the classes required to run the specs.")
                .withRequiredArg()
                .withValuesSeparatedBy(CLASSPATH_SEPRATOR)

            acceptsAll(listOf("s", "spec"), "Filter out specific spec classes.")
                .withRequiredArg()

            acceptsAll(listOf("q", "query"), "Specify xpath-like query.")
                .withRequiredArg()

            acceptsAll(listOf("h", "help"), "Prints this message.")
        }
    }

    inline fun <reified T> OptionSet.getOrDefault(option: String, default: T): T {
        return if (has(option)) valueOf(option) as T else default
    }

    inline fun <reified T> OptionSet.get(option: String): T {
        return valueOf(option) as T
    }

    fun run(vararg args: String) {
        try {
            val optionSet = parser.parse(*args)

            if (optionSet.has("help")) {
                parser.printHelpOn(System.out)
            } else {
                val spec = optionSet.getOrDefault("spec", "")
                val query = optionSet.getOrDefault("query", "")
                val dir = Paths.get(optionSet.get<String>("dir"))
                val cp = optionSet.valuesOf("cp")
                    .map { it as String }
                    .map { Paths.get(it) }

                val configuration = LaunchConfiguration(dir, cp, spec, query)

                launcher.launch(configuration)
            }
        } catch(e: OptionException) {
            println(e.message)
        }
    }

    companion object {
        val CLASSPATH_SEPRATOR = ":"
    }
}
