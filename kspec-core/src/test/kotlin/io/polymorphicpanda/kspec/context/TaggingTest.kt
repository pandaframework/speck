package io.polymorphicpanda.kspec.context

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import io.polymorphicpanda.kspec.tag.Tag
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class TaggingTest {
    @Test
    fun testTagExample() {
        val focus = Tag("focus")
        val test = Tag("test")
        val tags = setOf(focus)
        val example = ExampleContext("example", ExampleGroupContext("group", null), null, tags)

        assertThat(example.contains(focus), equalTo(true))
        assertThat(example.contains(test), equalTo(false))
    }

    @Test
    fun testTagGroup() {
        val focus = Tag("focus")
        val test = Tag("test")
        val parent = ExampleGroupContext("group", null, setOf(focus))
        val another = ExampleGroupContext("another", parent)
        val example = ExampleContext("example", parent, null, setOf(test))

        assertThat(parent.contains(focus), equalTo(true))
        assertThat(another.contains(focus), equalTo(true))
        assertThat(example.contains(focus), equalTo(true))
        assertThat(example.contains(test), equalTo(true))
    }

    @Test
    fun testGetTagExample() {
        val focus = Tag("focus")
        val tags = setOf(focus)
        val example = ExampleContext("example", ExampleGroupContext("group", null), null, tags)

        assertThat(example["focus"], present(equalTo(focus)))
    }

    @Test
    fun testGetTagGroup() {
        val focus = Tag("focus")
        val test = Tag("test")
        val parent = ExampleGroupContext("group", null, setOf(focus))
        val another = ExampleGroupContext("another", parent)
        val example = ExampleContext("example", parent, null, setOf(test))

        assertThat(parent.contains(focus), equalTo(true))
        assertThat(another.contains(focus), equalTo(true))
        assertThat(example.contains(focus), equalTo(true))
        assertThat(example.contains(test), equalTo(true))

        assertThat(parent["focus"], present(equalTo(focus)))
        assertThat(another["focus"], present(equalTo(focus)))
        assertThat(example["focus"], present(equalTo(focus)))
        assertThat(example["test"], present(equalTo(test)))
    }
}
