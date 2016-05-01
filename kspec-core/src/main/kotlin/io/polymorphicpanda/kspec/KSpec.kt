package io.polymorphicpanda.kspec

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext
import io.polymorphicpanda.kspec.tag.Tag
import kotlin.reflect.KClass

abstract class KSpec: Spec {
    val root: ExampleGroupContext = ExampleGroupContext(this.javaClass.name, null)

    var current = root

    var locked = false

    fun lock() {
        locked = true
    }

    abstract fun spec();

    open fun configure(config: KSpecConfig) {}

    override fun group(description: String, tags: Set<Tag>, block: () -> Unit) {
        invokeIfNotLocked {
            val context = ExampleGroupContext(description, current, tags)
            current = context
            block()
            current = current.parent ?: current
        }
    }

    override fun example(description: String, tags: Set<Tag>, block: () -> Unit) {
        invokeIfNotLocked {
            ExampleContext(description, current, block, tags)
        }
    }

    override fun before(action: () -> Unit) {
        invokeIfNotLocked {
            current.before = action
        }
    }

    override fun after(action: () -> Unit) {
        invokeIfNotLocked {
            current.after = action
        }
    }

    override fun beforeEach(action: () -> Unit) {
        invokeIfNotLocked {
            current.beforeEach = action
        }
    }

    override fun afterEach(action: () -> Unit) {
        invokeIfNotLocked {
            current.afterEach = action
        }
    }

    override fun <T: Any> group(subject: KClass<T>, description: String, tags: Set<Tag>,
                                block: SubjectSpec<T>.() -> Unit) {
        invokeIfNotLocked {
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

    internal fun invokeIfNotLocked(block: () -> Unit) {
        if (locked) {
            throw InvalidSpecException("You bad bad boy, this is not allowed here.")
        }
        block()
    }
}

class SubjectKSpec<T: Any>(val kspec: KSpec,
                           val context: ExampleGroupContext): SubjectSpec<T>, Spec by kspec {
    override fun subject(block: () -> T) {
        kspec.invokeIfNotLocked {
            context.subjectFactory = block
        }
    }

    override fun include(sharedExample: SharedExample<in T>) {
        kspec.invokeIfNotLocked {
            sharedExample.example().invoke(this)
        }
    }

    override val subject: T
        get() = context.subject()
}
