package io.polymorphicpanda.kspec.sample

import io.polymorphicpanda.kspec.KSpec
import io.polymorphicpanda.kspec.context
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.it

/**
 * @author Ranie Jade Ramiso
 */
class AnotherSpec: KSpec() {
    override fun spec() {
        describe("a group") {
            context("a nested group") {
                it("example") { }
            }

            describe("another nested group") {
                it("another example") { }
            }
        }
    }
}
