package io.polymorphicpanda.kspec.filter

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.*
import io.polymorphicpanda.kspec.Configuration
import io.polymorphicpanda.kspec.tag.Ignored
import io.polymorphicpanda.kspec.tag.Tag
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
object Filter: Configuration {

    override fun apply(config: KSpecConfig) {

        val match = config.filter.match

        if (match.isNotEmpty()) {
            var cache: MutableSet<Context>? = null

            config.around { context, chain ->
                if (cache == null) {
                    cache = LinkedHashSet<Context>(searchMatch(match, context as ExampleGroupContext, true))
                }

                if (cache!!.isNotEmpty()) {
                    if (cache!!.contains(context)) {
                        chain.next(context)
                    } else {
                        chain.stop("Not matching include filter")
                    }
                } else {
                    chain.next(context)
                }
            }
        }

        val includes = config.filter.includes

        if (includes.isNotEmpty()) {
            var cache: MutableSet<Context>? = null
            config.around { context, chain ->
                if (cache == null) {
                    cache = LinkedHashSet<Context>(searchMatch(includes, context as ExampleGroupContext, true))
                }
                if (cache!!.contains(context)) {
                    chain.next(context)
                } else {
                    chain.stop("Not matching include filter")
                }
            }
        }

        val excludes = config.filter.excludes

        if (excludes.isNotEmpty()) {
            var cache: MutableSet<Context>? = null
            config.around { context, chain ->
                if (cache == null) {
                    cache = LinkedHashSet<Context>(searchMatch(excludes, context as ExampleGroupContext))
                }
                if (!cache!!.contains(context)) {
                    chain.next(context)
                } else {
                    val ignored = context.tags.filterIsInstance(Ignored::class.java).firstOrNull()
                    if (ignored != null) {
                        chain.stop(ignored.reason)
                    } else {
                        chain.stop("Matching exclude filter")
                    }
                }
            }
        }

    }

    fun searchMatch(tags: Set<Tag>, root: ExampleGroupContext, implied: Boolean = false): Set<Context> {
        val check = object: ContextVisitor {
            val matches = HashSet<Context>()

            override fun preVisitExampleGroup(context: ExampleGroupContext) = doMatch(context)

            override fun postVisitExampleGroup(context: ExampleGroupContext) = ContextVisitResult.CONTINUE

            override fun onVisitExample(context: ExampleContext) = doMatch(context)

            private fun doMatch(context: Context): ContextVisitResult {
                val match = tags.any {
                    context.contains(it)
                }

                if (match) {
                    if (implied) {
                        addMatch(context)
                    } else {
                        matches.add(context)
                    }
                }

                return ContextVisitResult.CONTINUE
            }

            // recursively add this context and its parent to the match set
            private fun addMatch(context: Context) {
                when (context) {
                    is ExampleContext -> {
                        matches.add(context)
                        addMatch(context.parent)
                    }
                    is ExampleGroupContext -> {
                        matches.add(context)
                        if (context.parent != null) {
                            addMatch(context.parent)
                        }
                    }
                }
            }
        }

        root.visit(check)

        return check.matches
    }
}
