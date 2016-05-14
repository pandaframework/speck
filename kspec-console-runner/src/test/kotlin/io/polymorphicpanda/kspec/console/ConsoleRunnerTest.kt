package io.polymorphicpanda.kspec.console

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.engine.execution.ExecutionResult
import io.polymorphicpanda.kspec.launcher.KSpecLauncher
import io.polymorphicpanda.kspec.launcher.reporter.BaseReporter
import org.junit.Test
import java.nio.file.Paths

/**
 * @author Ranie Jade Ramiso
 */
class ConsoleRunnerTest {
    @Test
    fun testRun() {
        val launcher = KSpecLauncher()
        val reporter = BaseReporter()
        launcher.addReporter(reporter)
        val runner = ConsoleRunner(launcher)

        runner.run(
            "-s", Paths.get(this.javaClass.classLoader.getResource("specs_raw").toURI()).toString(),
            "-f", "io.polymorphicpanda.kspec.sample.*"
        )

        assertThat(reporter.totalSuccessCount, !equalTo(0))
        assertThat(reporter.totalIgnoredCount, !equalTo(0))
        assertThat(reporter.totalFailureCount, !equalTo(0))
    }

    @Test
    fun testPrintVersion() {
        val launcher = KSpecLauncher()
        val runner = ConsoleRunner(launcher)

        runner.run(
            "--version"
        )
    }

    @Test
    fun testPrintHelp() {
        val launcher = KSpecLauncher()
        val runner = ConsoleRunner(launcher)

        runner.run(
            "--help"
        )
    }

    @Test
    fun testRunWithCompile() {
        val launcher = KSpecLauncher()
        val builder = StringBuilder()

        val reporter: BaseReporter = object: BaseReporter() {
            override fun exampleGroupFinished(group: ExampleGroupContext, result: ExecutionResult) {
                super.exampleGroupFinished(group, result)
                builder.appendln(group.description)
            }

            override fun exampleFinished(example: ExampleContext, result: ExecutionResult) {
                super.exampleFinished(example, result)
                builder.appendln(example.description)
            }
        }

        launcher.addReporter(reporter)

        val runner = ConsoleRunner(launcher)

        val spec = Paths.get(
            javaClass.classLoader.getResource("specs_raw/io/polymorphicpanda/kspec/sample/SampleSpec.kt").toURI()
        )

        runner.run(
            "-s", spec.toString()
        )

        val expected = """
        it: an example
        context: a nested group
        describe: a sample group
        io.polymorphicpanda.kspec.sample.SampleSpec
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
        assertThat(reporter.totalFailureCount, equalTo(0))
        assertThat(reporter.exampleSuccessCount, equalTo(1))
        assertThat(reporter.exampleGroupSuccessCount, equalTo(3))
    }
}
