package io.polymorphicpanda.kspec.sample

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.xcontext
import io.polymorphicpanda.kspec.xit

/**
 * @author Ranie Jade Ramiso
 */
class IgnoredSpec: KSpec() {
    override fun spec() {
        describe("a group") {
            xcontext("pending group") { }
            xit("pending example") { }
        }
    }
}
