package io.polymorphicpanda.kspec.runner

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.context
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.it
import io.polymorphicpanda.kspec.support.RememberingListener
import io.polymorphicpanda.kspec.support.setupSpec
import io.polymorphicpanda.kspec.xit
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class RunListenersTest {
    @Test
    fun testNotify() {
        val spec = setupSpec {
            describe("a group") {
                context("another group") {
                    it("an example") {

                    }

                    it("another example") {

                    }

                    describe("yet another group") {
                        before {
                            throw RuntimeException()
                        }

                        it("yet another example") {

                        }
                    }

                    it("oh no.. another example") {
                        throw RuntimeException()
                    }

                    xit("oh no.. yet another example")
                }
            }
        }

        val runner = KSpecRunner(spec)
        val listener = RememberingListener()
        val notifier = RunNotifier()
        notifier.addListener(listener)

        runner.run(notifier)

        assertThat(listener.groupStarted.containsKey("describe: a group"), equalTo(true))
        assertThat(listener.groupStarted.containsKey("context: another group"), equalTo(true))
        assertThat(listener.groupStarted.containsKey("describe: yet another group"), equalTo(false))

        assertThat(listener.groupFailure.containsKey("describe: a group"), equalTo(false))
        assertThat(listener.groupFailure.containsKey("context: another group"), equalTo(false))
        assertThat(listener.groupFailure.containsKey("describe: yet another group"), equalTo(true))

        assertThat(listener.groupFinished.containsKey("describe: a group"), equalTo(true))
        assertThat(listener.groupFinished.containsKey("context: another group"), equalTo(true))
        assertThat(listener.groupFinished.containsKey("describe: yet another group"), equalTo(false))

        assertThat(listener.exampleStarted.containsKey("it: an example"), equalTo(true))
        assertThat(listener.exampleStarted.containsKey("it: another example"), equalTo(true))
        assertThat(listener.exampleStarted.containsKey("it: yet another example"), equalTo(false))
        assertThat(listener.exampleStarted.containsKey("it: oh no.. another example"), equalTo(true))
        assertThat(listener.exampleStarted.containsKey("it: oh no.. yet another example"), equalTo(false))

        assertThat(listener.exampleFailure.containsKey("it: an example"), equalTo(false))
        assertThat(listener.exampleFailure.containsKey("it: another example"), equalTo(false))
        assertThat(listener.exampleFailure.containsKey("it: yet another example"), equalTo(false))
        assertThat(listener.exampleFailure.containsKey("it: oh no.. another example"), equalTo(true))
        assertThat(listener.exampleFailure.containsKey("it: oh no.. yet another example"), equalTo(false))

        assertThat(listener.exampleIgnored.containsKey("it: an example"), equalTo(false))
        assertThat(listener.exampleIgnored.containsKey("it: another example"), equalTo(false))
                assertThat(listener.exampleIgnored.containsKey("it: yet another example"), equalTo(false))
        assertThat(listener.exampleIgnored.containsKey("it: oh no.. another example"), equalTo(false))
        assertThat(listener.exampleIgnored.containsKey("it: oh no.. yet another example"), equalTo(true))

        assertThat(listener.exampleFinished.containsKey("it: an example"), equalTo(true))
        assertThat(listener.exampleFinished.containsKey("it: another example"), equalTo(true))
        assertThat(listener.exampleFinished.containsKey("it: yet another example"), equalTo(false))
        assertThat(listener.exampleFinished.containsKey("it: oh no.. another example"), equalTo(true))
        assertThat(listener.exampleFinished.containsKey("it: oh no.. yet another example"), equalTo(false))

    }
}
