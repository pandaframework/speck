package io.polymorphicpanda.kspec.launcher

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.engine.execution.ExecutionResult
import io.polymorphicpanda.kspec.launcher.reporter.BaseReporter
import org.junit.Test
import java.nio.file.Paths

/**
 * @author Ranie Jade Ramiso
 */
class KSpecLauncherTest {

    @Test
    fun testLaunchMatchingSingleClass() {
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

        val configuration = LaunchConfiguration(
            Paths.get(this.javaClass.classLoader.getResource("specs").toURI()),
            emptyList(),
            "io.polymorphicpanda.kspec.sample.SampleSpec"
        )

        launcher.launch(configuration)

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

    @Test
    fun testLaunchWithQuery() {
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

        val configuration = LaunchConfiguration(
            Paths.get(this.javaClass.classLoader.getResource("specs").toURI()),
            emptyList(),
            "io.polymorphicpanda.kspec.sample.AnotherSpec",
            "context: a nested group/it: example"
        )

        launcher.launch(configuration)

        val expected = """
        it: example
        context: a nested group
        describe: a group
        io.polymorphicpanda.kspec.sample.AnotherSpec
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
        assertThat(reporter.totalFailureCount, equalTo(0))
        assertThat(reporter.exampleSuccessCount, equalTo(1))
        assertThat(reporter.exampleGroupSuccessCount, equalTo(3))
        assertThat(reporter.totalIgnoredCount, equalTo(0))
    }

    @Test
    fun testLaunchMatchingPacking() {
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

        val configuration = LaunchConfiguration(
            Paths.get(this.javaClass.classLoader.getResource("specs").toURI()),
            emptyList(),
            "io.polymorphicpanda.kspec.sample.other.*"
        )

        launcher.launch(configuration)

        val expected = """
        it: another example
        context: another group
        io.polymorphicpanda.kspec.sample.other.AnotherSampleSpec
        it: yet another example
        describe: yet another group
        io.polymorphicpanda.kspec.sample.other.YetAnotherSampleSpec
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
        assertThat(reporter.totalFailureCount, equalTo(0))
        assertThat(reporter.exampleSuccessCount, equalTo(2))
        assertThat(reporter.exampleGroupSuccessCount, equalTo(4))
    }

    @Test
    fun testLaunchWithFailure() {
        val launcher = KSpecLauncher()
        val builder = StringBuilder()

        val reporter: BaseReporter = object: BaseReporter() {
            override fun exampleGroupFinished(group: ExampleGroupContext, result: ExecutionResult) {
                super.exampleGroupFinished(group, result)
                builder.appendln(group.description)
            }

            override fun exampleFinished(example: ExampleContext, result: ExecutionResult) {
                super.exampleFinished(example, result)
                if (result is ExecutionResult.Failure) {
                    builder.appendln(example.description)
                }
            }
        }

        launcher.addReporter(reporter)

        val configuration = LaunchConfiguration(
            Paths.get(this.javaClass.classLoader.getResource("specs").toURI()),
            emptyList(),
            "io.polymorphicpanda.kspec.sample.FailingSpec"
        )

        launcher.launch(configuration)

        val expected = """
        it: failing example
        describe: a group
        io.polymorphicpanda.kspec.sample.FailingSpec
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
        assertThat(reporter.totalFailureCount, equalTo(1))
        assertThat(reporter.exampleSuccessCount, equalTo(0))
        assertThat(reporter.exampleGroupSuccessCount, equalTo(2))
    }

    @Test
    fun testLaunchWithIgnored() {
        val launcher = KSpecLauncher()
        val builder = StringBuilder()

        val reporter: BaseReporter = object: BaseReporter() {
            override fun exampleGroupFinished(group: ExampleGroupContext, result: ExecutionResult) {
                super.exampleGroupFinished(group, result)
                builder.appendln(group.description)
            }

            override fun exampleIgnored(example: ExampleContext) {
                super.exampleIgnored(example)
                builder.appendln(example.description)
            }

            override fun exampleGroupIgnored(group: ExampleGroupContext) {
                super.exampleGroupIgnored(group)
                builder.appendln(group.description)
            }
        }

        launcher.addReporter(reporter)

        val configuration = LaunchConfiguration(
            Paths.get(this.javaClass.classLoader.getResource("specs").toURI()),
            emptyList(),
            "io.polymorphicpanda.kspec.sample.IgnoredSpec"
        )

        launcher.launch(configuration)

        val expected = """
        context: pending group
        it: pending example
        describe: a group
        io.polymorphicpanda.kspec.sample.IgnoredSpec
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
        assertThat(reporter.totalFailureCount, equalTo(0))
        assertThat(reporter.exampleIgnoredCount, equalTo(1))
        assertThat(reporter.exampleGroupIgnoredCount, equalTo(1))
        assertThat(reporter.exampleGroupSuccessCount, equalTo(2))
        assertThat(reporter.exampleSuccessCount, equalTo(0))
    }
}
