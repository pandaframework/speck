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
import io.polymorphicpanda.kspec.it
import io.polymorphicpanda.kspec.tag.Tag
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class FilterTest {
    @Test
    fun testIncludeExample() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class IncludeSpec: KSpec() {
            override fun configure(config: KSpecConfig) {
                config.filter.include(tag1)
            }

            override fun spec() {
                describe("group") {
                    it("example") { }
                    it("included example", tag1) { }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(IncludeSpec::class)))

        val expected = """
        it: included example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testIncludeExampleGroup() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class IncludeSpec: KSpec() {
            override fun configure(config: KSpecConfig) {
                config.filter.include(tag1)
            }

            override fun spec() {
                describe("group") {
                    it("example") { }

                    context("context", tag1) {
                        it("included example") { }
                    }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(IncludeSpec::class)))

        val expected = """
        it: included example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testExcludeExample() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class ExcludeSpec: KSpec() {
            override fun configure(config: KSpecConfig) {
                config.filter.exclude(tag1)
            }

            override fun spec() {
                describe("group") {
                    it("example") { }
                    it("included example", tag1) { }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(ExcludeSpec::class)))

        val expected = """
        it: example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testExcludeExampleGroup() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class ExcludeSpec: KSpec() {
            override fun configure(config: KSpecConfig) {
                config.filter.exclude(tag1)
            }

            override fun spec() {
                describe("group") {
                    it("example") { }

                    context("context", tag1) {
                        it("included example") { }
                    }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(ExcludeSpec::class)))

        val expected = """
        it: example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testMatchExample() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class MatchSpec: KSpec() {
            override fun configure(config: KSpecConfig) {
                config.filter.matching(tag1)
            }

            override fun spec() {
                describe("group") {
                    it("example") { }
                    it("included example", tag1) { }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(MatchSpec::class)))

        val expected = """
        it: included example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testMatchExampleGroup() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class MatchSpec: KSpec() {
            override fun configure(config: KSpecConfig) {
                config.filter.matching(tag1)
            }

            override fun spec() {
                describe("group") {
                    it("example") { }

                    context("context", tag1) {
                        it("included example") { }
                    }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(MatchSpec::class)))

        val expected = """
        it: included example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testNoMatch() {
        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class MatchSpec: KSpec() {
            override fun configure(config: KSpecConfig) {
                config.filter.matching(tag1)
            }

            override fun spec() {
                describe("group") {
                    it("example") { }

                    context("context") {
                        it("another example") { }
                    }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(MatchSpec::class)))

        val expected = """
        it: example
        it: another example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testFilterPrecedence() {
        /**
         * The order filters are applied is:
         * 1. Match
         * 2. Include
         * 3. Exclude
         */

        val builder = StringBuilder()
        val notifier = ExecutionNotifier()

        notifier.addListener(object: ExecutionListenerAdapter() {
            override fun exampleStarted(example: ExampleContext) {
                builder.appendln(example.description)
            }
        })

        val engine = KSpecEngine(notifier)

        class FilterSpec: KSpec() {
            override fun configure(config: KSpecConfig) {
                config.filter.matching(tag1)
                config.filter.include(tag2)
                config.filter.exclude(tag3)
            }

            override fun spec() {
                describe("group") {
                    it("first included example", tag1) {
                        /**
                         * not executed, has match filter but not include filter tag
                         */
                    }

                    it("unmatched example", tag2) { }

                    context("matched context", tag1) {
                        it("excluded example", tag3) { }
                        it ("included example", tag2) {
                            /**
                             * Executed as parent has match filter and example has include tag
                             */
                        }

                        describe("included group", tag2) {
                            it ("some example") {
                                /**
                                 * Executed as parent has include filter tag
                                 */
                            }
                        }
                    }
                }
            }

        }

        val result = engine.discover(DiscoveryRequest(listOf(FilterSpec::class)))

        val expected = """
        it: included example
        it: some example
        """.trimIndent()

        engine.execute(result)

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    companion object {
        val tag1 = Tag("tag1")
        val tag2 = Tag("tag2")
        val tag3 = Tag("tag3")
    }
}
