package io.polymorphicpanda.kspec.engine.query

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.context
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.it
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class TransformTest {
    @Test
    fun testTransform() {
        class SampleSpec: KSpec() {
            override fun spec() {
                describe("bar") {
                    context("foo") {
                        it("foobar") { }
                    }

                    it("barfoo") { }
                }
            }

        }
        val spec = SampleSpec()
        spec.spec()
        val root = spec.root
        val bar = root.children.first { it.description.equals("describe: bar") } as ExampleGroupContext
        val foo = bar.children.first { it.description.equals("context: foo") } as ExampleGroupContext
        val foobar = foo.children.first { it.description.equals("it: foobar") } as ExampleContext
        val barfoo = bar.children.first { it.description.equals("it: barfoo") } as ExampleContext

        val kls = "io.polymorphicpanda.kspec.engine.query.TransformTest\$testTransform\$SampleSpec"

        assertThat(Query.transform(root), equalTo("$kls"))
        assertThat(Query.transform(bar), equalTo("$kls/${bar.description}"))
        assertThat(Query.transform(foo), equalTo("$kls/${bar.description}/${foo.description}"))
        assertThat(Query.transform(foobar), equalTo("$kls/${bar.description}/${foo.description}/${foobar.description}"))
        assertThat(Query.transform(barfoo), equalTo("$kls/${bar.description}/${barfoo.description}"))
    }
}
