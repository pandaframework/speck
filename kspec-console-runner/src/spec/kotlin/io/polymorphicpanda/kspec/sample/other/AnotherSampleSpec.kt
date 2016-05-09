package io.polymorphicpanda.kspec.sample.other

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.context
import io.polymorphicpanda.kspec.it

/**
 * @author Ranie Jade Ramiso
 */
class AnotherSampleSpec: KSpec() {
    override fun spec() {
        context("another group") {
            it("another example") { }
        }
    }
}
