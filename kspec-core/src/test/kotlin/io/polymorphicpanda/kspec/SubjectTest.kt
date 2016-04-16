package io.polymorphicpanda.kspec

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import com.natpryce.hamkrest.sameInstance
import io.polymorphicpanda.kspec.runner.KSpecRunner
import io.polymorphicpanda.kspec.runner.RunNotifier
import io.polymorphicpanda.kspec.support.setupSpec
import org.junit.Test

/**
 * @author Ranie Jade Ramiso
 */
class SubjectTest {
    @Test
    fun testMemoizedSubjects() {
        var subject1: Throwable? = null
        var subject2: Throwable? = null
        var subject3: Throwable? = null

        val root = setupSpec {
            context(RuntimeException::class) {
                it("example") {
                    subject1 = subject
                }

                it("another example") {
                    subject2 = subject
                    subject3 = subject
                }
            }
        }

        val notifier = RunNotifier()
        val runner = KSpecRunner(root)
        runner.run(notifier)

        assertThat(subject1, !sameInstance(subject2))
        assertThat(subject2, sameInstance(subject3))
    }

    @Test
    fun testSubjectNoArgConstructor() {

        class TestWithoutNoArgConstructor(val foo: Int)

        var exception: Throwable? = null

        val root = setupSpec {
            context(TestWithoutNoArgConstructor::class) {
                it("example") {
                    try {
                        // subjects are lazily initialized
                        this.subject
                    } catch (e: InstantiationException) {
                        exception = e
                    }
                }
            }
        }

        val notifier = RunNotifier()
        val runner = KSpecRunner(root)
        runner.run(notifier)

        fun Throwable.isInstantiationException() = this is InstantiationException

        assertThat(exception, present(Matcher.invoke(Throwable::isInstantiationException)))
    }


}
