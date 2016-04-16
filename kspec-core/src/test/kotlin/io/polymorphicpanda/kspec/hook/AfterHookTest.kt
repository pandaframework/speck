package io.polymorphicpanda.kspec.hook

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.config.KSpecConfig
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
class AfterHookTest {
    @Test
    fun testAfterHookExecutionOrder() {
        val builder = StringBuilder()
        val config = KSpecConfig()

        config.after {
            builder.appendln("after hook> ${it.description}")
        }

        val root = setupSpec {
            describe("group") {

                afterEach {
                    builder.appendln("afterEach")
                }

                it("example") {
                    builder.appendln("example")
                }

                it("another example") {
                    builder.appendln("another example")
                }
            }
        }

        val notifier = RunNotifier()
        val runner = KSpecRunner(root, config)

        runner.run(notifier)

        val expected = """
        example
        afterEach
        after hook> it: example
        another example
        afterEach
        after hook> it: another example
        after hook> describe: group
        after hook> ${root.description}""".trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testAfterHookFiltering() {
        val builder = StringBuilder()
        val config = KSpecConfig()

        val tag = Tag("test")

        config.after(tag) {
            builder.appendln("after hook")
        }


        val root = setupSpec {
            describe("group") {

                afterEach {
                    builder.appendln("afterEach")
                }

                it("example", tag) {
                    builder.appendln("example")
                }

                it("ignored example") {
                    builder.appendln("ignored example")
                }

                it("another example", tag) {
                    builder.appendln("another example")
                }
            }
        }

        val notifier = RunNotifier()
        val runner = KSpecRunner(root, config)

        runner.run(notifier)

        val expected = """
        example
        afterEach
        after hook
        ignored example
        afterEach
        another example
        afterEach
        after hook""".trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
