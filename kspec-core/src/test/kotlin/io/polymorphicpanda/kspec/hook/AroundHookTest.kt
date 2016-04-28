package io.polymorphicpanda.kspec.hook

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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
class AroundHookTest {
    @Test
    fun testAroundHookExecutionOrder() {
        val builder = StringBuilder()

        val root = setupSpec {
            describe("group") {

                beforeEach {
                    builder.appendln("beforeEach")
                }
                it("example") {
                    builder.appendln("example")
                }

                it("another example") {
                    builder.appendln("another example")
                }

                afterEach {
                    builder.appendln("afterEach")
                }
            }
        }

        val notifier = RunNotifier()
        val runner = KSpecRunner(root, { config ->
            config.around { context, chain ->
                builder.appendln("begin around> ${context.description}")
                chain.next(context)
                builder.appendln("end around> ${context.description}")
            }
        })

        runner.run(notifier)

        val expected = """
        begin around> ${root.description}
        begin around> describe: group
        begin around> it: example
        beforeEach
        example
        afterEach
        end around> it: example
        begin around> it: another example
        beforeEach
        another example
        afterEach
        end around> it: another example
        end around> describe: group
        end around> ${root.description}
        """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testAroundHookFiltering() {
        val builder = StringBuilder()
        val tag = Tag("test")

        val root = setupSpec {
            describe("group") {
                it("example", tag) {
                    builder.appendln("example")
                }

                it("ignored example") {
                    builder.appendln("ignored example")
                }
            }
        }

        val notifier = RunNotifier()
        val runner = KSpecRunner(root, { config ->
            config.around(false, tag) { example, chain ->
                builder.appendln("begin around")

                chain.next(example)

                builder.appendln("end around")

            }
        })

        runner.run(notifier)

        val expected = """
        begin around
        example
        end around
        ignored example""".trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
