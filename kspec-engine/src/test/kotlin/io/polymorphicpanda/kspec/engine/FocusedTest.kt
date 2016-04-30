package io.polymorphicpanda.kspec.engine

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.*
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryRequest
import io.polymorphicpanda.kspec.engine.execution.ExecutionListenerAdapter
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class FocusedTest {
    @Test
    fun testMatch() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class FocusedSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    fit("focused example") {
                    }

                    it("example") {
                    }

                    fcontext("focused group using fcontext") {
                        it("another focused example #1") {
                        }

                        fdescribe(String::class, "focused group w/ a subject using fdescribe") {
                            subject { "hello" }

                            it("another focused example #4") {
                            }
                        }
                    }

                    fcontext(String::class, "focused group w/ a subject using fcontext") {
                        subject { "hello" }

                        it("another focused example #2") {
                        }

                        fdescribe("focused group using fdescribe") {
                            it("another focused example #3") {
                            }
                        }
                    }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(FocusedSpec::class)))

        val expected = """
        it: focused example
        it: another focused example #1
        it: another focused example #4
        it: another focused example #2
        it: another focused example #3
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testNoMatch() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })
        val engine = KSpecEngine(notifier)

        class FocusedSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    it("example") {
                    }

                    it("another example") {
                    }

                    context("bar") {
                        it("yet another example") {
                        }
                    }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(FocusedSpec::class)))

        val expected = """
        it: example
        it: another example
        it: yet another example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
