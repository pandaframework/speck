package io.polymorphicpanda.kspec.engine

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryRequest
import io.polymorphicpanda.kspec.engine.execution.ExecutionListenerAdapter
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import io.polymorphicpanda.kspec.engine.execution.ExecutionRequest
import io.polymorphicpanda.kspec.engine.query.Query
import io.polymorphicpanda.kspec.it
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class QueriedTest {
    @Test
    fun testQuery() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class SampleSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    context("a nested group") {
                        it("example") { }
                    }

                    describe("another nested group") {
                        it("another example") { }
                    }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(SampleSpec::class)))

        val expected = """
        it: example
        """.trimIndent()

        engine.execute(ExecutionRequest(KSpecConfig(), result, Query.parse("context: a nested group/it: example")))

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
