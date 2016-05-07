package io.polymorphicpanda.kspec.context

/**
 * @author Ranie Jade Ramiso
 */
enum class ContextVisitResult {
    CONTINUE,
    SKIP_SUBTREE,
    REMOVE,
    TERMINATE
}
