package io.polymorphicpanda.kspec

import io.polymorphicpanda.kspec.tag.Tag
import kotlin.reflect.KClass

interface Spec {
    fun group(description: String, tags: Set<Tag>, block: () -> Unit)
    fun <T: Any> group(subject: KClass<T>, description: String = "", tags: Set<Tag>, block: SubjectSpec<T>.() -> Unit)
    fun example(description: String, tags: Set<Tag>, block: () -> Unit)
    fun before(action: () -> Unit)
    fun after(action: () -> Unit)
    fun beforeEach(action: () -> Unit)
    fun afterEach(action: () -> Unit)
}

interface SubjectSpec<T: Any>: Spec {
    val subject: T
    fun subject(block: () -> T)
    fun include(sharedExample: SharedExample<in T>)
}

interface SharedExample<T: Any> {
    fun example(): SubjectSpec<out T>.() -> Unit
}

fun <T: Any> sharedExample(block: SubjectSpec<out T>.() -> Unit): SharedExample<T> {
    return object: SharedExample<T> {
        override fun example(): SubjectSpec<out T>.() -> Unit = block
    }
}
