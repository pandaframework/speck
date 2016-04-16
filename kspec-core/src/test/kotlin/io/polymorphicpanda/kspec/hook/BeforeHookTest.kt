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
class BeforeHookTest {
    @Test
    fun testBeforeHookExecutionOrder() {
        val builder = StringBuilder()
        val config = KSpecConfig()

        config.before {
            builder.appendln("before hook> ${it.description}")
        }

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
            }
        }

        val notifier = RunNotifier()
        val runner = KSpecRunner(root, config)

        runner.run(notifier)

        val expected = """
        before hook> ${root.description}
        before hook> describe: group
        before hook> it: example
        beforeEach
        example
        before hook> it: another example
        beforeEach
        another example""".trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun  testBeforeHookFiltering() {

        val builder = StringBuilder()
        val config = KSpecConfig()

        val tag = Tag("test")

        config.before(tag) {
            builder.appendln("before hook")
        }

        val root = setupSpec {
            describe("group") {

                beforeEach {
                    builder.appendln("beforeEach")
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
        before hook
        beforeEach
        example
        beforeEach
        ignored example
        before hook
        beforeEach
        another example""".trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
