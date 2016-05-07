package io.polymorphicpanda.kspec.engine.query

import io.polymorphicpanda.kspec.context.Context
import io.polymorphicpanda.kspec.context.ExampleContext
import io.polymorphicpanda.kspec.context.ExampleGroupContext

/**
 * @author Ranie Jade Ramiso
 */
class Query private constructor(private val nodes: List<Node>) {

    fun matches(path: String): Boolean {
        val iterator = nodes.iterator()
        var current: Node = iterator.next()

        split(path).forEach {
            when {
                !current.terminal -> {
                    if (current.text == it) {
                        current = if (iterator.hasNext()) {
                            iterator.next()
                        } else {
                            Node.Wildcard()
                        }
                    }
                }
                else -> { }
            }
        }

        return current.terminal
    }

    companion object {
        fun parse(query: String): Query {
            val splits = split(query)
            val nodes = splits.mapIndexed { i, split ->
                when (split) {
                    "*" -> Node.Wildcard()
                    else -> Node.Equal(split)
                }
            }

            return Query(nodes)
        }

        fun transform(context: Context): String {
            return when(context) {
                is ExampleContext -> {
                    return "${transform(context.parent)}/${context.description}"
                }
                is ExampleGroupContext -> {
                    return if(context.parent != null) {
                        "${transform(context.parent!!)}/${context.description}"
                    } else {
                        context.description
                    }
                }
                else -> ""
            }
        }

        private fun split(query: String): List<String> = query.split("/")
    }
}
