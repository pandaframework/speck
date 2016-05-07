package io.polymorphicpanda.kspec.engine.query

/**
 * @author Ranie Jade Ramiso
 */
sealed class Node(val text: String, val terminal: Boolean) {
    class Equal(text: String): Node(text, false)
    class Wildcard(): Node("*", true)
}
