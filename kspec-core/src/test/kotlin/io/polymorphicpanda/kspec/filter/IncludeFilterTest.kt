package io.polymorphicpanda.kspec.filter

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.it
import io.polymorphicpanda.kspec.runner.KSpecRunner
import io.polymorphicpanda.kspec.runner.RunNotifier
import io.polymorphicpanda.kspec.support.setupSpec
import io.polymorphicpanda.kspec.tag.Tag
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class IncludeFilterTest {
    @Test
    fun testSingle() {
        val builder = StringBuilder()

        val tag1 = Tag("tag1")

        val root = setupSpec {
            describe("group") {
                it("example", tag1) {
                    builder.appendln("example")
                }

                it("another example") {
                    builder.appendln("another example")
                }
            }
        }

        val config = KSpecConfig()

        config.filter.include(tag1)

        val notifier = RunNotifier()
        val runner = KSpecRunner(root, config)

        runner.run(notifier)

        val expected = """
        example
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testGroup() {
        val result = StringBuilder().apply {

            val tag1 = Tag("tag1")

            val root = setupSpec {
                describe("group") {
                    it("example") {
                        appendln("example")
                    }

                    it("another example") {
                        appendln("another example")
                    }

                    context("context", tag1) {
                        it("context example") {
                            appendln("context example")
                        }
                    }
                }
            }

            val config = KSpecConfig()

            config.filter.include(tag1)

            val notifier = RunNotifier()
            val runner = KSpecRunner(root, config)

            runner.run(notifier)

        }.trimEnd().toString()

        val expected = """
            context example
            """.trimIndent()

        assertThat(result, equalTo(expected))
    }

    @Test
    fun testMultiple() {
        val builder = StringBuilder()

        val tag1 = Tag("tag1")
        val tag2 = Tag("tag2")

        val root = setupSpec {
            describe("group") {
                it("example", tag1) {
                    builder.appendln("example")
                }

                context("bar", tag2) {
                    it("another example") {
                        builder.appendln("another example")
                    }
                }



                it("yet another example") {
                    builder.appendln("yet another example")
                }
            }
        }

        val config = KSpecConfig()

        config.filter.include(tag1, tag2)

        val notifier = RunNotifier()
        val runner = KSpecRunner(root, config)

        runner.run(notifier)

        val expected = """
        example
        another example
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
