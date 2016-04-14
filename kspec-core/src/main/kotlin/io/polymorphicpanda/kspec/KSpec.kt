package io.polymorphicpanda.kspec

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.tag.Tag
import kotlin.reflect.KClass

abstract class KSpec: Spec {
    val root: ExampleGroupContext = ExampleGroupContext(this.javaClass.name, null)

    var current = root

    var disabled = false

    fun disable() {
        disabled = true
    }

    abstract fun spec();

    open fun configure(config: KSpecConfig) {}

    override fun group(description: String, tags: Set<Tag>, block: () -> Unit) {
        invokeIfNotDisabled {
            val context = ExampleGroupContext(description, current, tags)
            current = context
            block()
            current = current.parent ?: current
        }
    }

    override fun example(description: String, tags: Set<Tag>, block: () -> Unit) {
        invokeIfNotDisabled {
            ExampleContext(description, current, block, tags)
        }
    }

    override fun before(action: () -> Unit) {
        invokeIfNotDisabled {
            current.before = action
        }
    }

    override fun after(action: () -> Unit) {
        invokeIfNotDisabled {
            current.after = action
        }
    }

    override fun beforeEach(action: () -> Unit) {
        invokeIfNotDisabled {
            current.beforeEach = action
        }
    }

    override fun afterEach(action: () -> Unit) {
        invokeIfNotDisabled {
            current.afterEach = action
        }
    }

    override fun <T: Any> group(subject: KClass<T>, description: String, tags: Set<Tag>,
                                block: SubjectSpec<T>.() -> Unit) {
        invokeIfNotDisabled {
            val context = ExampleGroupContext(description.format(subject), current, tags, {
                val constructor =
                        subject.constructors.firstOrNull { it.parameters.isEmpty() } ?: throw InstantiationException()
                constructor.call()
            })
            current = context
            SubjectKSpec<T>(this, context).apply(block)
            current = current.parent ?: current
        }
    }

    internal fun invokeIfNotDisabled(block: () -> Unit) {
        if (disabled) {
            throw CollectionException("You bad bad boy, this is not allowed here.")
        }
        block()
    }
}

class SubjectKSpec<T: Any>(val kspec: KSpec,
                           val context: ExampleGroupContext): SubjectSpec<T>, Spec by kspec {
    override fun subject(block: () -> T) {
        kspec.invokeIfNotDisabled {
            context.subjectFactory = block
        }
    }

    override fun include(sharedExample: SharedExample<in T>) {
        kspec.invokeIfNotDisabled {
            sharedExample.example().invoke(this)
        }
    }

    override val subject: T
        get() = context.subject()
}
