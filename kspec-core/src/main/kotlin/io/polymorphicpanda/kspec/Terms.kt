package io.polymorphicpanda.kspec

import io.polymorphicpanda.kspec.tag.Tag
import io.polymorphicpanda.kspec.focus
import io.polymorphicpanda.kspec.pending
import kotlin.reflect.KClass

fun Spec.describe(description: String, vararg tags: Tag, action: () -> Unit) {
    group("describe: $description", setOf(*tags),action)
}

fun <T: Any> Spec.describe(subject: KClass<T>, description: String = "%s",
                           vararg tags: Tag, action: SubjectSpec<T>.() -> Unit) {
    group(subject, "describe: $description", setOf(*tags), action)
}

fun Spec.context(description: String, vararg tags: Tag, action: () -> Unit) {
    group("context: $description", setOf(*tags), action)
}

fun <T: Any> Spec.context(subject: KClass<T>, description: String = "%s",
                          vararg tags: Tag, action: SubjectSpec<T>.() -> Unit) {
    group(subject, "context: $description", setOf(*tags), action)
}

fun Spec.fdescribe(description: String, vararg tags: Tag, action: () -> Unit) {
    group("describe: $description", setOf(focus(), *tags),action)
}

fun <T: Any> Spec.fdescribe(subject: KClass<T>, description: String = "%s",
                           vararg tags: Tag, action: SubjectSpec<T>.() -> Unit) {
    group(subject, "describe: $description", setOf(focus(), *tags), action)
}

fun Spec.fcontext(description: String, vararg tags: Tag, action: () -> Unit) {
    group("context: $description", setOf(focus(), *tags), action)
}

fun <T: Any> Spec.fcontext(subject: KClass<T>, description: String = "%s",
                          vararg tags: Tag, action: SubjectSpec<T>.() -> Unit) {
    group(subject, "context: $description", setOf(focus(), *tags), action)
}

fun Spec.xdescribe(description: String, reason: String? = null, action: () -> Unit) {
    group("describe: $description", setOf(pending(reason ?: "No reason provided")),action)
}

fun <T: Any> Spec.xdescribe(subject: KClass<T>, description: String = "%s",
                            reason: String? = null, action: SubjectSpec<T>.() -> Unit) {
    group(subject, "describe: $description", setOf(pending(reason ?: "No reason provided")), action)
}

fun Spec.xcontext(description: String, reason: String? = null, action: () -> Unit) {
    group("context: $description", setOf(pending(reason ?: "No reason provided")), action)
}

fun <T: Any> Spec.xcontext(subject: KClass<T>, description: String = "%s",
                           reason: String? = null, action: SubjectSpec<T>.() -> Unit) {
    group(subject, "context: $description", setOf(pending(reason ?: "No reason provided")), action)
}

fun Spec.it(description: String, vararg tags: Tag, action: () -> Unit) {
    example("it: $description", setOf(*tags), action)
}

fun Spec.fit(description: String, vararg tags: Tag, action: () -> Unit) {
    example("it: $description", setOf(focus(), *tags), action)
}

fun Spec.xit(description: String, reason: String? = null, block: (() -> Unit)? = null) {
    example("it: $description", setOf(pending(reason ?: "No reason provided")), block ?: {})
}

fun <T: Any> SubjectSpec<T>.itBehavesLike(sharedExample: SharedExample<in T>) {
    include(sharedExample)
}

