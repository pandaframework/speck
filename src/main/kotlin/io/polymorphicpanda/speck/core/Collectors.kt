package io.polymorphicpanda.speck.core

import io.polymorphicpanda.speck.dsl.Given
import io.polymorphicpanda.speck.dsl.Spec
import io.polymorphicpanda.speck.dsl.Then
import io.polymorphicpanda.speck.dsl.When
import java.util.*

internal interface Action<T> {
    fun description(): String
    fun execute(target: T)
}

internal abstract  class Collector<T> {
    val actions = LinkedList<Action<T>>()

    fun collect(description: String, init: T.() -> Unit) {
        actions.add(
                object: Action<T> {
                    override fun description(): String = "${getPrefix()} $description"

                    override fun execute(target: T) {
                        with(target, init)
                    }
                }
        )
    }

    fun iterate(each: (Action<T>) -> Unit) {
        val iterator = actions.iterator()
        while (iterator.hasNext()) {
            each(iterator.next());
            iterator.remove()
        }
    }

    abstract fun getPrefix(): String
}

internal class GivenCollector: Collector<Given>(), Spec {
    override fun getPrefix(): String = "Given"
    override fun Given(description: String, init: Given.() -> Unit) = collect(description, init)

}
internal class WhenCollector: Collector<When>(), Given {
    override fun getPrefix(): String = "When"
    override fun When(description: String, init: When.() -> Unit) = collect(description, init)
}

internal class ThenCollector: Collector<Then>(), When {
    override fun getPrefix(): String = "Then"
    override fun Then(description: String, init: Then.() -> Unit) = collect(description, init)

}