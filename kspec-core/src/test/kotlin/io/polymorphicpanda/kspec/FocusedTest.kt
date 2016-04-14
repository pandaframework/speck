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
class FocusedTest {
    @Test
    fun testMatch() {
        val builder = StringBuilder()

        val root = setupSpec {
            describe("group") {
                fit("focused example") {
                    builder.appendln("focused example")
                }

                it("example") {
                    builder.appendln("example")
                }

                fcontext("focused group using fcontext") {
                    it("another focused example #1") {
                        builder.appendln("another focused example #1")
                    }

                    fdescribe(String::class, "focused group w/ a subject using fdescribe") {
                        subject { "hello" }

                        it("another focused example #4") {
                            builder.appendln("another focused example #4")
                        }
                    }
                }

                fcontext(String::class, "focused group w/ a subject using fcontext") {
                    subject { "hello" }

                    it("another focused example #2") {
                        builder.appendln("another focused example #2")
                    }

                    fdescribe("focused group using fdescribe") {
                        it("another focused example #3") {
                            builder.appendln("another focused example #3")
                        }
                    }
                }
            }
        }

        val config = KSpecConfig()
        val notifier = RunNotifier()
        val runner = KSpecRunner(root, config)

        runner.run(notifier)

        val expected = """
        focused example
        another focused example #1
        another focused example #4
        another focused example #2
        another focused example #3
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testNoMatch() {
        val builder = StringBuilder()

        val root = setupSpec {
            describe("group") {
                it("example") {
                    builder.appendln("example")
                }

                it("another example") {
                    builder.appendln("another example")
                }

                context("bar") {
                    it("yet another example") {
                        builder.appendln("yet another example")
                    }
                }
            }
        }

        val config = KSpecConfig()
        val notifier = RunNotifier()
        val runner = KSpecRunner(root, config)

        runner.run(notifier)

        val expected = """
        example
        another example
        yet another example
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
