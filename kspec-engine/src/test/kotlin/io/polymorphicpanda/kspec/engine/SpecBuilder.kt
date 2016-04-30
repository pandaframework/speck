package io.polymorphicpanda.kspec.support

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.Spec
import io.polymorphicpanda.kspec.context.ExampleGroupContext

class TestSpec: KSpec() {
    override fun spec() {
        throw UnsupportedOperationException()
    }
}

inline fun setupSpec(block: Spec.() -> Unit): ExampleGroupContext {
    val builder = TestSpec()
    block.invoke(builder)
    return builder.root
}
