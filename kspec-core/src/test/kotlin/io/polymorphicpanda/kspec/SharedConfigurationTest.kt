package io.polymorphicpanda.kspec

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.polymorphicpanda.kspec.annotation.Configurations
import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.Configuration
import io.polymorphicpanda.kspec.runner.KSpecRunner
import io.polymorphicpanda.kspec.runner.RunNotifier
import io.polymorphicpanda.kspec.support.setupSpec
import org.junit.Test
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class SharedConfigurationTest {
    // just test if the annotation is applied
    @Test(expected = UnsupportedOperationException::class)
    fun testApply() {
        class TestConfiguration: Configuration {
            override fun apply(config: KSpecConfig) {
                throw UnsupportedOperationException()
            }
        }

        @Configurations(TestConfiguration::class)
        class AnnotationHolder

        val root = setupSpec {  }
        val runner = KSpecRunner(root, {}, Utils.findAnnotation(AnnotationHolder::class, Configurations::class))

        runner.run(RunNotifier())
    }

    open class BaseConfiguration: Configuration {
        override fun apply(config: KSpecConfig) {
            order.add(this)
        }
        companion object {
            val order = LinkedList<Configuration>()
        }
    }

    @Test
    fun testOrder() {

        class TestConfiguration1: BaseConfiguration()
        class TestConfiguration2: BaseConfiguration()
        class TestConfiguration3: BaseConfiguration()


        @Configurations(TestConfiguration1::class, TestConfiguration2::class, TestConfiguration3::class)
        class AnnotationHolder

        val root = setupSpec {  }
        val runner = KSpecRunner(root, {}, Utils.findAnnotation(AnnotationHolder::class, Configurations::class))

        runner.run(RunNotifier())

        assertThat(BaseConfiguration.Companion.order[0] is TestConfiguration1, equalTo(true))
        assertThat(BaseConfiguration.Companion.order[1] is TestConfiguration2, equalTo(true))
        assertThat(BaseConfiguration.Companion.order[2] is TestConfiguration3, equalTo(true))

    }

}
