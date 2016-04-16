package io.polymorphicpanda.kspec

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.runner.KSpecRunner
import io.polymorphicpanda.kspec.runner.RunNotifier
import io.polymorphicpanda.kspec.support.setupSpec
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class PendingGroupTest {
    @Test
    fun testGroup() {
        val result = StringBuilder().apply {
            val root = setupSpec {
                describe("group") {
                    it("not a pending example") {
                        appendln("not a pending example")
                    }

                    xcontext("pending group") {
                        it("pending example #1") {
                            appendln("pending example #1")
                        }

                        xdescribe(String::class, "pending group with reason and subject", "the reason") {
                            it("pending example #2") {
                                appendln("pending example #2")
                            }
                        }

                        xdescribe(String::class, "pending group with a subject") {
                            it("pending example #6") {
                                appendln("pending example #6")
                            }
                        }
                    }

                    xdescribe("pending group using xdescribe") {
                        it("pending example #3") {
                            appendln("pending example #3")
                        }

                        xcontext(String::class, "pending group with reason and subject", "the reason") {
                            it("pending example #4") {
                                appendln("pending example #4")
                            }
                        }

                        xcontext(String::class, "pending group with a subject") {
                            it("pending example #5") {
                                appendln("pending example #5")
                            }
                        }
                    }
                }
            }

            val config = KSpecConfig()

            val notifier = RunNotifier()
            val runner = KSpecRunner(root, config)

            runner.run(notifier)
        }.trimEnd().toString()

        val expected = """
            not a pending example
            """.trimIndent()

        assertThat(result, equalTo(expected))
    }
}
