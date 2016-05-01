package io.polymorphicpanda.kspec.engine

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.*
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.engine.discovery.DiscoveryRequest
import io.polymorphicpanda.kspec.engine.execution.ExecutionListenerAdapter
import io.polymorphicpanda.kspec.engine.execution.ExecutionNotifier
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Ranie Jade Ramiso
 */
class InvalidGroupDeclarationTest {
    @Test
    fun testInvalid() {
        val notifier = ExecutionNotifier()
        val engine = KSpecEngine(notifier)
        val expected = AtomicReference<Throwable>()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleFailure(example: ExampleContext, throwable: Throwable) {
                expected.set(throwable)
            }
        })

        class InvalidSpec: KSpec() {
            override fun spec() {
                describe("group") {
                    it("example") {
                        context("this is invalid") { }
                    }
                }
            }
        }

        val result = engine.discover(DiscoveryRequest(listOf(InvalidSpec::class)))
        engine.execute(result)

        assertThat(expected.get() is InvalidSpecException, equalTo(true))
    }
}
