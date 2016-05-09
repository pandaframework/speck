package io.polymorphicpanda.kspec.launcher

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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
            "-d", Paths.get(this.javaClass.classLoader.getResource("specs").toURI()).toString(),
            "--spec", "io.polymorphicpanda.kspec.sample.*"
        )

        assertThat(reporter.totalSuccessCount, !equalTo(0))
        assertThat(reporter.totalIgnoredCount, !equalTo(0))
        assertThat(reporter.totalFailureCount, !equalTo(0))
    }
}
