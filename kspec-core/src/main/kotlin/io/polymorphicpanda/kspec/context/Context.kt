package io.polymorphicpanda.kspec.context

import io.polymorphicpanda.kspec.helpers.MemoizedHelper
import io.polymorphicpanda.kspec.tag.Tag
import io.polymorphicpanda.kspec.tag.Taggable
import java.util.*

open class Context(val description: String, parent: Taggable?, tags: Set<Tag>): Taggable(parent, tags)

class ExampleGroupContext(description: String,
                          val parent: ExampleGroupContext?,
                          tags: Set<Tag> = setOf<Tag>(),
                          var subjectFactory: () -> Any = { throw UnsupportedOperationException() })
    : Context(description, parent, tags) {
    val children = LinkedList<Context>()

    private var memoizedSubject = MemoizedHelper(subjectFactory)

    private val subjectInstance by memoizedSubject

    var before: (() -> Unit)? = null
    var after: (() -> Unit)? = null

    var beforeEach: (() -> Unit)? = null
    var afterEach: (() -> Unit)? = null

    init {
        if (parent != null) {
            parent.children.add(this)
        }
    }

    fun visit(visitor: ContextVisitor) {
        doVisit(visitor, this)
    }


    fun <T> subject() = subjectInstance as T

    internal fun reset() {
        parent?.reset()
        memoizedSubject.forget()
    }

    companion object {
        private fun doVisit(visitor: ContextVisitor, context: Context): ContextVisitResult {
            when(context) {
                is ExampleGroupContext -> {
                    val result = visitor.preVisitExampleGroup(context)

                    if(result == ContextVisitResult.CONTINUE) {
                        val iterator = context.children.iterator()
                        while (iterator.hasNext()) {
                            val visitResult = doVisit(visitor, iterator.next())
                            if (visitResult == ContextVisitResult.TERMINATE) {
                                return ContextVisitResult.TERMINATE
                            } else if (visitResult == ContextVisitResult.REMOVE) {
                                iterator.remove()
                            }
                        }
                    }

                    if (result != ContextVisitResult.TERMINATE) {
                        return visitor.postVisitExampleGroup(context)
                    }

                    return result
                }
            }
            return visitor.onVisitExample(context as ExampleContext)
        }
    }
}

class ExampleContext(description: String, val parent: ExampleGroupContext,
                     val action: (() -> Unit)?, tags: Set<Tag> = setOf<Tag>())
    : Context(description, parent, tags) {

    init {
        parent.children.add(this)
    }

    operator fun invoke() {
        parent.reset()
        action!!()
    }
}
