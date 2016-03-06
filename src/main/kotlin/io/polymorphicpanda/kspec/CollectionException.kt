package io.polymorphicpanda.kspec

/**
 * @author Ranie Jade Ramiso
 */
class CollectionException(message: String?, cause: Throwable?): Throwable(message, cause) {
    constructor(message: String): this(message, null)
    constructor(cause: Throwable): this(null, cause)
}
