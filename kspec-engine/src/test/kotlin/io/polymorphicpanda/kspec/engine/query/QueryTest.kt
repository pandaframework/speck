package io.polymorphicpanda.kspec.engine.query

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class QueryTest {

    @Test
    fun testFull() {
        assertMatch("com.example.Spec/describe: group/it: example", "com.example.Spec/describe: group/it: example")
    }

    @Test
    fun testRelative() {
        val query = "describe: bar/it: example"
        assertMatch(query, "com.example.Spec/describe: bar/it: example")
        assertNotMatch(query, "com.example.Spec/describe: bar/it: another example")
        assertMatch(query, "com.example.Spec2/context: foo/describe: bar/it: example")
    }

    @Test
    fun testWildCard() {
        val query = "describe: foo/*"
        assertMatch(query, "com.example.Spec/describe: foo/it: example")
        assertMatch(query, "com.example.Spec/describe: foo/context: bar/it: example")
    }

    private fun assertMatch(query: String, path: String) {
        assertThat(Query.parse(query).matches(path), equalTo(true))
    }

    private fun assertNotMatch(query: String, path: String) {
        assertThat(Query.parse(query).matches(path), equalTo(false))
    }
}
