package io.polymorphicpanda.kspec.helpers

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.sameInstance
import com.natpryce.hamkrest.throws
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class MemoizedHelperTest {
    @Test
    fun testLazy() {
        val memoized = MemoizedHelper<String>({
            throw UnsupportedOperationException()
        })


        assertThat({
            memoized.get()
        }, throws<UnsupportedOperationException>())
    }

    @Test
    fun testCached() {
        val memoized = MemoizedHelper({
            Any()
        })

        val first = memoized.get()

        assertThat(first, sameInstance(memoized.get()))
    }

    @Test
    fun testForget() {
        val memoized = MemoizedHelper({
            Any()
        })

        val first = memoized.get()

        memoized.forget()

        assertThat(first, !sameInstance(memoized.get()))
    }
}
