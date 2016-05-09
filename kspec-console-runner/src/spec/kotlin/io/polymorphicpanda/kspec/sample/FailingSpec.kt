package io.polymorphicpanda.kspec.sample

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.it

/**
 * @author Ranie Jade Ramiso
 */
class FailingSpec: KSpec() {
    override fun spec() {
        describe("a group") {
            it("failing example") {
                TODO()
            }
        }
    }
}
