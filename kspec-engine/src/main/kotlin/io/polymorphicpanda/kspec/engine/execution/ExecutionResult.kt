package io.polymorphicpanda.kspec.engine.execution

import io.polymorphicpanda.kspec.context.Context

/**
 * @author Ranie Jade Ramiso
 */
sealed class ExecutionResult(val context: Context) {
    class Success(context: Context): ExecutionResult(context)
    class Failure(context: Context, val cause: Throwable): ExecutionResult(context)

    companion object {
        fun success(context: Context) = Success(context)
        fun failure(context: Context, cause: Throwable) = Failure(context, cause)
    }
}
