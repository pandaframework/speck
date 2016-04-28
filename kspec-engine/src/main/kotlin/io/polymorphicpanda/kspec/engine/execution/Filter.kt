package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.config.KSpecConfig
import io.polymorphicpanda.kspec.context.*
import io.polymorphicpanda.kspec.tag.Tag
import java.util.*

/**
 * @author Ranie Jade Ramiso
 */
class Filter(root: ExampleGroupContext, val config: KSpecConfig) {

    private val includes = HashSet<Context>()
    private val matches = HashSet<Context>()
    private val excludes = HashSet<Context>()

    init {
        config.filter.apply {
            search(includes, match, excludes, root)
        }
    }

    fun hasAnyMatch() = matches.isNotEmpty()

    fun matchesIncludeFilter(context: Context) = includes.contains(context)
    fun matchesMatchFilter(context: Context) = matches.contains(context)
    fun matchesExcludeFilter(context: Context) = excludes.contains(context)

    private fun search(includes: Set<Tag>, matches: Set<Tag>,
                       excludes: Set<Tag>, root: ExampleGroupContext) {
        val checker = object: ContextVisitor {
            override fun preVisitExampleGroup(context: ExampleGroupContext) = doMatch(context)

            override fun postVisitExampleGroup(context: ExampleGroupContext) = ContextVisitResult.CONTINUE

            override fun onVisitExample(context: ExampleContext) = doMatch(context)

            private fun doMatch(context: Context): ContextVisitResult {

                includes.any { context.contains(it) }.apply {
                    if (this) {
                        addMatch(context, true, this@Filter.includes)
                    }
                }

                matches.any { context.contains(it) }.apply {
                    if (this) {
                        addMatch(context, true, this@Filter.matches)
                    }
                }

                excludes.any { context.contains(it) }.apply {
                    if (this) {
                        addMatch(context, false, this@Filter.excludes)
                    }
                }

                return ContextVisitResult.CONTINUE
            }

            private fun addMatch(context: Context, implied: Boolean, target: HashSet<Context>) {
                if (implied) {
                    recursivelyAddMatch(context, target)
                } else {
                    target.add(context)
                }
            }

            // recursively add this context and its parent to the match set
            private fun recursivelyAddMatch(context: Context, target: HashSet<Context>) {
                when (context) {
                    is ExampleContext -> {
                        target.add(context)
                        recursivelyAddMatch(context.parent, target)
                    }
                    is ExampleGroupContext -> {
                        target.add(context)
                        if (context.parent != null) {
                            recursivelyAddMatch(context.parent!!, target)
                        }
                    }
                }
            }
        }

        root.visit(checker)
    }
}
