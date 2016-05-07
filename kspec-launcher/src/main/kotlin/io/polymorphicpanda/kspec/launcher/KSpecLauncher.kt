package io.polymorphicpanda.kspec.launcher

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.engine.KSpecEngine
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryRequest
import io.polymorphicpanda.kspec.engine.execution.ExecutionListenerAdapter
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import io.polymorphicpanda.kspec.launcher.reporter.Reporter
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.regex.Pattern
import kotlin.reflect.KClass

/**
 * @author Ranie Jade Ramiso
 */
class KSpecLauncher {
    private val reporters = LinkedHashSet<Reporter>()

    private val listener = object: ExecutionListenerAdapter() {
        override fun executionStarted() {
            reporters.forEach { it.executionStarted() }
        }

        override fun executionFinished() {
            reporters.forEach { it.executionFinished() }
        }

        override fun exampleFailure(example: ExampleContext, throwable: Throwable) {
            reporters.forEach { it.exampleFailure(example, throwable) }
        }

        override fun exampleFinished(example: ExampleContext) {
            reporters.forEach { it.exampleSuccess(example) }
        }

        override fun exampleIgnored(example: ExampleContext) {
            reporters.forEach { it.exampleIgnored(example) }
        }

        override fun exampleGroupFailure(group: ExampleGroupContext, throwable: Throwable) {
            reporters.forEach { it.exampleGroupFailure(group, throwable) }
        }

        override fun exampleGroupIgnored(group: ExampleGroupContext) {
            reporters.forEach { it.exampleGroupIgnored(group) }
        }

        override fun exampleGroupFinished(group: ExampleGroupContext) {
            reporters.forEach { it.exampleGroupSuccess(group) }
        }
    }

    private val notifier by lazy(LazyThreadSafetyMode.NONE) {
        ExecutionNotifier().apply {
            addListener(listener)
        }
    }

    private val engine by lazy(LazyThreadSafetyMode.NONE) {
        KSpecEngine(notifier)
    }

    fun addReporter(reporter: Reporter) {
        reporters.add(reporter)
    }

    fun launch(configuration: LaunchConfiguration) {
        val target = configuration.specs

        if (!target.isDirectory()) {
            throw IllegalArgumentException("Target must be a directory.")
        }

        val classes = searchClasses(target)

        val runtime = ArrayList<URL>()
        runtime.add(target.toUri().toURL())
        runtime.addAll(configuration.classpath.map { it.toUri().toURL() })

        val classloader = URLClassLoader(runtime.toTypedArray(), Thread.currentThread().contextClassLoader)

        Thread.currentThread().contextClassLoader = classloader

        val pattern = Pattern.compile(escape(configuration.spec))

        val filtered = classes.map { classloader.loadClass(it) }
            .filter { KSpec::class.java.isAssignableFrom(it)}
            .filter { pattern.matcher(it.name).matches() }
            .sortedBy { it.name }
            .map { it.kotlin as KClass<out KSpec> }

        runSpecs(filtered)
    }

    private fun runSpecs(specs: List<KClass<out KSpec>>) {
        val discoveryResult = engine.discover(DiscoveryRequest(specs))

        engine.execute(discoveryResult)
    }

    private fun escape(pattern: String): String {
        return pattern.replace(".", "\\.")
            .replace("*", ".*")
    }
}
