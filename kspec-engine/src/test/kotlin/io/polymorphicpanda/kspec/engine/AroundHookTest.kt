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
class AroundHookTest {
    @Test
    fun testMatchTag() {
        val builder = StringBuilder()
        val config = KSpecConfig()
        val notifier = ExecutionNotifier()
        val engine = KSpecEngine(notifier)

        config.around(tag1) { context, chain ->
            builder.appendln(context.description)
            chain.next(context)
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

        val result = engine.discover(DiscoveryRequest(listOf(TestSpec::class), KSpecConfig()))

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

        config.around { context, chain ->
            builder.appendln(context.description)
            chain.next(context)
        }

        class TestSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    context("context", AfterHookTest.tag1) {
                        it("example") { }
                    }

                    it("another example", AfterHookTest.tag1) { }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(TestSpec::class), KSpecConfig()))

        val expected = """
        ${TestSpec::class.java.name}
        describe: group
        context: context
        it: example
        it: another example
        """.trimIndent()

        engine.execute(ExecutionRequest(config, result))

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    companion object {
        val tag1 = Tag("tag1")
    }
}
