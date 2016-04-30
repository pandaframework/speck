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
    fun testFocusedExample() {
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
                    fit("focused example") { }

                    it("example") { }

                    context("another group") {
                        fit("another focused example") { }
                    }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(FocusedSpec::class)))

        val expected = """
        it: focused example
        it: another focused example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testFocusedExampleGroup() {
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
                    it("example") { }

                    fcontext("another group") {
                        it("focused example") { }
                    }

                    fdescribe("other group") {
                        it("another focused example") { }
                    }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(FocusedSpec::class)))

        val expected = """
        it: focused example
        it: another focused example
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
