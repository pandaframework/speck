package io.polymorphicpanda.kspec.filter

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.*
import io.polymorphicpanda.kspec.extension.Extension
import io.polymorphicpanda.kspec.tag.Ignored
import io.polymorphicpanda.kspec.tag.Tag

/**
 * @author Ranie Jade Ramiso
 */
object Filter: Extension {

    override fun configure(config: KSpecConfig, root: ExampleGroupContext) {

        val match = config.filter.match

        if (match.isNotEmpty() && hasMatch(match, root)) {
            config.around { context, chain ->
                if (context.contains(match) || (context is ExampleGroupContext && hasMatch(match, context))) {
                    chain.next(context)
                } else {
                    chain.stop("Not matching include filter")
                }
            }
        }

        val includes = config.filter.includes

        if (includes.isNotEmpty()) {
            config.around { context, chain ->
                if (context.contains(includes) || (context is ExampleGroupContext && hasMatch(includes, context))) {
                    chain.next(context)
                } else {
                    chain.stop("Not matching include filter")
                }
            }
        }

        val excludes = config.filter.excludes

        if (excludes.isNotEmpty()) {
            config.around { context, chain ->
                if (!context.contains(excludes) || (context is ExampleGroupContext && hasMatch(excludes, context))) {
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

    fun hasMatch(tags: Set<Tag>, root: ExampleGroupContext): Boolean {
        val check = object: ContextVisitor {
            var match = false
            override fun preVisitExampleGroup(context: ExampleGroupContext) = hasMatch(context)

            override fun postVisitExampleGroup(context: ExampleGroupContext) = ContextVisitResult.CONTINUE

            override fun onVisitExample(context: ExampleContext) = hasMatch(context)

            private fun hasMatch(context: Context): ContextVisitResult {
                match = tags.any {
                    context.contains(it)
                }

                if (match) {
                    return ContextVisitResult.TERMINATE
                }
                return ContextVisitResult.CONTINUE
            }
        }

        root.visit(check)

        return check.match
    }
}
