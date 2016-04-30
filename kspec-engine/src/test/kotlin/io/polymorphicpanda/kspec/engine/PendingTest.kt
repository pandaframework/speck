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
class PendingTest {
    @Test
    fun testPendingExample() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class PendingSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    xit("pending example") { }

                    xit("another pending example w/o a body", "reason")

                    it("not a pending example") { }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(PendingSpec::class)))

        val expected = """
        it: not a pending example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testPendingExampleGroup() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class PendingSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    it("not a pending example") {
                    }

                    xcontext("pending group") {
                        it("pending example #1") {
                        }

                        xdescribe(String::class, "pending group with reason and subject", "the reason") {
                            it("pending example #2") {
                            }
                        }

                        xdescribe(String::class, "pending group with a subject") {
                            it("pending example #6") {
                            }
                        }
                    }

                    xdescribe("pending group using xdescribe") {
                        it("pending example #3") {
                        }

                        xcontext(String::class, "pending group with reason and subject", "the reason") {
                            it("pending example #4") {
                            }
                        }

                        xcontext(String::class, "pending group with a subject") {
                            it("pending example #5") {
                            }
                        }
                    }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(PendingSpec::class)))

        val expected = """
        it: not a pending example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
