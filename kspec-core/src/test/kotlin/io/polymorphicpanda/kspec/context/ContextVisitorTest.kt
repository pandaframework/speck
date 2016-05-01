package io.polymorphicpanda.kspec.context

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.context
import io.polymorphicpanda.kspec.describe
import io.polymorphicpanda.kspec.it
import io.polymorphicpanda.kspec.setupSpec
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class ContextVisitorTest {
    val root = setupSpec {
        describe("test") {
            context("fooo") {
                it("bar") {

                }

                context("bar") {
                    it("shafoo") {

                    }
                }
            }
        }

        it("oh no") {

        }
    }

    @Test
    fun testSkipSubTree() {
        val builder = StringBuilder()

        root.visit(object: ContextVisitor {
            override fun preVisitExampleGroup(context: ExampleGroupContext): ContextVisitResult {
                builder.appendln("pre - ${context.description}")
                if (context.description == "context: fooo") {
                    return ContextVisitResult.SKIP_SUBTREE
                }
                return ContextVisitResult.CONTINUE
            }

            override fun onVisitExample(context: ExampleContext): ContextVisitResult {
                builder.appendln("on - ${context.description}")
                return ContextVisitResult.CONTINUE
            }

            override fun postVisitExampleGroup(context: ExampleGroupContext): ContextVisitResult {
                builder.appendln("post - ${context.description}")
                return ContextVisitResult.CONTINUE
            }
        })

        val expected = """
            pre - ${root.description}
            pre - describe: test
            pre - context: fooo
            post - context: fooo
            post - describe: test
            on - it: oh no
            post - ${root.description}
            """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testTerminatePreVisitExampleGroup() {
        val builder = StringBuilder()

        root.visit(object: ContextVisitor {
            override fun preVisitExampleGroup(context: ExampleGroupContext): ContextVisitResult {
                builder.appendln("pre - ${context.description}")
                if (context.description == "context: fooo") {
                    return ContextVisitResult.TERMINATE
                }
                return ContextVisitResult.CONTINUE
            }

            override fun onVisitExample(context: ExampleContext): ContextVisitResult {
                builder.appendln("on - ${context.description}")
                return ContextVisitResult.CONTINUE
            }

            override fun postVisitExampleGroup(context: ExampleGroupContext): ContextVisitResult {
                builder.appendln("post - ${context.description}")
                return ContextVisitResult.CONTINUE
            }
        })

        val expected = """
            pre - ${root.description}
            pre - describe: test
            pre - context: fooo
            """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testTerminatePostVisitExampleGroup() {
        val builder = StringBuilder()

        root.visit(object: ContextVisitor {
            override fun preVisitExampleGroup(context: ExampleGroupContext): ContextVisitResult {
                builder.appendln("pre - ${context.description}")
                return ContextVisitResult.CONTINUE
            }

            override fun onVisitExample(context: ExampleContext): ContextVisitResult {
                builder.appendln("on - ${context.description}")
                return ContextVisitResult.CONTINUE
            }

            override fun postVisitExampleGroup(context: ExampleGroupContext): ContextVisitResult {
                builder.appendln("post - ${context.description}")
                if (context.description == "context: fooo") {
                    return ContextVisitResult.TERMINATE
                }
                return ContextVisitResult.CONTINUE
            }
        })

        val expected = """
            pre - ${root.description}
            pre - describe: test
            pre - context: fooo
            on - it: bar
            pre - context: bar
            on - it: shafoo
            post - context: bar
            post - context: fooo
            """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }

    @Test
    fun testTerminateOnVisitExample() {
        val builder = StringBuilder()

        root.visit(object: ContextVisitor {
            override fun preVisitExampleGroup(context: ExampleGroupContext): ContextVisitResult {
                builder.appendln("pre - ${context.description}")
                return ContextVisitResult.CONTINUE
            }

            override fun onVisitExample(context: ExampleContext): ContextVisitResult {
                builder.appendln("on - ${context.description}")
                if (context.description == "it: bar") {
                    return ContextVisitResult.TERMINATE
                }
                return ContextVisitResult.CONTINUE
            }

            override fun postVisitExampleGroup(context: ExampleGroupContext): ContextVisitResult {
                builder.appendln("post - ${context.description}")
                return ContextVisitResult.CONTINUE
            }
        })

        val expected = """
            pre - ${root.description}
            pre - describe: test
            pre - context: fooo
            on - it: bar
            """.trimIndent()

        assertThat(builder.trimEnd().toString(), equalTo(expected))
    }
}
