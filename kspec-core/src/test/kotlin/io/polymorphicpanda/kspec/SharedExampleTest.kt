package io.polymorphicpanda.kspec

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.runner.KSpecRunner
import io.polymorphicpanda.kspec.runner.RunNotifier
import io.polymorphicpanda.kspec.support.setupSpec
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class SharedExampleTest {
    @Test
    fun testShared() {

        var subject: Throwable? = null

        val shared = sharedExample<Throwable> {
            it("an example") {
                subject = this.subject
            }
        }

        val spec = setupSpec {
            describe(RuntimeException::class) {
                subject { RuntimeException() }
                itBehavesLike(shared)
            }
        }

        val notifier = RunNotifier()
        val runner = KSpecRunner(spec)
        runner.run(notifier)

        assertThat(subject is RuntimeException, equalTo(true))
    }
}
