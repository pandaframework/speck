package io.polymorphicpanda.kspec.engine

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryRequest
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import io.polymorphicpanda.kspec.engine.execution.ExecutionRequest
import io.polymorphicpanda.kspec.it
import io.polymorphicpanda.kspec.tag.Tag
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class AfterHookTest {
    @Test
    fun testMatchTag() {
        val builder = StringBuilder()
        val config = KSpecConfig()
        val notifier = ExecutionNotifier()
        val engine = KSpecEngine(notifier)

        config.after(tag1) {
            builder.appendln(it.description)
        }

        class TestSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    context("context", tag1) {
                        it("example") { }
                    }

                    it("another example", tag1) { }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(TestSpec::class)))

        val expected = """
        context: context
        it: another example
        """.trimIndent()

        engine.execute(ExecutionRequest(config, result))

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testMatchAll() {
        val builder = StringBuilder()
        val config = KSpecConfig()
        val notifier = ExecutionNotifier()
        val engine = KSpecEngine(notifier)

        config.after {
            builder.appendln(it.description)
        }

        class TestSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    context("context", tag1) {
                        it("example") { }
                    }

                    it("another example", tag1) { }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(TestSpec::class)))

        val expected = """
        it: example
        context: context
        it: another example
        describe: group
        ${TestSpec::class.java.name}
        """.trimIndent()

        engine.execute(ExecutionRequest(config, result))

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    companion object {
        val tag1 = Tag("tag1")
    }
}
