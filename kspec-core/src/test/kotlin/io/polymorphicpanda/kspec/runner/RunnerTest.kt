package io.polymorphicpanda.kspec.runner

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.it
import io.polymorphicpanda.kspec.support.RememberingListener
import io.polymorphicpanda.kspec.support.setupSpec
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class RunnerTest {
    @Test
    fun testBeforeFailure() {
        val root = setupSpec {
            describe("group") {
                before { throw RuntimeException() }

                it("example") {

                }
            }
        }

        val runner = KSpecRunner(root)
        val listener = RememberingListener()
        val notifier = RunNotifier()
        notifier.addListener(listener)

        runner.run(notifier)

        assertThat(listener.groupFailure.containsKey("describe: group"), equalTo(true))
    }

    @Test
    fun testAfterFailure() {
        val root = setupSpec {
            describe("group") {
                after { throw RuntimeException() }

                it("example") {

                }
            }
        }

        val runner = KSpecRunner(root)
        val listener = RememberingListener()
        val notifier = RunNotifier()
        notifier.addListener(listener)

        runner.run(notifier)

        assertThat(listener.groupFailure.containsKey("describe: group"), equalTo(true))
    }

    @Test
    fun testBeforeEachFailure() {
        val root = setupSpec {
            describe("group") {
                beforeEach { throw RuntimeException() }

                it("example") {

                }
            }
        }

        val runner = KSpecRunner(root)
        val listener = RememberingListener()
        val notifier = RunNotifier()
        notifier.addListener(listener)

        runner.run(notifier)

        assertThat(listener.exampleFailure.containsKey("it: example"), equalTo(true))
    }

    @Test
    fun testAfterEachFailure() {
        val root = setupSpec {
            describe("group") {
                afterEach { throw RuntimeException() }

                it("example") {

                }
            }
        }

        val runner = KSpecRunner(root)
        val listener = RememberingListener()
        val notifier = RunNotifier()
        notifier.addListener(listener)

        runner.run(notifier)

        assertThat(listener.exampleFailure.containsKey("it: example"), equalTo(true))
    }
}
